package com.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Category Entity — Room database table for expense categories.
 *
 * References:
 * - Android Developers (2024) Define relationships between objects. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/relationships
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - All monetary amounts (minGoal, maxGoal) stored as Long milliunits.
 *   Pattern adopted from YNAB API (2024): 1 000 milliunits = R1.00
 *   e.g. R500.00 is stored as 500_000L.
 *   Reference: YNAB API (2024) YNAB API v1 documentation: milliunits and data model.
 *   Available at: https://api.ynab.com/v1 (Accessed: 24 March 2026).
 * - ForeignKey links category to user — cascade delete removes categories
 *   when user is deleted.
 * - colorHex stores the display colour as a hex string e.g. "#1A6FE8" (R05).
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

    /** Auto-generated primary key. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    val categoryId: Int = 0,

    /** Foreign key — links category to its owner (R05). */
    @ColumnInfo(name = "user_id")
    val userId: Int,

    /** Display name of the category e.g. "Groceries", "Transport" (R05). */
    @ColumnInfo(name = "name")
    val name: String,

    /**
     * Hex colour string for UI display e.g. "#1A6FE8" (R05).
     * Used to colour-code category cards on the dashboard (R15).
     */
    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#1A6FE8",

    /**
     * Minimum monthly spending goal in milliunits (R14).
     * Null means no minimum goal is set.
     * e.g. R200.00 minimum = 200_000L
     */
    @ColumnInfo(name = "min_goal_milliunits")
    val minGoalMilliunits: Long? = null,

    /**
     * Maximum monthly spending goal in milliunits (R14).
     * Null means no maximum goal is set.
     * e.g. R500.00 maximum = 500_000L
     */
    @ColumnInfo(name = "max_goal_milliunits")
    val maxGoalMilliunits: Long? = null,

    /** Soft delete flag — deleted categories are hidden not removed (R07). */
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    /** Creation timestamp in epoch milliseconds. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
