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

        // If registration successful, create sample categories for new user (R05)
        if (success) {
            createSampleCategoriesForUser(userId.toInt())
        }

        return success
    }

    private suspend fun createSampleCategoriesForUser(userId: Int) {
        Log.d(TAG, "createSampleCategoriesForUser: Creating sample categories for userId=$userId")

        // Define some sample categories (R05)
        val sampleCategories = listOf(
            com.example.savesmart.data.entity.Category(
                userId = userId,
                name = "Food & Dining",
                colorHex = "#FF6B6B",
                maxGoalMilliunits = 200_000L, // R2,000.00
                minGoalMilliunits = 100_000L   // R1,000.00
            ),
            com.example.savesmart.data.entity.Category(
                userId = userId,
                name = "Transport",
                colorHex = "#4ECDC4",
                maxGoalMilliunits = 150_000L, // R1,500.00
                minGoalMilliunits = 75_000L   // R750.00
            ),
            com.example.savesmart.data.entity.Category(
                userId = userId,
                name = "Entertainment",
                colorHex = "#45B7D1",
                maxGoalMilliunits = 100_000L, // R1,000.00
                minGoalMilliunits = 50_000L   // R500.00
            ),
            com.example.savesmart.data.entity.Category(
                userId = userId,
                name = "Shopping",
                colorHex = "#96CEB4",
                maxGoalMilliunits = 80_000L,  // R800.00
                minGoalMilliunits = 40_000L   // R400.00
            )
        )

        // Insert each sample category into the database
        for (category in sampleCategories) {
            val categoryId = categoryDao.insertCategory(category)
            Log.d(TAG, "createSampleCategoriesForUser: Created category '${category.name}' with ID=$categoryId")
        }

        // Removed creation of sample expenses to ensure dashboard starts at zero (T01)
        // createSampleExpensesForUser(userId)

        Log.d(TAG, "createSampleCategoriesForUser: Created ${sampleCategories.size} sample categories")
    }

    private suspend fun createSampleExpensesForUser(userId: Int) {
        Log.d(TAG, "createSampleExpensesForUser: Creating sample expenses for userId=$userId")

        // Get the categories we just created
        val categories = categoryDao.getCategoriesForUser(userId)

        if (categories.isEmpty()) {
            Log.w(TAG, "createSampleExpensesForUser: No categories found, skipping expense creation")
            return
        }

        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()

        // Create some sample expenses for this month
        val sampleExpenses = mutableListOf<com.example.savesmart.data.entity.Expense>()

        // Food expenses
        val foodCategory = categories.find { it.name == "Food & Dining" }
        if (foodCategory != null) {
            sampleExpenses.add(com.example.savesmart.data.entity.Expense(
                userId = userId,
                categoryId = foodCategory.categoryId,
                amountMilliunits = 45_000L, // R450.00
                description = "Lunch at restaurant",
                dateMillis = currentTime - (2 * 24 * 60 * 60 * 1000L), // 2 days ago
                startTimeMillis = currentTime - (2 * 24 * 60 * 60 * 1000L) + (12 * 60 * 60 * 1000L), // 12:00
                endTimeMillis = currentTime - (2 * 24 * 60 * 60 * 1000L) + (13 * 60 * 60 * 1000L)    // 13:00
            ))
            sampleExpenses.add(com.example.savesmart.data.entity.Expense(
                userId = userId,
                categoryId = foodCategory.categoryId,
                amountMilliunits = 25_000L, // R250.00
                description = "Groceries",
                dateMillis = currentTime - (5 * 24 * 60 * 60 * 1000L), // 5 days ago
                startTimeMillis = currentTime - (5 * 24 * 60 * 60 * 1000L) + (10 * 60 * 60 * 1000L), // 10:00
                endTimeMillis = currentTime - (5 * 24 * 60 * 60 * 1000L) + (11 * 60 * 60 * 1000L)    // 11:00
            ))
        }

        // Transport expenses
        val transportCategory = categories.find { it.name == "Transport" }
        if (transportCategory != null) {
            sampleExpenses.add(com.example.savesmart.data.entity.Expense(
                userId = userId,
                categoryId = transportCategory.categoryId,
                amountMilliunits = 15_000L, // R150.00
                description = "Uber ride",
                dateMillis = currentTime - (1 * 24 * 60 * 60 * 1000L), // 1 day ago
                startTimeMillis = currentTime - (1 * 24 * 60 * 60 * 1000L) + (18 * 60 * 60 * 1000L), // 18:00
                endTimeMillis = currentTime - (1 * 24 * 60 * 60 * 1000L) + (18 * 60 * 60 * 1000L) + (30 * 60 * 1000L) // 18:30
            ))
        }

        // Entertainment expenses
        val entertainmentCategory = categories.find { it.name == "Entertainment" }
        if (entertainmentCategory != null) {
            sampleExpenses.add(com.example.savesmart.data.entity.Expense(
                userId = userId,
                categoryId = entertainmentCategory.categoryId,
                amountMilliunits = 35_000L, // R350.00
                description = "Movie tickets",
                dateMillis = currentTime - (7 * 24 * 60 * 60 * 1000L), // 7 days ago
                startTimeMillis = currentTime - (7 * 24 * 60 * 60 * 1000L) + (20 * 60 * 60 * 1000L), // 20:00
                endTimeMillis = currentTime - (7 * 24 * 60 * 60 * 1000L) + (22 * 60 * 60 * 1000L)    // 22:00
            ))
        }

        // Insert sample expenses
        for (expense in sampleExpenses) {
            val expenseId = expenseDao.insertExpense(expense)
            Log.d(TAG, "createSampleExpensesForUser: Created expense '${expense.description}' with ID=$expenseId")
        }

        Log.d(TAG, "createSampleExpensesForUser: Created ${sampleExpenses.size} sample expenses")
    }

    suspend fun loginUser(username: String, passwordHash: String): User? {
        return userDao.getUserByCredentials(username, passwordHash)
    }

    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    fun getUserLive(userId: Int): LiveData<User?> = userDao.getUserByIdLive(userId)

    // ────────────────────────────────────────────────────────────────────────
    // CATEGORY METHODS (R05, R06)
    // ────────────────────────────────────────────────────────────────────────

    fun getCategoriesForUserLive(userId: Int): LiveData<List<Category>> {
        return categoryDao.getCategoriesForUserLive(userId)
    }

    suspend fun insertCategory(category: Category): Long {
        Log.d(TAG, "insertCategory: Inserting category '${category.name}'")
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        Log.d(TAG, "updateCategory: Updating category '${category.name}'")
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(categoryId: Int) {
        Log.d(TAG, "deleteCategory: Soft deleting categoryId $categoryId")
        categoryDao.softDeleteCategory(categoryId)
    }

    // ────────────────────────────────────────────────────────────────────────
    // EXPENSE METHODS (R08, R10, R12, R13)
    // ────────────────────────────────────────────────────────────────────────

    suspend fun insertExpense(expense: Expense): Long {
        Log.d(TAG, "insertExpense(): description=${expense.description}, amount=${expense.amountMilliunits}")
        return expenseDao.insertExpense(expense)
    }

    fun getExpensesInRangeLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesInRangeLive(userId, startMillis, endMillis)
    }

    suspend fun deleteExpense(expenseId: Int) {
        Log.d(TAG, "deleteExpense(): expenseId=$expenseId")
        expenseDao.softDeleteExpense(expenseId)
    }

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
