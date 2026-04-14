package com.example.savesmart.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.savesmart.data.dao.BadgeDao
import com.example.savesmart.data.dao.CategoryDao
import com.example.savesmart.data.dao.ExpenseDao
import com.example.savesmart.data.dao.UserDao
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.User
import com.example.savesmart.data.entity.UserBadge
import com.example.savesmart.ui.dashboard.CategoryWithSpending

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
        return userDao.insertUser(newUser) > 0
    }

    suspend fun loginUser(username: String, passwordHash: String): User? {
        return userDao.getUserByCredentials(username, passwordHash)
    }

    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    fun getUserLive(userId: Int): LiveData<User?> = userDao.getUserByIdLive(userId)

    // ────────────────────────────────────────────────────────────────────────
    // DASHBOARD METHODS (R14, R15, R16)
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Get all categories with spending for current month (R15).
     * Used by dashboard to show category summaries.
     */
    suspend fun getCategoriesWithSpending(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): List<CategoryWithSpending> {
        Log.d(TAG, "getCategoriesWithSpending: userId=$userId, period=$startMillis to $endMillis")
        try {
            val categories = categoryDao.getAllCategoriesForUser(userId)
            Log.d(TAG, "getCategoriesWithSpending: Found ${categories.size} categories")

            return categories.map { category ->
                val spending = categoryDao.getTotalSpendingForCategory(
                    category.categoryId,
                    startMillis,
                    endMillis
                )
                Log.d(TAG, "getCategoriesWithSpending: ${category.name} = $spending milliunits")

                CategoryWithSpending(
                    categoryId = category.categoryId,
                    name = category.name,
                    colorHex = category.colorHex,
                    totalMilliunits = spending,
                    maxGoalMilliunits = category.maxGoalMilliunits ?: 0L,
                    minGoalMilliunits = category.minGoalMilliunits ?: 0L
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCategoriesWithSpending: Error", e)
            throw e
        }
    }

    /**
     * Get total spending for current month (R15).
     */
    suspend fun getTotalMonthlySpending(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): Long {
        Log.d(TAG, "getTotalMonthlySpending: userId=$userId")
        return expenseDao.getTotalSpendingForUser(userId, startMillis, endMillis)
    }
}