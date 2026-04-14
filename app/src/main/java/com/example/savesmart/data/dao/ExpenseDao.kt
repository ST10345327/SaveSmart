package com.example.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.savesmart.data.entity.Expense

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

    @Query("SELECT COALESCE(SUM(amount_milliunits), 0) FROM expenses WHERE user_id = :userId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0")
    suspend fun getTotalSpendingInRange(userId: Int, startMillis: Long, endMillis: Long): Long

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
