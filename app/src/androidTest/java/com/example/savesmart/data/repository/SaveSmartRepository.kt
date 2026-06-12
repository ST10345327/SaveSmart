package com.savesmart.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.savesmart.data.dao.BadgeDao
import com.savesmart.data.dao.CategoryDao
import com.savesmart.data.dao.CategorySpendingSummary
import com.savesmart.data.dao.DailySpending
import com.savesmart.data.dao.ExpenseDao
import com.savesmart.data.dao.UserDao
import com.savesmart.data.entity.Badge
import com.savesmart.data.entity.Category
import com.savesmart.data.entity.Expense
import com.savesmart.data.entity.User
import com.savesmart.data.entity.UserBadge

/**
 * SaveSmartRepository — Single source of truth for all data operations.
 *
 * References:
 * - Android Developers (2024) Guide to app architecture. Google LLC.
 *   Available at: https://developer.android.com/topic/architecture
 *   (Accessed: 24 March 2026).
 * - Android Developers (2024) Data layer. Google LLC.
 *   Available at: https://developer.android.com/topic/architecture/data-layer
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - Repository abstracts all DAO calls from ViewModels (MVVM architecture, T01).
 * - All suspend functions run on the caller's coroutine — ViewModels use
 *   viewModelScope to call these safely off the main thread.
 * - Gamification logic (points, badges) is handled here to keep ViewModels lean.
 * - Milliunit convention used throughout (T10): 1_000L milliunits = R1.00
 *
 * Gamification point values (R19):
 * - Log any expense:                +5 pts
 * - Stay under category limit:      +25 pts
 * - 7-day logging streak:           +50 pts
 * - Achieve monthly budget goal:    +100 pts
 * - 30-day logging streak:          +200 pts
 */
