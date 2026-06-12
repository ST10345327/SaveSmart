/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 */

package com.example.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.savesmart.data.entity.Expense

/**
 * Data Access Object for Expense entity.
 */
@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Query("""
        SELECT e.*, c.name AS category_name, c.color_hex AS category_color 
        FROM expenses e 
        LEFT JOIN categories c ON e.category_id = c.category_id 
        WHERE e.user_id = :userId AND e.date_millis BETWEEN :startMillis AND :endMillis AND e.is_deleted = 0 
        ORDER BY e.date_millis DESC, e.created_at DESC
    """)
    fun getExpensesWithCategoryLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<ExpenseWithCategory>>

    @Query("SELECT * FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0 ORDER BY date_millis DESC, created_at DESC")
    fun getExpensesInRangeLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<Expense>>

    // Requirement R12: Ensure specific fetch respects soft-delete
    @Query("SELECT * FROM expenses WHERE expense_id = :expenseId AND is_deleted = 0 LIMIT 1")
    suspend fun getExpenseById(expenseId: Int): Expense?

    @Query("SELECT COUNT(*) FROM expenses WHERE user_id = :userId AND is_deleted = 0")
    suspend fun getTotalExpenseCount(userId: Int): Int

    @Query("SELECT COALESCE(SUM(amount_milliunits), 0) FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0")
    suspend fun getTotalSpendingForUser(userId: Int, startMillis: Long, endMillis: Long): Long

    @Query("""
        SELECT (strftime('%s', date(date_millis / 1000, 'unixepoch')) * 1000) AS date_millis,
               SUM(amount_milliunits) AS totalMilliunits
        FROM expenses
        WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0
        GROUP BY date(date_millis / 1000, 'unixepoch')
        ORDER BY date_millis ASC
    """)
    suspend fun getDailySpending(userId: Int, startMillis: Long, endMillis: Long): List<DailySpending>

    @Query("SELECT COUNT(*) FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startOfDay AND :endOfDay AND is_deleted = 0")
    suspend fun getExpenseCountForDay(userId: Int, startOfDay: Long, endOfDay: Long): Int

    @Query("SELECT COUNT(DISTINCT date(date_millis / 1000, 'unixepoch')) FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0")
    suspend fun getDistinctLoggingDays(userId: Int, startMillis: Long, endMillis: Long): Int

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("UPDATE expenses SET is_deleted = 1 WHERE expense_id = :expenseId")
    suspend fun softDeleteExpense(expenseId: Int)
}

data class DailySpending(
    @ColumnInfo(name = "date_millis") val dateMillis: Long,
    val totalMilliunits: Long
)

data class ExpenseWithCategory(
    @ColumnInfo(name = "expense_id") val expenseId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "category_id") val categoryId: Int?,
    @ColumnInfo(name = "amount_milliunits") val amountMilliunits: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date_millis") val dateMillis: Long,
    @ColumnInfo(name = "receipt_photo_path") val receiptPhotoPath: String?,
    @ColumnInfo(name = "category_name") val categoryName: String?,
    @ColumnInfo(name = "category_color") val categoryColor: String?
)
