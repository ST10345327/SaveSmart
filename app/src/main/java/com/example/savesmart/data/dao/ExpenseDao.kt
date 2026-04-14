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
 *
 * GitHub commit suggestion:
 *   [db] add monthly spending query to ExpenseDao
 *   - Implemented aggregation query for total spending
 *   Refs: R15, T02
 */
@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0 ORDER BY date_millis DESC, created_at DESC")
    fun getExpensesInRangeLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE expense_id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: Int): Expense?

    @Query("SELECT COUNT(*) FROM expenses WHERE user_id = :userId AND is_deleted = 0")
    suspend fun getTotalExpenseCount(userId: Int): Int

    /**
     * Requirement R15: Sum all expense amounts for a specific user within a date range.
     */
    @Query("SELECT COALESCE(SUM(amount_milliunits), 0) FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0")
    suspend fun getTotalSpendingForUser(userId: Int, startMillis: Long, endMillis: Long): Long

    @Query("SELECT date_millis, SUM(amount_milliunits) AS totalMilliunits FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0 GROUP BY date_millis ORDER BY date_millis ASC")
    suspend fun getDailySpending(userId: Int, startMillis: Long, endMillis: Long): List<DailySpending>

    @Query("SELECT COUNT(*) FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startOfDay AND :endOfDay AND is_deleted = 0")
    suspend fun getExpenseCountForDay(userId: Int, startOfDay: Long, endOfDay: Long): Int

    @Query("SELECT COUNT(DISTINCT date_millis) FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0")
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
