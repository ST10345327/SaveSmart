package com.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Expense Entity — Room database table for individual expense entries.
 *
 * References:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room
 *   (Accessed: 24 March 2026).
 * - YNAB API (2024) YNAB API v1 documentation: milliunits and data model.
 *   You Need A Budget LLC. Available at: https://api.ynab.com/v1
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - amountMilliunits: All amounts stored as Long milliunits to prevent
 *   floating point rounding errors (T10, R08).
 *   e.g. R12.50 = 12_500L, R1 000.00 = 1_000_000L
 * - startTimeMillis / endTimeMillis: epoch ms for start and end time (R08).
 * - receiptPhotoPath: file system path to the attached photo (R09).
 *   Null if no photo is attached.
 * - dateMillis: epoch ms of the expense date for date range filtering (R10).
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["category_id"]),
        Index(value = ["date_millis"])
    ]
)
data class Expense(

    /** Auto-generated primary key. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    val expenseId: Int = 0,

    /** Foreign key — links expense to its owner user. */
    @ColumnInfo(name = "user_id")
    val userId: Int,

    /**
     * Foreign key — links expense to a category (R08).
     * Nullable: SET_NULL if category is deleted.
     */
    @ColumnInfo(name = "category_id")
    val categoryId: Int? = null,

    /**
     * Expense amount stored in milliunits (R08, T10).
     * Adopted from YNAB API milliunit pattern:
     * 1 000 milliunits = R1.00
     * R12.50 stored as 12_500L
     */
    @ColumnInfo(name = "amount_milliunits")
    val amountMilliunits: Long,

    /** Short description of what the expense was for (R08). */
    @ColumnInfo(name = "description")
    val description: String = "",

    /**
     * Date of the expense in epoch milliseconds (R08).
     * Used for date range filtering in the expense list (R10).
     */
    @ColumnInfo(name = "date_millis")
    val dateMillis: Long,

    /**
     * Start time of the activity in epoch milliseconds (R08).
     * Required field per POE Part 2 brief.
     */
    @ColumnInfo(name = "start_time_millis")
    val startTimeMillis: Long,

    /**
     * End time of the activity in epoch milliseconds (R08).
     * Required field per POE Part 2 brief.
     */
    @ColumnInfo(name = "end_time_millis")
    val endTimeMillis: Long,

    /**
     * Absolute file path to the attached receipt photo (R09).
     * Null if no photo is attached.
     * e.g. "/data/data/com.savesmart/files/receipts/receipt_1234.jpg"
     */
    @ColumnInfo(name = "receipt_photo_path")
    val receiptPhotoPath: String? = null,

    /** Soft delete flag — deleted expenses hidden not removed (R12). */
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    /** Creation timestamp in epoch milliseconds. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
