package com.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savesmart.data.entity.Category

/**
 * CategoryDao — Data Access Object for expense category operations.
 *
 * References:
 * - Android Developers (2024) Accessing data using Room DAOs. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/accessing-data
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - Soft delete pattern used (is_deleted flag) rather than hard delete.
 *   This preserves referential integrity with existing expense entries (R07).
 * - All monetary values returned/stored as Long milliunits (T10).
 */
@Dao
interface CategoryDao {

    // ── INSERT ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new expense category (R05).
     *
     * @param category The Category entity to insert.
     * @return The new row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    // ── SELECT ────────────────────────────────────────────────────────────────

    /**
     * Returns all active (non-deleted) categories for a user as LiveData (R05).
     * Ordered alphabetically by name for consistent display.
     *
     * @param userId The owner's user ID.
     * @return LiveData list of active categories.
     */
    @Query("""
        SELECT * FROM categories 
        WHERE user_id = :userId 
        AND is_deleted = 0
        ORDER BY name ASC
    """)
    fun getCategoriesForUserLive(userId: Int): LiveData<List<Category>>

    /**
     * Returns all active categories for a user as a plain list.
     * Used in suspend context where LiveData is not needed
     * e.g. spinner population in AddExpense screen (R08).
     *
     * @param userId The owner's user ID.
     * @return List of active categories.
     */
    @Query("""
        SELECT * FROM categories 
        WHERE user_id = :userId 
        AND is_deleted = 0
        ORDER BY name ASC
    """)
    suspend fun getCategoriesForUser(userId: Int): List<Category>

    /**
     * Returns a single category by its primary key (R06).
     *
     * @param categoryId The category's primary key.
     * @return The Category entity, or null if not found.
     */
    @Query("SELECT * FROM categories WHERE category_id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: Int): Category?

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Updates an existing category's details (R06).
     * Used when editing name, colour, or min/max goals.
     *
     * @param category The updated Category entity.
     */
    @Update
    suspend fun updateCategory(category: Category)

    // ── SOFT DELETE ───────────────────────────────────────────────────────────

    /**
     * Soft-deletes a category by setting is_deleted = true (R07).
     * The category remains in the database to preserve expense history.
     * Expenses linked to this category retain their category_id reference.
     *
     * @param categoryId The category's primary key.
     */
    @Query("UPDATE categories SET is_deleted = 1 WHERE category_id = :categoryId")
    suspend fun softDeleteCategory(categoryId: Int)

    // ── SPENDING AGGREGATES ───────────────────────────────────────────────────

    /**
     * Returns the total spending in milliunits for a category
     * within a given date range (R14, R15).
     * Used by the dashboard to calculate progress bar values.
     *
     * @param categoryId The category's primary key.
     * @param startMillis Start of the date range in epoch milliseconds.
     * @param endMillis End of the date range in epoch milliseconds.
     * @return Total amount spent in milliunits, or 0 if no expenses.
     */
    @Query("""
        SELECT COALESCE(SUM(e.amount_milliunits), 0)
        FROM expenses e
        WHERE e.category_id = :categoryId
        AND e.date_millis BETWEEN :startMillis AND :endMillis
        AND e.is_deleted = 0
    """)
    suspend fun getTotalSpendingForCategory(
        categoryId: Int,
        startMillis: Long,
        endMillis: Long
    ): Long

    /**
     * Returns total spending per category as a LiveData list
     * for the category report screen (R17).
     *
     * @param userId The user's ID.
     * @param startMillis Start of report period in epoch milliseconds.
     * @param endMillis End of report period in epoch milliseconds.
     * @return LiveData list of CategorySpendingSummary.
     */
    @Query("""
        SELECT c.category_id, c.name, c.color_hex,
               COALESCE(SUM(e.amount_milliunits), 0) AS total_milliunits
        FROM categories c
        LEFT JOIN expenses e 
            ON c.category_id = e.category_id
            AND e.date_millis BETWEEN :startMillis AND :endMillis
            AND e.is_deleted = 0
        WHERE c.user_id = :userId
        AND c.is_deleted = 0
        GROUP BY c.category_id
        ORDER BY total_milliunits DESC
    """)
    fun getCategorySpendingSummaryLive(
        userId: Int,
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<CategorySpendingSummary>>
}

/**
 * Data class for category spending summary — used by the Category Report
 * screen pie chart (R17) and dashboard category cards (R15).
 *
 * Reference:
 * - Android Developers (2024) Return a subset of a table's columns. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/accessing-data#query-subset-cols
 *   (Accessed: 24 March 2026).
 */
data class CategorySpendingSummary(
    val categoryId: Int,
    val name: String,
    val colorHex: String,
    /** Total spending in milliunits. Divide by 1000 for Rand display. */
    val totalMilliunits: Long
)
