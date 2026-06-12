/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 * - YNAB API (2024) YNAB API v1 documentation: milliunits and data model. https://api.ynab.com/v1
 */

package com.example.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Expense entity — Individual expense records with receipt support (Requirement R08, R09).
 *
 * Stores expense amount in milliunits (no Double/Float) for 100% financial precision (T10).
 * Receipt photo path enables receipt capture workflow (Requirement R09, R11).
 * Soft delete (is_deleted flag) preserves history and financial calculations (Requirement R12).
 *
 * GitHub commit suggestion:
 *   [db] define expense entity with receipt photo and time tracking
 *   - amount_milliunits stores in Long (1000 = R1.00) per YNAB convention (T10)
 *   - date_millis, start_time_millis, end_time_millis track expense timing (R08)
 *   - receipt_photo_path nullable for optional camera capture (R09)
 *   - is_deleted flag preserves all transactions for reports
 *   - Indexed on user_id, category_id, date_millis for fast range queries
 *   Refs: R08, R09, R10, R12, T02, T10
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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    val expenseId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "category_id")
    val categoryId: Int? = null,

    @ColumnInfo(name = "amount_milliunits")
    val amountMilliunits: Long,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "date_millis")
    val dateMillis: Long,

    @ColumnInfo(name = "start_time_millis")
    val startTimeMillis: Long,

    @ColumnInfo(name = "end_time_millis")
    val endTimeMillis: Long,

    @ColumnInfo(name = "receipt_photo_path")
    val receiptPhotoPath: String? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)