package com.example.savesmart.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.savesmart.data.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND password_hash = :passwordHash LIMIT 1")
    suspend fun getUserByCredentials(username: String, passwordHash: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    fun getUserByIdLive(userId: Int): LiveData<User?>

    @Query("SELECT * FROM users ORDER BY total_points DESC")
    fun getAllUsersRankedLive(): LiveData<List<User>>

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET total_points = total_points + :points WHERE user_id = :userId")
    suspend fun addPoints(userId: Int, points: Int)

    @Query("UPDATE users SET onboarding_complete = 1 WHERE user_id = :userId")
    suspend fun markOnboardingComplete(userId: Int)
}