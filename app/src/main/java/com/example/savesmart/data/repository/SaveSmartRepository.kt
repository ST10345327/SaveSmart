package com.example.savesmart.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
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
import java.util.Calendar

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

    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    suspend fun registerUser(username: String, passwordHash: String): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser != null) return false
        
        val newUser = User(username = username, passwordHash = passwordHash, fullName = username)
        val userId = userDao.insertUser(newUser)
        return userId > 0
    }

    suspend fun loginUser(username: String, passwordHash: String): User? {
        return userDao.getUserByCredentials(username, passwordHash)
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
     */
    suspend fun awardPoints(userId: Int, points: Int) {
        Log.d(TAG, "awardPoints: Adding $points points to user $userId")
        val user = userDao.getUserById(userId)
        user?.let {
            val newPoints = it.totalPoints + points
            // Simple leveling logic (R21): 1000 points per level
            val newLevel = (newPoints / 1000) + 1
            userDao.updateUser(it.copy(totalPoints = newPoints, level = newLevel))
        }
    }

    // ... (rest of the methods remain the same)
    
    fun getCategoriesForUserLive(userId: Int): LiveData<List<Category>> = categoryDao.getCategoriesForUserLive(userId)
    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(categoryId: Int) = categoryDao.softDeleteCategory(categoryId)
    
    suspend fun insertExpense(expense: Expense): Long {
        Log.d(TAG, "insertExpense(): description=${expense.description}, amount=${expense.amountMilliunits}")
        val id = expenseDao.insertExpense(expense)
        if (id > 0) {
            // R19: Award 10 points for every expense added
            awardPoints(expense.userId, 10)
            
            // Check for badges (Requirement R20)
            checkForBadges(expense.userId)
        }
        return id
    }

    /**
     * Requirement R20: Check and award badges based on user behavior.
     */
    private suspend fun checkForBadges(userId: Int) {
        Log.d(TAG, "checkForBadges: Checking for user $userId")
        
        // 1. "First Save" Badge
        awardBadgeIfConditionsMet(userId, "FIRST_SAVE") { true }
        
        // 2. "Quick Logger" Badge (10 expenses in one day)
        awardBadgeIfConditionsMet(userId, "QUICK_LOGGER") {
            val today = System.currentTimeMillis()
            val startOfDay = today - (today % (24 * 60 * 60 * 1000))
            val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
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
        
        // 5. "Goal Crusher" Badge (Stayed within limits for current month so far)
        awardBadgeIfConditionsMet(userId, "GOAL_CRUSHER") {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val start = cal.timeInMillis
            val end = System.currentTimeMillis()
            
            val summaries = categoryDao.getCategoriesWithSpending(userId, start, end)
            summaries.isNotEmpty() && summaries.all { 
                it.maxGoalMilliunits == null || it.totalMilliunits <= it.maxGoalMilliunits 
            }
        }
    }

    private suspend fun awardBadgeIfConditionsMet(userId: Int, badgeKey: String, condition: suspend () -> Boolean) {
        if (!badgeDao.hasBadgeBeenEarned(userId, badgeKey)) {
            if (condition()) {
                val badge = badgeDao.getBadgeByKey(badgeKey)
                badge?.let {
                    badgeDao.awardBadge(UserBadge(userId, it.badgeId))
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
        val start = today.timeInMillis - (days.toLong() * 24 * 60 * 60 * 1000)
        
        val distinctDays = expenseDao.getDistinctLoggingDays(userId, start, end)
        return distinctDays >= days
    }

    fun getExpensesInRangeLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<Expense>> = expenseDao.getExpensesInRangeLive(userId, startMillis, endMillis)
    suspend fun deleteExpense(expenseId: Int) = expenseDao.softDeleteExpense(expenseId)

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
