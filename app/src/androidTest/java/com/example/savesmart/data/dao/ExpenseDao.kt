package com.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savesmart.data.entity.Expense

/**
 * ExpenseDao — Data Access Object for expense entry operations.
 *
 * References:
 * - Android Developers (2024) Accessing data using Room DAOs. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/accessing-data
 *   (Accessed: 24 March 2026).
 * - Stack Overflow (2021) Room database — query between two dates.
 *   Available at: https://stackoverflow.com/questions/44429548
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - All date filtering uses epoch milliseconds stored in date_millis (R10).
 * - Soft delete pattern: is_deleted flag set to 1 instead of hard delete (R12).
 * - Queries always filter AND is_deleted = 0 to exclude soft-deleted rows.
 */
@Dao
interface ExpenseDao {

    // ── INSERT ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new expense entry (R08).
     *
     * @param expense The Expense entity to insert.
     * @return The new row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    // ── SELECT ────────────────────────────────────────────────────────────────

    /**
     * Returns all active expenses for a user within a date range
     * as LiveData (R10).
     * Ordered by date descending — most recent first.
     *
     * @param userId The owner's user ID.
     * @param startMillis Start of date range in epoch milliseconds.
     * @param endMillis End of date range in epoch milliseconds.
     * @return LiveData list of expenses in the date range.
     */
    @Query("""
        SELECT * FROM expenses
        WHERE user_id = :userId
        AND date_millis BETWEEN :startMillis AND :endMillis
        AND is_deleted = 0
        ORDER BY date_millis DESC, created_at DESC
    """)
    fun getExpensesInRangeLive(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<Expense>>

    /**
     * Returns a single expense by its primary key (R12).
     * Used when opening an expense for editing.
     *
     * @param expenseId The expense's primary key.
     * @return The Expense entity, or null if not found.
     */
    @Query("SELECT * FROM expenses WHERE expense_id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: Int): Expense?

    /**
     * Returns the total number of active expenses for a user (R19).
     * Used to trigger the FIRST_SAVE badge unlock on count == 1.
     *
     * @param userId The user's ID.
     * @return Total count of non-deleted expenses.
     */
    @Query("""
        SELECT COUNT(*) FROM expenses 
        WHERE user_id = :userId 
        AND is_deleted = 0
    """)
    suspend fun getTotalExpenseCount(userId: Int): Int

    /**
     * Returns the total spending in milliunits for a user
     * within a date range (R15, R16).
     * Used by the dashboard total budget progress bar.
     *
     * @param userId The user's ID.
     * @param startMillis Start of period in epoch milliseconds.
     * @param endMillis End of period in epoch milliseconds.
     * @return Total spending in milliunits, or 0 if no expenses.
     */
    @Query("""
        SELECT COALESCE(SUM(amount_milliunits), 0)
        FROM expenses
        WHERE user_id = :userId
        AND date_millis BETWEEN :startMillis AND :endMillis
        AND is_deleted = 0
    """)
    suspend fun getTotalSpendingInRange(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): Long

    /**
     * Returns daily spending totals for the spending graph (R18).
     * Groups expenses by date_millis truncated to day boundaries.
     *
     * @param userId The user's ID.
     * @param startMillis Start of period in epoch milliseconds.
     * @param endMillis End of period in epoch milliseconds.
     * @return List of DailySpending objects for chart rendering.
     */
    @Query("""
        SELECT date_millis, SUM(amount_milliunits) AS total_milliunits
        FROM expenses
        WHERE user_id = :userId
        AND date_millis BETWEEN :startMillis AND :endMillis
        AND is_deleted = 0
        GROUP BY date_millis
        ORDER BY date_millis ASC
    """)
    suspend fun getDailySpending(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): List<DailySpending>

    /**
     * Returns the count of expenses logged on a specific date (R19).
     * Used to check the QUICK_LOGGER badge condition (10 in one day).
     *
     * @param userId The user's ID.
     * @param startOfDay Start of day in epoch milliseconds.
     * @param endOfDay End of day in epoch milliseconds.
     * @return Count of expenses on that day.
     */
    @Query("""
        SELECT COUNT(*) FROM expenses
        WHERE user_id = :userId
        AND date_millis BETWEEN :startOfDay AND :endOfDay
        AND is_deleted = 0
    """)
    suspend fun getExpenseCountForDay(
        userId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): Int

    /**
     * Returns the count of distinct days the user logged at least one expense,
     * within the last N days — used for streak calculation (R19, R20).
     *
     * @param userId The user's ID.
     * @param startMillis Start of the streak window.
     * @param endMillis End of the streak window (today).
     * @return Count of distinct logging days.
     */
    @Query("""
        SELECT COUNT(DISTINCT date_millis) FROM expenses
        WHERE user_id = :userId
        AND date_millis BETWEEN :startMillis AND :endMillis
        AND is_deleted = 0
    """)
    suspend fun getDistinctLoggingDays(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): Int

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Updates an existing expense entry (R12).
     *
     * @param expense The updated Expense entity.
     */
    @Update
    suspend fun updateExpense(expense: Expense)

    // ── SOFT DELETE ───────────────────────────────────────────────────────────

    /**
     * Soft-deletes an expense by setting is_deleted = 1 (R12).
     * The expense is hidden from all queries but preserved in the database
     * for data integrity.
     *
     * @param expenseId The expense's primary key.
     */
    @Query("UPDATE expenses SET is_deleted = 1 WHERE expense_id = :expenseId")
    suspend fun softDeleteExpense(expenseId: Int)
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * DailySpending — Projection used for the Spending Graph bar chart (R18).
 *
 * Reference:
 * - Android Developers (2024) Return a subset of a table's columns. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/accessing-data
 *   (Accessed: 24 March 2026).
 */
data class DailySpending(
    /** Day's date in epoch milliseconds. */
    val dateMillis: Long,
    /** Total spending for that day in milliunits. */
    val totalMilliunits: Long
)
