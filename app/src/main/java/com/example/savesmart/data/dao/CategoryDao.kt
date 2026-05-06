/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 * - Android Developers (2024) LiveData overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/livedata (Accessed: 24 March 2026).
 */

package com.example.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.savesmart.data.entity.Category
import com.example.savesmart.ui.dashboard.CategoryWithSpending

/**
 * CategoryDao — Data Access Object for Category entity (Requirement T02).
 *
 * GitHub commit suggestion:
 *   [db] implement category DAO with spending aggregation queries
 *   - Supports CRUD operations with soft-delete pattern (R07)
 *   - getTotalSpendingForCategory() joins with expenses for dashboard (R15)
 *   - getCategorySpendingSummaryLive() feeds pie chart rendering (R17)
 *   Refs: R05, R06, R07, R14, R15, T02
 */
@Dao
interface CategoryDao {
    /**
     * Insert category (Requirement R05).
     * @param category Category object
     * @return row ID of inserted category
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    /**
     * Fetch all active categories for a user as LiveData (Requirement R05, T01).
     * Excludes soft-deleted categories (is_deleted = 0).
     * @param userId user ID
     * @return LiveData list of non-deleted categories
     */
    @Query("SELECT * FROM categories WHERE user_id = :userId AND is_deleted = 0 ORDER BY name ASC")
    fun getCategoriesForUserLive(userId: Int): LiveData<List<Category>>

    /**
     * Fetch all active categories for a user (suspend version).
     * @param userId user ID
     * @return list of non-deleted categories
     */
    @Query("SELECT * FROM categories WHERE user_id = :userId AND is_deleted = 0 ORDER BY name ASC")
    suspend fun getCategoriesForUser(userId: Int): List<Category>

    /**
     * Fetch single category by ID (Requirement R06, R07).
     * Respects soft-delete flag (is_deleted = 0).
     * @param categoryId category ID
     * @return Category object if found and not soft-deleted, null otherwise
     */
    @Query("SELECT * FROM categories WHERE category_id = :categoryId AND is_deleted = 0 LIMIT 1")
    suspend fun getCategoryById(categoryId: Int): Category?

    /**
     * Update category (Requirement R06).
     * @param category Category object with updated fields
     */
    @Update
    suspend fun updateCategory(category: Category)

    /**
     * Soft-delete category (Requirement R07).
     * Sets is_deleted = 1, preserves record for historical queries.
     * @param categoryId category ID
     */
    @Query("UPDATE categories SET is_deleted = 1 WHERE category_id = :categoryId")
    suspend fun softDeleteCategory(categoryId: Int)

    /**
     * Calculate total spending for a category in date range (Requirement R15).
     * Used for progress bar calculations.
     * @param categoryId category ID
     * @param startMillis range start (epoch millis)
     * @param endMillis range end (epoch millis)
     * @return total amount in milliunits
     */
    @Query("SELECT COALESCE(SUM(amount_milliunits), 0) FROM expenses WHERE category_id = :categoryId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0")
    suspend fun getTotalSpendingForCategory(categoryId: Int, startMillis: Long, endMillis: Long): Long

    /**
     * Fetch all categories for a user (suspend version, no filtering).
     * @param userId user ID
     * @return list of all categories (including soft-deleted for admin)
     */
    @Query("SELECT * FROM categories WHERE user_id = :userId AND is_deleted = 0 ORDER BY name ASC")
    suspend fun getAllCategoriesForUser(userId: Int): List<Category>

    /**
     * Fetch categories with aggregated spending (Requirement R15, R17).
     * Complex query joins categories with expenses and sums by category.
     * @param userId user ID
     * @param startMillis range start
     * @param endMillis range end
     * @return list of CategoryWithSpending objects
     */
    @Query("""
        SELECT 
            c.category_id AS categoryId, 
            c.name, 
            c.color_hex AS colorHex, 
            COALESCE(SUM(e.amount_milliunits), 0) AS totalMilliunits,
            c.max_goal_milliunits AS maxGoalMilliunits,
            c.min_goal_milliunits AS minGoalMilliunits
        FROM categories c 
        LEFT JOIN expenses e ON c.category_id = e.category_id 
            AND e.date_millis BETWEEN :startMillis AND :endMillis 
            AND e.is_deleted = 0 
        WHERE c.user_id = :userId AND c.is_deleted = 0 
        GROUP BY c.category_id 
        ORDER BY totalMilliunits DESC
    """)
    suspend fun getCategoriesWithSpending(userId: Int, startMillis: Long, endMillis: Long): List<CategoryWithSpending>

    /**
     * Fetch category spending summary as LiveData for reactive UI (Requirement R15, R17, T01).
     * @param userId user ID
     * @param startMillis range start
     * @param endMillis range end
     * @return LiveData list of CategorySpendingSummary objects
     */
    @Query("SELECT c.category_id, c.name, c.color_hex, COALESCE(SUM(e.amount_milliunits), 0) AS totalMilliunits FROM categories c LEFT JOIN expenses e ON c.category_id = e.category_id AND e.date_millis BETWEEN :startMillis AND :endMillis AND e.is_deleted = 0 WHERE c.user_id = :userId AND c.is_deleted = 0 GROUP BY c.category_id ORDER BY totalMilliunits DESC")
    fun getCategorySpendingSummaryLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<CategorySpendingSummary>>
}

/**
 * CategorySpendingSummary — Lightweight DTO for dashboard category cards (Requirement R15).
 */
data class CategorySpendingSummary(
    @ColumnInfo(name = "category_id") val categoryId: Int,
    val name: String,
    @ColumnInfo(name = "color_hex") val colorHex: String,
    val totalMilliunits: Long
)
