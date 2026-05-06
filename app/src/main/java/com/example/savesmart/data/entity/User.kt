/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 * - Google (2024) BigInteger for integer storage. Google LLC.
 *   Available at: https://developer.android.com/reference/java/math/BigInteger
 */

package com.example.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User entity — Core user account record (Requirement R01, R02).
 *
 * Stores user credentials (hashed), account metadata, and gamification state.
 * onboarding_complete flag ensures 3-step flow only shows once (Requirement R23).
 *
 * GitHub commit suggestion:
 *   [db] define user entity with password hashing and gamification fields
 *   - username unique indexed for fast credential lookup
 *   - password_hash stores SHA-256 for security (never plaintext)
 *   - total_points and level track gamification progress
 *   - min/max_monthly_budget for dashboard spending goals
 *   Refs: R01, R02, R21, T02, T10
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "total_points")
    val totalPoints: Int = 0,

    @ColumnInfo(name = "level")
    val level: Int = 1,

    @ColumnInfo(name = "onboarding_complete")
    val onboardingComplete: Boolean = false,

    @ColumnInfo(name = "min_monthly_budget")
    val minMonthlyBudget: Long = 0,

    @ColumnInfo(name = "max_monthly_budget")
    val maxMonthlyBudget: Long = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)