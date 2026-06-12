package com.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Badge Entity — Defines all available milestone badges in SaveSmart.
 *
 * References:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - Badge definitions are pre-populated at database creation (R20).
 * - badgeKey is a unique string identifier used for programmatic unlock logic.
 * - iconResName stores the drawable resource name as a string to avoid
 *   storing resource IDs which change between builds.
 *
 * Badge definitions (R20):
 * - FIRST_SAVE: First expense logged
 * - STREAK_7: 7-day consecutive logging streak
 * - STREAK_30: 30-day consecutive logging streak
 * - BUDGET_MASTER: Stayed under budget for 3 consecutive months
 * - QUICK_LOGGER: Logged 10 expenses in a single day
 * - ZERO_SPEND: Logged a zero-spend day
 * - GOAL_CRUSHER: All categories within limits for a full month
 */
@Entity(tableName = "badges")
data class Badge(

    /** Auto-generated primary key. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "badge_id")
    val badgeId: Int = 0,

    /**
     * Unique string key for programmatic badge unlock logic (R20).
     * e.g. "FIRST_SAVE", "STREAK_7", "BUDGET_MASTER"
     */
    @ColumnInfo(name = "badge_key")
    val badgeKey: String,

    /** Display name shown in the Rewards screen badge grid (R20). */
    @ColumnInfo(name = "name")
    val name: String,

    /** Short description shown below the badge icon (R20). */
    @ColumnInfo(name = "description")
    val description: String,

    /**
     * Drawable resource name for the badge icon (R20).
     * e.g. "ic_badge_first_save"
     * Resolved at runtime using Resources.getIdentifier()
     */
    @ColumnInfo(name = "icon_res_name")
    val iconResName: String,

    /** Points awarded when this badge is unlocked (R19). */
    @ColumnInfo(name = "points_reward")
    val pointsReward: Int = 0
)

// ─────────────────────────────────────────────────────────────────────────────

/**
 * UserBadge Entity — Junction table linking users to their earned badges.
 *
 * References:
 * - Android Developers (2024) Create many-to-many relationships. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/relationships
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - Composite primary key on (userId, badgeId) prevents duplicate badge awards.
 * - earnedAt stores when the badge was unlocked for display in the UI.
 */
@Entity(
    tableName = "user_badges",
    primaryKeys = ["user_id", "badge_id"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Badge::class,
            parentColumns = ["badge_id"],
            childColumns = ["badge_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["badge_id"])
    ]
)
data class UserBadge(

    /** Foreign key — the user who earned the badge (R20). */
    @ColumnInfo(name = "user_id")
    val userId: Int,

    /** Foreign key — the badge that was earned (R20). */
    @ColumnInfo(name = "badge_id")
    val badgeId: Int,

    /** Timestamp when the badge was earned in epoch milliseconds (R20). */
    @ColumnInfo(name = "earned_at")
    val earnedAt: Long = System.currentTimeMillis()
)
