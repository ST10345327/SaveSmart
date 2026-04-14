package com.example.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.UserBadge

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: Badge): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllBadges(badges: List<Badge>)

    @Query("SELECT * FROM badges ORDER BY badge_id ASC")
    suspend fun getAllBadges(): List<Badge>

    @Query("SELECT * FROM badges WHERE badge_key = :badgeKey LIMIT 1")
    suspend fun getBadgeByKey(badgeKey: String): Badge?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun awardBadge(userBadge: UserBadge): Long

    @Query("SELECT badge_id FROM user_badges WHERE user_id = :userId")
    suspend fun getEarnedBadgeIds(userId: Int): List<Int>

    @Query("SELECT b.* FROM badges b INNER JOIN user_badges ub ON b.badge_id = ub.badge_id WHERE ub.user_id = :userId ORDER BY ub.earned_at DESC")
    fun getEarnedBadgesLive(userId: Int): LiveData<List<Badge>>

    @Query("SELECT b.* FROM badges b INNER JOIN user_badges ub ON b.badge_id = ub.badge_id WHERE ub.user_id = :userId ORDER BY ub.earned_at DESC LIMIT 1")
    suspend fun getLatestEarnedBadge(userId: Int): Badge?

    @Query("SELECT COUNT(*) > 0 FROM user_badges ub INNER JOIN badges b ON ub.badge_id = b.badge_id WHERE ub.user_id = :userId AND b.badge_key = :badgeKey")
    suspend fun hasBadgeBeenEarned(userId: Int, badgeKey: String): Boolean
}