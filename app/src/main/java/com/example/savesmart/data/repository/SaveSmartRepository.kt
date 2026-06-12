package com.example.savesmart.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.example.savesmart.data.dao.BadgeDao
import com.example.savesmart.data.dao.CategoryDao
import com.example.savesmart.data.dao.ExpenseDao
import com.example.savesmart.data.dao.UserDao
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.entity.Expense
import com.example.savesmart.data.entity.User
import com.example.savesmart.data.entity.UserBadge
import com.example.savesmart.ui.dashboard.CategoryWithSpending
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap

/**
 * SaveSmartRepository — Single source of truth for all data operations.
 * Implements logic for Authentication, Expenses, Categories, and Gamification.
 */
class SaveSmartRepository(
    private val database: com.example.savesmart.data.database.SaveSmartDatabase
) {
    private val userDao = database.userDao()
    private val badgeDao = database.badgeDao()
    private val categoryDao = database.categoryDao()
    private val expenseDao = database.expenseDao()

    private val TAG = "SaveSmartRepository"
    
    // Idempotency guards to prevent duplicate submissions (R22/R23 Hardening)
    private val activityMutex = Mutex()
    private val recentSubmissions = ConcurrentHashMap<String, Long>()
    private val IDEMPOTENCY_WINDOW_MS = 5000L

    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    /**
     * Requirement R01 Hardening: Atomic registration with duplicate prevention.
     */
    suspend fun registerUser(username: String, passwordHash: String): Boolean = database.withTransaction {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser != null) return@withTransaction false
        
        val newUser = User(username = username, passwordHash = passwordHash, fullName = username)
        try {
            val userId = userDao.insertUser(newUser)
            return@withTransaction userId > 0
        } catch (e: Exception) {
            Log.e(TAG, "registerUser: Constraint violation or failure", e)
            false
        }
    }

    suspend fun loginUser(username: String, passwordHash: String): User? {
        return userDao.getUserByCredentials(username, passwordHash)
    }

    suspend fun updateOnboardingStep(userId: Int, step: Int) {
        userDao.updateOnboardingStep(userId, step)
    }

    fun getUserLive(userId: Int): LiveData<User?> = userDao.getUserByIdLive(userId)

    suspend fun getUserById(userId: Int): User? = userDao.getUserById(userId)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    /**
     * Requirement R22: Get all users ranked by points.
     */
    fun getAllUsersRankedLive(): LiveData<List<User>> = userDao.getAllUsersRankedLive()

    // ────────────────────────────────────────────────────────────────────────
    // GAMIFICATION (R19, R20, R21)
    // ────────────────────────────────────────────────────────────────────────

    fun getEarnedBadgesLive(userId: Int): LiveData<List<Badge>> {
        return badgeDao.getEarnedBadgesLive(userId)
    }

    /**
     * Requirement R19: Award points to the user.
     * Guaranteed atomic via UserDao @Transaction.
     */
    suspend fun awardPoints(userId: Int, points: Int) {
        Log.d(TAG, "awardPoints: Adding $points points to user $userId")
        userDao.addPointsAndLevelUp(userId, points)
    }

    suspend fun getExpenseById(expenseId: Int): Expense? = expenseDao.getExpenseById(expenseId)

    fun getCategoriesForUserLive(userId: Int): LiveData<List<Category>> = categoryDao.getCategoriesForUserLive(userId)

    /**
     * Requirement R05 Hardening: Atomic category creation with point award.
     */
    suspend fun insertCategory(category: Category): Long = database.withTransaction {
        // Idempotency Check
        val key = "CAT_${category.userId}_${category.name.hashCode()}"
        if (isDuplicateSubmission(key)) return@withTransaction -1L

        Log.d(TAG, "insertCategory: ${category.name} for user ${category.userId}")
        val id = categoryDao.insertCategory(category)
        if (id > 0) {
            // R19: Award 25 points for setting up a category
            awardPoints(category.userId, 25)
        }
        id
    }

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    /**
     * Requirement R07 Hardening: Atomic delete with point deduction.
     */
    suspend fun deleteCategory(categoryId: Int) = database.withTransaction {
        val category = categoryDao.getCategoryById(categoryId)
        category?.let {
            Log.d(TAG, "deleteCategory: Deducting 25 points from user ${it.userId}")
            userDao.addPointsAndLevelUp(it.userId, -25)
            categoryDao.softDeleteCategory(categoryId)
        }
    }
    
    /**
     * Requirement R08 Hardening: Atomic expense creation with point award and badge check.
     */
    suspend fun insertExpense(expense: Expense): Long = database.withTransaction {
        // Idempotency Check (Prevent rapid double-taps)
        val key = "EXP_${expense.userId}_${expense.amountMilliunits}_${expense.dateMillis}_${expense.description.hashCode()}"
        if (isDuplicateSubmission(key)) return@withTransaction -1L

        Log.d(TAG, "insertExpense(): user=${expense.userId}, amount=${expense.amountMilliunits}")
        val id = expenseDao.insertExpense(expense)
        if (id > 0) {
            // R19: Award 10 points for every expense added
            awardPoints(expense.userId, 10)
            
            // Check for badges (Requirement R20)
            checkForBadgesInternal(expense.userId, expense.dateMillis)
        }
        id
    }

    private fun isDuplicateSubmission(key: String): Boolean {
        val now = System.currentTimeMillis()
        val lastTime = recentSubmissions[key]
        if (lastTime != null && (now - lastTime) < IDEMPOTENCY_WINDOW_MS) {
            Log.w(TAG, "Duplicate submission blocked: $key")
            return true
        }
        recentSubmissions[key] = now
        // Cleanup old keys periodically (primitive)
        if (recentSubmissions.size > 100) recentSubmissions.clear() 
        return false
    }

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    /**
     * Requirement R20 Hardening: Badge logic moved inside transaction context.
     */
    private suspend fun checkForBadgesInternal(userId: Int, expenseDateMillis: Long) {
        Log.d(TAG, "checkForBadges: Checking for user $userId, date=$expenseDateMillis")
        
        // 1. "First Save" Badge
        awardBadgeIfConditionsMet(userId, "FIRST_SAVE") { true }
        
        // 2. "Quick Logger" Badge
        awardBadgeIfConditionsMet(userId, "QUICK_LOGGER") {
            val cal = Calendar.getInstance()
            cal.timeInMillis = expenseDateMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfDay = cal.timeInMillis
            
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = cal.timeInMillis

            expenseDao.getExpenseCountForDay(userId, startOfDay, endOfDay) >= 10
        }

        // 3. "7-Day Streak" Badge
        awardBadgeIfConditionsMet(userId, "STREAK_7") {
            checkLoggingStreak(userId, 7)
        }

        // 4. "30-Day Streak" Badge
        awardBadgeIfConditionsMet(userId, "STREAK_30") {
            checkLoggingStreak(userId, 30)
        }
        
        // 5. "Goal Crusher" Badge
        awardBadgeIfConditionsMet(userId, "GOAL_CRUSHER") {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val start = cal.timeInMillis
            
            cal.timeInMillis = System.currentTimeMillis()
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            val end = cal.timeInMillis
            
            val summaries = categoryDao.getCategoriesWithSpending(userId, start, end)
            val hasExpenses = summaries.any { it.totalMilliunits > 0 }
            hasExpenses && summaries.all {
                it.maxGoalMilliunits == null || it.totalMilliunits <= it.maxGoalMilliunits 
            }
        }
    }

    /**
     * Atomic badge award to prevent race conditions (Requirement R20).
     */
    private suspend fun awardBadgeIfConditionsMet(userId: Int, badgeKey: String, condition: suspend () -> Boolean) {
        // Double-Check Locking pattern with DB state
        if (badgeDao.hasBadgeBeenEarned(userId, badgeKey)) return

        if (condition()) {
            val badge = badgeDao.getBadgeByKey(badgeKey)
            badge?.let {
                // Primary key constraint in user_badges handles atomic award
                val resultId = badgeDao.awardBadge(UserBadge(userId, it.badgeId))
                if (resultId != -1L) {
                    awardPoints(userId, it.pointsReward)
                    Log.d(TAG, "Awarded $badgeKey badge to user $userId")
                }
            }
        }
    }

    private suspend fun checkLoggingStreak(userId: Int, days: Int): Boolean {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        
        val end = today.timeInMillis + (24 * 60 * 60 * 1000)
        val start = today.timeInMillis - ((days - 1).toLong() * 24 * 60 * 60 * 1000)
        
        val distinctDays = expenseDao.getDistinctLoggingDays(userId, start, end)
        return distinctDays >= days
    }

    fun getExpensesInRangeLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<Expense>> = expenseDao.getExpensesInRangeLive(userId, startMillis, endMillis)
    
    fun getExpensesWithCategoryLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<com.example.savesmart.data.dao.ExpenseWithCategory>> = 
        expenseDao.getExpensesWithCategoryLive(userId, startMillis, endMillis)

    /**
     * Requirement R12 Hardening: Atomic delete with point deduction to prevent farming.
     */
    suspend fun deleteExpense(expenseId: Int) = database.withTransaction {
        val expense = expenseDao.getExpenseById(expenseId)
        expense?.let {
            Log.d(TAG, "deleteExpense: Deducting 10 points from user ${it.userId}")
            userDao.addPointsAndLevelUp(it.userId, -10)
            expenseDao.softDeleteExpense(expenseId)
        }
    }

    suspend fun getTotalMonthlySpending(userId: Int, startMillis: Long, endMillis: Long): Long {
        return expenseDao.getTotalSpendingForUser(userId, startMillis, endMillis)
    }

    /**
     * Optimized category spending summary (Performance T08).
     * Now uses a single SQL JOIN instead of multiple queries in a loop.
     */
    suspend fun getCategoriesWithSpending(userId: Int, startMillis: Long, endMillis: Long): List<CategoryWithSpending> {
        return categoryDao.getCategoriesWithSpending(userId, startMillis, endMillis)
    }

    /**
     * Requirement R18: Get daily spending for a user within a range.
     */
    suspend fun getDailySpending(userId: Int, startMillis: Long, endMillis: Long) = 
        expenseDao.getDailySpending(userId, startMillis, endMillis)
}