class SaveSmartRepository(
    private val userDao: UserDao,
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    private val badgeDao: BadgeDao
) {

    private val TAG = "SaveSmartRepository"

    // ══════════════════════════════════════════════════════════════════════════
    // USER OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Registers a new user (R01).
     * Returns the new user's row ID, or -1 if username is already taken (R03).
     *
     * @param user The User entity to register.
     * @return New user ID on success, -1 on duplicate username.
     */
    suspend fun registerUser(user: User): Long {
        Log.d(TAG, "Registering user: ${user.username}")
        return userDao.insertUser(user)
    }

    /**
     * Authenticates a user with username and password hash (R02).
     * Returns null if credentials are invalid.
     *
     * @param username The entered username.
     * @param passwordHash SHA-256 hash of the entered password.
     * @return Authenticated User or null.
     */
    suspend fun loginUser(username: String, passwordHash: String): User? {
        Log.d(TAG, "Login attempt for username: $username")
        return userDao.getUserByCredentials(username, passwordHash)
    }

    /**
     * Checks if a username is already taken (R03).
     *
     * @param username The username to check.
     * @return True if the username is already registered.
     */
    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    /**
     * Returns a user by ID as LiveData (dashboard header, R15).
     *
     * @param userId The user's primary key.
     * @return LiveData wrapping the User.
     */
    fun getUserLive(userId: Int): LiveData<User?> =
        userDao.getUserByIdLive(userId)

    /**
     * Returns all users ranked by total points for the leaderboard (R22).
     *
     * @return LiveData list of users ordered by points descending.
     */
    fun getLeaderboardLive(): LiveData<List<User>> =
        userDao.getAllUsersRankedLive()

    /**
     * Marks the user's onboarding as complete (R23).
     * Called after the user finishes or skips the 3-step onboarding flow.
     *
     * @param userId The user's primary key.
     */
    suspend fun completeOnboarding(userId: Int) {
        Log.d(TAG, "Marking onboarding complete for userId: $userId")
        userDao.markOnboardingComplete(userId)
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CATEGORY OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Creates a new expense category (R05).
     *
     * @param category The Category entity to create.
     * @return New category row ID.
     */
    suspend fun createCategory(category: Category): Long {
        Log.d(TAG, "Creating category: ${category.name} for userId: ${category.userId}")
        return categoryDao.insertCategory(category)
    }

    /**
     * Updates an existing category (R06).
     *
     * @param category The updated Category entity.
     */
    suspend fun updateCategory(category: Category) {
        Log.d(TAG, "Updating category: ${category.categoryId}")
        categoryDao.updateCategory(category)
    }

    /**
     * Soft-deletes a category (R07).
     * Associated expenses are preserved — category_id set to null via CASCADE.
     *
     * @param categoryId The category's primary key.
     */
    suspend fun deleteCategory(categoryId: Int) {
        Log.d(TAG, "Soft-deleting category: $categoryId")
        categoryDao.softDeleteCategory(categoryId)
    }

    /**
     * Returns all active categories for a user as LiveData (R05, R15).
     *
     * @param userId The user's primary key.
     * @return LiveData list of active categories.
     */
    fun getCategoriesLive(userId: Int): LiveData<List<Category>> =
        categoryDao.getCategoriesForUserLive(userId)

    /**
     * Returns all active categories as a plain list (R08).
     * Used to populate category selector chips on the Add Expense screen.
     *
     * @param userId The user's primary key.
     * @return List of active categories.
     */
    suspend fun getCategories(userId: Int): List<Category> =
        categoryDao.getCategoriesForUser(userId)

    /**
     * Returns category spending summary for the report screen (R17).
     *
     * @param userId The user's primary key.
     * @param startMillis Period start in epoch milliseconds.
     * @param endMillis Period end in epoch milliseconds.
     * @return LiveData list of CategorySpendingSummary.
     */
    fun getCategorySpendingSummaryLive(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<CategorySpendingSummary>> =
        categoryDao.getCategorySpendingSummaryLive(userId, startMillis, endMillis)

    // ══════════════════════════════════════════════════════════════════════════
    // EXPENSE OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Saves a new expense entry and awards +5 points (R08, R19).
     * Also checks badge conditions after saving.
     *
     * @param expense The Expense entity to save.
     * @return New expense row ID.
     */
    suspend fun saveExpense(expense: Expense): Long {
        Log.d(TAG, "Saving expense: ${expense.description} amount: ${expense.amountMilliunits}")
        val expenseId = expenseDao.insertExpense(expense)

        // Award +5 points for logging any expense (R19)
        if (expenseId > 0) {
            userDao.addPoints(expense.userId, 5)
            Log.d(TAG, "+5 points awarded for logging expense")

            // Check badge conditions after saving (R20)
            checkAndAwardBadges(expense.userId, expense.dateMillis)
        }
        return expenseId
    }

    /**
     * Updates an existing expense entry (R12).
     *
     * @param expense The updated Expense entity.
     */
    suspend fun updateExpense(expense: Expense) {
        Log.d(TAG, "Updating expense: ${expense.expenseId}")
        expenseDao.updateExpense(expense)
    }

    /**
     * Soft-deletes an expense entry (R12).
     *
     * @param expenseId The expense's primary key.
     */
    suspend fun deleteExpense(expenseId: Int) {
        Log.d(TAG, "Soft-deleting expense: $expenseId")
        expenseDao.softDeleteExpense(expenseId)
    }

    /**
     * Returns expenses in a date range as LiveData (R10).
     *
     * @param userId The user's primary key.
     * @param startMillis Period start in epoch milliseconds.
     * @param endMillis Period end in epoch milliseconds.
     * @return LiveData list of expenses.
     */
    fun getExpensesInRangeLive(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<Expense>> =
        expenseDao.getExpensesInRangeLive(userId, startMillis, endMillis)

    /**
     * Returns an expense by ID for editing (R12).
     *
     * @param expenseId The expense's primary key.
     * @return The Expense entity or null.
     */
    suspend fun getExpenseById(expenseId: Int): Expense? =
        expenseDao.getExpenseById(expenseId)

    /**
     * Returns total spending in a period for the dashboard (R15).
     *
     * @param userId The user's primary key.
     * @param startMillis Period start in epoch milliseconds.
     * @param endMillis Period end in epoch milliseconds.
     * @return Total spending in milliunits.
     */
    suspend fun getTotalSpending(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): Long = expenseDao.getTotalSpendingInRange(userId, startMillis, endMillis)

    /**
     * Returns daily spending totals for the spending graph (R18).
     *
     * @param userId The user's primary key.
     * @param startMillis Period start in epoch milliseconds.
     * @param endMillis Period end in epoch milliseconds.
     * @return List of DailySpending for bar chart rendering.
     */
    suspend fun getDailySpending(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): List<DailySpending> =
        expenseDao.getDailySpending(userId, startMillis, endMillis)

    // ══════════════════════════════════════════════════════════════════════════
    // GAMIFICATION — BADGE OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns all badges with their earned status for a user (R20).
     * Used to build the Rewards screen badge grid with earned/locked states.
     *
     * @param userId The user's primary key.
     * @return List of BadgeWithStatus objects.
     */
    suspend fun getBadgesWithStatus(userId: Int): List<BadgeWithStatus> {
        val allBadges = badgeDao.getAllBadges()
        val earnedIds = badgeDao.getEarnedBadgeIds(userId).toSet()
        return allBadges.map { badge ->
            BadgeWithStatus(badge, isEarned = badge.badgeId in earnedIds)
        }
    }

    /**
     * Returns earned badges as LiveData for the Rewards screen (R20).
     *
     * @param userId The user's primary key.
     * @return LiveData list of earned Badge objects.
     */
    fun getEarnedBadgesLive(userId: Int): LiveData<List<Badge>> =
        badgeDao.getEarnedBadgesLive(userId)

    /**
     * Checks and awards applicable badges after an expense is logged (R20).
     * Called internally after saveExpense().
     *
     * Badge conditions checked:
     * - FIRST_SAVE: first expense ever logged
     * - QUICK_LOGGER: 10 or more expenses logged today
     * - STREAK_7: 7 consecutive logging days
     * - STREAK_30: 30 consecutive logging days
     *
     * @param userId The user's primary key.
     * @param todayMillis Epoch milliseconds for today's date.
     */
    private suspend fun checkAndAwardBadges(userId: Int, todayMillis: Long) {
        Log.d(TAG, "Checking badge conditions for userId: $userId")

        // FIRST_SAVE: Check if this is the user's very first expense (R20)
        val totalCount = expenseDao.getTotalExpenseCount(userId)
        if (totalCount == 1) {
            awardBadgeIfNotEarned(userId, "FIRST_SAVE")
        }

        // QUICK_LOGGER: Check if 10+ expenses logged today (R20)
        val startOfDay = getStartOfDay(todayMillis)
        val endOfDay   = getEndOfDay(todayMillis)
        val todayCount = expenseDao.getExpenseCountForDay(userId, startOfDay, endOfDay)
        if (todayCount >= 10) {
            awardBadgeIfNotEarned(userId, "QUICK_LOGGER")
        }

        // STREAK_7: Check if 7 consecutive days logged (R20)
        val streak7Start = todayMillis - (6L * 24 * 60 * 60 * 1000)
        val streak7Days  = expenseDao.getDistinctLoggingDays(userId, streak7Start, todayMillis)
        if (streak7Days >= 7) {
            val pointsAwarded = awardBadgeIfNotEarned(userId, "STREAK_7")
            if (pointsAwarded > 0) userDao.addPoints(userId, 50) // +50 streak bonus (R19)
        }

        // STREAK_30: Check if 30 consecutive days logged (R20)
        val streak30Start = todayMillis - (29L * 24 * 60 * 60 * 1000)
        val streak30Days  = expenseDao.getDistinctLoggingDays(userId, streak30Start, todayMillis)
        if (streak30Days >= 30) {
            val pointsAwarded = awardBadgeIfNotEarned(userId, "STREAK_30")
            if (pointsAwarded > 0) userDao.addPoints(userId, 200) // +200 streak bonus (R19)
        }
    }

    /**
     * Awards a badge to a user if they have not already earned it (R20).
     * Returns the badge's pointsReward if newly awarded, 0 if already earned.
     *
     * @param userId The user's primary key.
     * @param badgeKey The unique badge key to award.
     * @return Points reward if badge newly awarded, 0 if already earned.
     */
    suspend fun awardBadgeIfNotEarned(userId: Int, badgeKey: String): Int {
        val alreadyEarned = badgeDao.hasBadgeBeenEarned(userId, badgeKey)
        if (alreadyEarned) {
            Log.d(TAG, "Badge $badgeKey already earned by userId: $userId")
            return 0
        }
        val badge = badgeDao.getBadgeByKey(badgeKey) ?: return 0
        val result = badgeDao.awardBadge(UserBadge(userId, badge.badgeId))
        return if (result > 0) {
            Log.d(TAG, "Badge $badgeKey awarded to userId: $userId (+${badge.pointsReward} pts)")
            badge.pointsReward
        } else 0
    }

    // ══════════════════════════════════════════════════════════════════════════
    // UTILITY FUNCTIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns the start of a day (00:00:00.000) in epoch milliseconds.
     * Used for day boundary calculations in streak and badge checks.
     *
     * @param epochMillis Any timestamp within the target day.
     * @return Epoch milliseconds for 00:00:00.000 of that day.
     */
    private fun getStartOfDay(epochMillis: Long): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = epochMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    /**
     * Returns the end of a day (23:59:59.999) in epoch milliseconds.
     *
     * @param epochMillis Any timestamp within the target day.
     * @return Epoch milliseconds for 23:59:59.999 of that day.
     */
    private fun getEndOfDay(epochMillis: Long): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = epochMillis
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * BadgeWithStatus — Combines a Badge with its earned status for a specific user.
 * Used to render the full badge grid on the Rewards screen (R20).
 */
data class BadgeWithStatus(
    val badge: Badge,
    /** True = earned (full colour), False = locked (faded grey). */
    val isEarned: Boolean
)
