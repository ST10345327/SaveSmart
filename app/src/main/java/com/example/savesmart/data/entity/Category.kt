/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 * - Android Developers (2024) Material Design 3 Colours. Google LLC.
 *   Available at: https://m3.material.io (Accessed: 24 March 2026).
 */

package com.example.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Category entity — User-defined expense categories with custom colours (Requirement R05, R06).
 *
 * Supports min/max budget goals per category for dashboard progress bars (Requirement R14).
 * Soft delete (is_deleted flag) ensures categories remain in history (Requirement R07).
 *
 * GitHub commit suggestion:
 *   [db] define category entity with colour picker and budget goals
 *   - user_id foreign key enforces per-user isolation
 *   - color_hex stores 6-digit hex colour (#RRGGBB)
 *   - min/max_goal_milliunits for financial tracking (T10)
 *   - is_deleted flag soft-delete pattern — never hard-delete
 *   Refs: R05, R06, R07, R14, T02, T10
 */
@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    val categoryId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#1A6FE8",

    @ColumnInfo(name = "min_goal_milliunits")
    val minGoalMilliunits: Long? = null,

    @ColumnInfo(name = "max_goal_milliunits")
    val maxGoalMilliunits: Long? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)