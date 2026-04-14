package com.example.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.savesmart.data.entity.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Query("SELECT * FROM categories WHERE user_id = :userId AND is_deleted = 0 ORDER BY name ASC")
    fun getCategoriesForUserLive(userId: Int): LiveData<List<Category>>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND is_deleted = 0 ORDER BY name ASC")
    suspend fun getCategoriesForUser(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE category_id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: Int): Category?

    @Update
    suspend fun updateCategory(category: Category)

    @Query("UPDATE categories SET is_deleted = 1 WHERE category_id = :categoryId")
    suspend fun softDeleteCategory(categoryId: Int)

    @Query("SELECT COALESCE(SUM(amount_milliunits), 0) FROM expenses WHERE category_id = :categoryId AND date_millis BETWEEN :startMillis AND :endMillis AND is_deleted = 0")
    suspend fun getTotalSpendingForCategory(categoryId: Int, startMillis: Long, endMillis: Long): Long

    @Query("SELECT c.category_id, c.name, c.color_hex, COALESCE(SUM(e.amount_milliunits), 0) AS totalMilliunits FROM categories c LEFT JOIN expenses e ON c.category_id = e.category_id AND e.date_millis BETWEEN :startMillis AND :endMillis AND e.is_deleted = 0 WHERE c.user_id = :userId AND c.is_deleted = 0 GROUP BY c.category_id ORDER BY totalMilliunits DESC")
    fun getCategorySpendingSummaryLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<CategorySpendingSummary>>
}

data class CategorySpendingSummary(
    @ColumnInfo(name = "category_id") val categoryId: Int,
    val name: String,
    @ColumnInfo(name = "color_hex") val colorHex: String,
    val totalMilliunits: Long
)
