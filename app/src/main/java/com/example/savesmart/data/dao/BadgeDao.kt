/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 * - Android Developers (2024) LiveData overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/livedata (Accessed: 24 March 2026).
 */

package com.example.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.UserBadge

/**
 * BadgeDao — Data Access Object for Badge and UserBadge entities (Requirement T02, R20).
 *
 * GitHub commit suggestion:
 *   [db] implement badge DAO for milestone and achievement tracking
 *   - insertBadge() seed badges on database creation
 *   - awardBadge() when user unlocks achievement (R20)
 *   - getEarnedBadgesLive() feeds Rewards screen UI (R20)
 *   - hasBadgeBeenEarned() prevents duplicate awards
 *   Refs: R20, T02, T01
 */
@Dao
interface BadgeDao {
    /**
     * Insert single badge (Requirement R20).
     * @param badge Badge object
     * @return row ID of inserted badge
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: Badge): Long

    /**
     * Batch insert badges (database initialization).
     * @param badges list of Badge objects
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllBadges(badges: List<Badge>)

    /**
     * Fetch all available badges.
     * @return list of all Badge objects
     */
    @Query("SELECT * FROM badges ORDER BY badge_id ASC")
    suspend fun getAllBadges(): List<Badge>

    /**
     * Fetch badge by key (Requirement R20).
     * @param badgeKey  badge identifier (e.g., "FIRST_SAVE")
     * @return Badge object if found, null otherwise
     */
    @Query("SELECT * FROM badges WHERE badge_key = :badgeKey LIMIT 1")
    suspend fun getBadgeByKey(badgeKey: String): Badge?

    /**
     * Award badge to user (Requirement R20).
     * Links Badge to User via UserBadge junction table.
     * @param userBadge UserBadge junction record
     * @return row ID of inserted record
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun awardBadge(userBadge: UserBadge): Long

    /**
     * Fetch list of badge IDs earned by user.
     * @param userId user ID
     * @return list of badge_id integers
     */
    @Query("SELECT badge_id FROM user_badges WHERE user_id = :userId")
    suspend fun getEarnedBadgeIds(userId: Int): List<Int>

    /**
     * Fetch all earned badges for a user as LiveData (Requirement R20, T01).
     * Joins with user_badges to filter user-specific achievements.
     * @param userId user ID
     * @return LiveData list of Badge objects earned by user
     */
    @Query("SELECT b.* FROM badges b INNER JOIN user_badges ub ON b.badge_id = ub.badge_id WHERE ub.user_id = :userId ORDER BY ub.earned_at DESC")
    fun getEarnedBadgesLive(userId: Int): LiveData<List<Badge>>

    /**
     * Fetch most recently earned badge.
     * @param userId user ID
     * @return most recent Badge object, or null
     */
    @Query("SELECT b.* FROM badges b INNER JOIN user_badges ub ON b.badge_id = ub.badge_id WHERE ub.user_id = :userId ORDER BY ub.earned_at DESC LIMIT 1")
    suspend fun getLatestEarnedBadge(userId: Int): Badge?

    /**
     * Check if user already has badge (prevents duplicate awards).
     * @param userId user ID
     * @param badgeKey badge key to check
     * @return true if user has earned this badge before
     */
    @Query("SELECT COUNT(*) > 0 FROM user_badges ub INNER JOIN badges b ON ub.badge_id = b.badge_id WHERE ub.user_id = :userId AND b.badge_key = :badgeKey")
    suspend fun hasBadgeBeenEarned(userId: Int, badgeKey: String): Boolean
}