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
import androidx.room.Update
import com.example.savesmart.data.entity.User

/**
 * UserDao — Data Access Object for User entity (Requirement T02).
 *
 * GitHub commit suggestion:
 *   [db] implement user DAO with credential queries and gamification updates
 *   - insertUser() enforces username uniqueness via Room constraint
 *   - getUserByCredentials() for login authentication (R02)
 *   - addPoints() increments user score for gamification (R19)
 *   Refs: R01, R02, R19, R21, T02, T05
 */
@Dao
interface UserDao {
    /**
     * Insert new user into database (Requirement R01).
     * @param user User object to insert
     * @return row ID of inserted user
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    /**
     * Authenticate user by username and hashed password (Requirement R02).
     * @param username user's username
     * @param passwordHash SHA-256 hashed password
     * @return User object if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE username = :username AND password_hash = :passwordHash LIMIT 1")
    suspend fun getUserByCredentials(username: String, passwordHash: String): User?

    /**
     * Check if username exists (duplicate prevention) (Requirement R03).
     * @param username username to check
     * @return User object if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    /**
     * Fetch user by ID.
     * @param userId primary key
     * @return User object if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    /**
     * Fetch user by ID as LiveData for reactive updates (Requirement T01).
     * @param userId primary key
     * @return LiveData<User> for auto-updating UI
     */
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    fun getUserByIdLive(userId: Int): LiveData<User?>

    /**
     * Fetch all users ranked by points descending (Requirement R22).
     * @return LiveData list of users sorted by total_points DESC
     */
    @Query("SELECT * FROM users ORDER BY total_points DESC")
    fun getAllUsersRankedLive(): LiveData<List<User>>

    /**
     * Update user record (profile info, settings, etc.).
     * @param user User object with updated fields
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Award points to user for achievement (Requirement R19).
     * @param userId recipient user ID
     * @param points points to add
     */
    @Query("UPDATE users SET total_points = total_points + :points WHERE user_id = :userId")
    suspend fun addPoints(userId: Int, points: Int)

    /**
     * Mark onboarding complete (Requirement R23).
     * @param userId user ID to update
     */
    @Query("UPDATE users SET onboarding_complete = 1 WHERE user_id = :userId")
    suspend fun markOnboardingComplete(userId: Int)
}