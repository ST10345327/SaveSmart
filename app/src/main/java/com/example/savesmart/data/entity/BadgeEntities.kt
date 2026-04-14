package com.example.savesmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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