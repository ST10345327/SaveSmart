package com.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savesmart.data.entity.User

/**
 * UserDao — Data Access Object for user authentication operations.
 *
 * References:
 * - Android Developers (2024) Accessing data using Room DAOs. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/accessing-data
 *   (Accessed: 24 March 2026).
 * - Android Developers (2024) Write asynchronous DAO queries. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/async-queries
 *   (Accessed: 24 March 2026).
 *
 * All suspend functions run on a background coroutine — never on the main thread.
 * LiveData queries automatically notify observers on data changes.
 */
@Dao
interface UserDao {

    // ── INSERT ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new user into the database (R01).
     * ABORT strategy returns -1L if username already exists (unique index).
     *
     * @param user The User entity to insert.
     * @return The new row ID, or -1 if insert failed (duplicate username).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    // ── SELECT ────────────────────────────────────────────────────────────────

    /**
     * Finds a user by username and password hash for login validation (R02).
     * Returns null if no matching user is found (invalid credentials).
     *
     * @param username The entered username.
     * @param passwordHash SHA-256 hash of the entered password.
     * @return The matching User, or null if credentials are invalid.
     */
    @Query("""
        SELECT * FROM users 
        WHERE username = :username 
        AND password_hash = :passwordHash
        LIMIT 1
    """)
    suspend fun getUserByCredentials(username: String, passwordHash: String): User?

    /**
     * Finds a user by username alone — used for duplicate check during
     * registration (R03).
     *
     * @param username The username to check.
     * @return The User if found, null if username is available.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    /**
     * Retrieves a user by their primary key (R02).
     * Used to restore session from SharedPreferences on app launch.
     *
     * @param userId The user's primary key.
     * @return The User entity, or null if not found.
     */
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    /**
     * LiveData version of getUserById — automatically updates UI
     * when user data changes e.g. points update (R19).
     *
     * @param userId The user's primary key.
     * @return LiveData wrapping the User entity.
     */
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    fun getUserByIdLive(userId: Int): LiveData<User?>

    /**
     * Gets all users ordered by total points descending — used for
     * the leaderboard (R22).
     *
     * @return LiveData list of all users ranked by points.
     */
    @Query("SELECT * FROM users ORDER BY total_points DESC")
    fun getAllUsersRankedLive(): LiveData<List<User>>

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Updates user data — used when points or level change (R19, R21).
     *
     * @param user The updated User entity.
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Adds points to a user's total and updates their level (R19).
     * Level thresholds: 1=0, 2=100, 3=250, 4=500, 5=1000 (R21).
     *
     * @param userId The user's primary key.
     * @param points Number of points to add.
     */
    @Query("""
        UPDATE users 
        SET total_points = total_points + :points,
            level = CASE
                WHEN total_points + :points >= 1000 THEN 5
                WHEN total_points + :points >= 500  THEN 4
                WHEN total_points + :points >= 250  THEN 3
                WHEN total_points + :points >= 100  THEN 2
                ELSE 1
            END
        WHERE user_id = :userId
    """)
    suspend fun addPoints(userId: Int, points: Int)

    /**
     * Marks the onboarding flow as complete for a user (R23).
     * Called after the user completes or skips the 3-step onboarding.
     *
     * @param userId The user's primary key.
     */
    @Query("UPDATE users SET onboarding_complete = 1 WHERE user_id = :userId")
    suspend fun markOnboardingComplete(userId: Int)
}
