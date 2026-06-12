package com.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User Entity — Room database table for user authentication.
 *
 * References:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room
 *   (Accessed: 24 March 2026).
 * - Android Developers (2024) Define data using Room entities. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/defining-data
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - Username is indexed and unique to prevent duplicate accounts (R03).
 * - Password is stored as a SHA-256 hash — never in plain text (R01).
 * - createdAt stores epoch milliseconds for consistent date handling.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(

    /** Auto-generated primary key. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    /** Unique username chosen during registration. */
    @ColumnInfo(name = "username")
    val username: String,

    /**
     * SHA-256 hashed password.
     * Plain text passwords are NEVER stored.
     * Reference: Android Developers (2024) Security best practices.
     * Available at: https://developer.android.com/topic/security/best-practices
     */
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    /** Full display name of the user. */
    @ColumnInfo(name = "full_name")
    val fullName: String,

    /** Total gamification points accumulated by the user (R19). */
    @ColumnInfo(name = "total_points")
    val totalPoints: Int = 0,

    /** Current gamification level derived from total points (R21). */
    @ColumnInfo(name = "level")
    val level: Int = 1,

    /**
     * Whether the user has completed the onboarding flow (R23).
     * False on first login — onboarding shown once only.
     */
    @ColumnInfo(name = "onboarding_complete")
    val onboardingComplete: Boolean = false,

    /** Account creation timestamp in epoch milliseconds. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
