/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 */

package com.example.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Badge entity — Milestone achievements for gamification (Requirement R20).
 *
 * GitHub commit suggestion:
 *   [db] define badge entity for milestone tracking
 *   - badge_key unique identifier (FIRST_SAVE, STREAK_7, etc.)
 *   - icon_res_name references drawable resources for UI display
 *   - points_reward incentivises badge collection
 *   Refs: R20, T02
 */
@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "badge_id")
    val badgeId: Int = 0,

    @ColumnInfo(name = "badge_key")
    val badgeKey: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "icon_res_name")
    val iconResName: String,

    @ColumnInfo(name = "points_reward")
    val pointsReward: Int = 0
)

/**
 * UserBadge entity — Link between User and Badge (junction table) (Requirement R20).
 *
 * GitHub commit suggestion:
 *   [db] define user_badge junction table for many-to-many relationship
 *   - earned_at timestamp records badge unlock date for display
 *   - Natural composite key (user_id, badge_id) prevents duplicates
 *   Refs: R20, T02
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
    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "badge_id")
    val badgeId: Int,

    @ColumnInfo(name = "earned_at")
    val earnedAt: Long = System.currentTimeMillis()
)