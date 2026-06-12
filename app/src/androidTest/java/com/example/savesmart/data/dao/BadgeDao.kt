package com.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savesmart.data.entity.Badge
import com.savesmart.data.entity.UserBadge

/**
 * BadgeDao — Data Access Object for gamification badge operations.
 *
 * References:
 * - Android Developers (2024) Accessing data using Room DAOs. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/accessing-data
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - Badge definitions are pre-populated at DB creation and never modified.
 * - UserBadge records are inserted when a badge is earned — never deleted.
 * - IGNORE conflict strategy on UserBadge prevents duplicate badge awards.
 */
@Dao
interface BadgeDao {

    // ── BADGE DEFINITIONS ─────────────────────────────────────────────────────

    /**
     * Inserts a badge definition (called during DB pre-population only).
     *
     * @param badge The Badge entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: Badge): Long

    /**
     * Inserts multiple badge definitions at database creation.
     *
     * @param badges List of Badge entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllBadges(badges: List<Badge>)

    /**
     * Returns all badge definitions.
     * Used to build the full badge grid on the Rewards screen (R20).
     *
     * @return List of all Badge definitions.
     */
    @Query("SELECT * FROM badges ORDER BY badge_id ASC")
    suspend fun getAllBadges(): List<Badge>

    /**
     * Returns a badge by its unique key (R20).
     * Used to look up badge ID before inserting a UserBadge.
     *
     * @param badgeKey The unique badge key e.g. "FIRST_SAVE".
     * @return The Badge entity, or null if not found.
     */
    @Query("SELECT * FROM badges WHERE badge_key = :badgeKey LIMIT 1")
    suspend fun getBadgeByKey(badgeKey: String): Badge?

    // ── USER BADGES ───────────────────────────────────────────────────────────

    /**
     * Awards a badge to a user (R20).
     * IGNORE conflict strategy silently prevents duplicate badge awards
     * since the primary key is (user_id, badge_id).
     *
     * @param userBadge The UserBadge junction record to insert.
     * @return The new row ID, or -1 if already earned.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun awardBadge(userBadge: UserBadge): Long

    /**
     * Returns all badge IDs earned by a user (R20).
     * Used to determine which badges to show as earned vs locked
     * in the Rewards screen badge grid.
     *
     * @param userId The user's ID.
     * @return Set of earned badge IDs.
     */
    @Query("""
        SELECT badge_id FROM user_badges 
        WHERE user_id = :userId
    """)
    suspend fun getEarnedBadgeIds(userId: Int): List<Int>

    /**
     * Returns full badge details for all badges earned by a user (R20).
     * Used to display earned badges in full colour on the Rewards screen.
     *
     * @param userId The user's ID.
     * @return LiveData list of earned Badge objects.
     */
    @Query("""
        SELECT b.* FROM badges b
        INNER JOIN user_badges ub ON b.badge_id = ub.badge_id
        WHERE ub.user_id = :userId
        ORDER BY ub.earned_at DESC
    """)
    fun getEarnedBadgesLive(userId: Int): LiveData<List<Badge>>

    /**
     * Returns the most recently earned badge for a user (R20).
     * Displayed in the "Latest Badge" section on the Rewards screen.
     *
     * @param userId The user's ID.
     * @return The most recently earned Badge, or null.
     */
    @Query("""
        SELECT b.* FROM badges b
        INNER JOIN user_badges ub ON b.badge_id = ub.badge_id
        WHERE ub.user_id = :userId
        ORDER BY ub.earned_at DESC
        LIMIT 1
    """)
    suspend fun getLatestEarnedBadge(userId: Int): Badge?

    /**
     * Checks whether a specific badge has already been earned (R20).
     * Used before attempting to award a badge to avoid duplicate processing.
     *
     * @param userId The user's ID.
     * @param badgeKey The badge key to check e.g. "STREAK_7".
     * @return True if the badge has already been earned.
     */
    @Query("""
        SELECT COUNT(*) > 0 FROM user_badges ub
        INNER JOIN badges b ON ub.badge_id = b.badge_id
        WHERE ub.user_id = :userId
        AND b.badge_key = :badgeKey
    """)
    suspend fun hasBadgeBeenEarned(userId: Int, badgeKey: String): Boolean
}
