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

    suspend fun registerUser(username: String, passwordHash: String): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser != null) return false
        
        val newUser = User(username = username, passwordHash = passwordHash, fullName = username)
        val userId = userDao.insertUser(newUser)
        val success = userId > 0

        if (success) {
            createSampleCategoriesForUser(userId.toInt())
        }
        return success
    }

    private suspend fun createSampleCategoriesForUser(userId: Int) {
        Log.d(TAG, "createSampleCategoriesForUser: Creating sample categories for userId=$userId")
        val sampleCategories = listOf(
            Category(userId = userId, name = "Food & Dining", colorHex = "#FF6B6B", maxGoalMilliunits = 200_000L, minGoalMilliunits = 100_000L),
            Category(userId = userId, name = "Transport", colorHex = "#4ECDC4", maxGoalMilliunits = 150_000L, minGoalMilliunits = 75_000L),
            Category(userId = userId, name = "Entertainment", colorHex = "#45B7D1", maxGoalMilliunits = 100_000L, minGoalMilliunits = 50_000L),
            Category(userId = userId, name = "Shopping", colorHex = "#96CEB4", maxGoalMilliunits = 80_000L, minGoalMilliunits = 40_000L)
        )

        for (category in sampleCategories) {
            categoryDao.insertCategory(category)
        }
    }

    suspend fun loginUser(username: String, passwordHash: String): User? {
        return userDao.getUserByCredentials(username, passwordHash)
    }

    fun getUserLive(userId: Int): LiveData<User?> = userDao.getUserByIdLive(userId)

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
        }
        return id
    }

    fun getExpensesInRangeLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<Expense>> = expenseDao.getExpensesInRangeLive(userId, startMillis, endMillis)
    suspend fun deleteExpense(expenseId: Int) = expenseDao.softDeleteExpense(expenseId)

    suspend fun getCategoriesWithSpending(userId: Int, startMillis: Long, endMillis: Long): List<CategoryWithSpending> {
        val categories = categoryDao.getAllCategoriesForUser(userId)
        return categories.map { category ->
            val spending = categoryDao.getTotalSpendingForCategory(category.categoryId, startMillis, endMillis)
            CategoryWithSpending(
                categoryId = category.categoryId,
                name = category.name,
                colorHex = category.colorHex,
                totalMilliunits = spending,
                maxGoalMilliunits = category.maxGoalMilliunits ?: 0L,
                minGoalMilliunits = category.minGoalMilliunits ?: 0L
            )
        }
    }
}
