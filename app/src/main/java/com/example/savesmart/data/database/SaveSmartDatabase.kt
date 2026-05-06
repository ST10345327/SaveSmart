/**
 * Reference:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 * - Android Developers (2024) Kotlin coroutines on Android. Google LLC.
 *   Available at: https://developer.android.com/kotlin/coroutines (Accessed: 24 March 2026).
 */

package com.example.savesmart.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.savesmart.data.dao.BadgeDao
import com.example.savesmart.data.dao.CategoryDao
import com.example.savesmart.data.dao.ExpenseDao
import com.example.savesmart.data.dao.UserDao
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.entity.Expense
import com.example.savesmart.data.entity.User
import com.example.savesmart.data.entity.UserBadge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * SaveSmartDatabase — Room database initialization and schema definition (Requirement T02).
 *
 * GitHub commit suggestion:
 *   [db] implement room database with multi-entity schema and badge initialization
 *   - Entities: User, Category, Expense, Badge, UserBadge
 *   - Version 3 with fallbackToDestructiveMigration for development
 *   - DatabaseCallback populates badges on creation
 *   - Singleton pattern with thread-safe getInstance()
 *   Refs: T02, T01
 */
@Database(
    entities = [
        User::class,
        Category::class,
        Expense::class,
        Badge::class,
        UserBadge::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SaveSmartDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        private const val TAG = "SaveSmartDatabase"
        // Renamed to v3 to force a clean slate for users unable to clear app userData
        private const val DATABASE_NAME = "savesmart_v3.db"

        @Volatile
        private var INSTANCE: SaveSmartDatabase? = null

        /**
         * Get database singleton instance (thread-safe) (Requirement T02).
         * @param context Android context
         * @return SaveSmartDatabase singleton
         */
        fun getInstance(context: Context): SaveSmartDatabase {
            Log.d(TAG, "getInstance() entry")
            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "getInstance() creating new database instance")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaveSmartDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                Log.d(TAG, "getInstance() complete")
                instance
            }
        }

        /**
         * Alias for getInstance() for backwards compatibility.
         * @param context Android context
         * @return SaveSmartDatabase singleton
         */
        fun getDatabase(context: Context): SaveSmartDatabase = getInstance(context)
    }

    /**
     * DatabaseCallback — Runs on database creation to seed default data.
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        /**
         * Called on first database creation.
         * Populates default badges for gamification (Requirement R20).
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "DatabaseCallback.onCreate() entry")
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateBadges(database.badgeDao())
                    Log.d(TAG, "DatabaseCallback.onCreate() complete")
                }
            }
        }

        /**
         * Populate default badges on database creation (Requirement R20).
         * @param badgeDao BadgeDao for insertion
         */
        private suspend fun populateBadges(badgeDao: BadgeDao) {
            Log.d(TAG, "populateBadges() entry")
            val badges = listOf(
                Badge(badgeKey = "FIRST_SAVE", name = "First Save", description = "Saved money for the first time", iconResName = "ic_badge_first_save", pointsReward = 10),
                Badge(badgeKey = "STREAK_7", name = "7-Day Streak", description = "Logged expenses 7 days in a row", iconResName = "ic_badge_streak_7", pointsReward = 50),
                Badge(badgeKey = "STREAK_30", name = "30-Day Streak", description = "Logged expenses 30 days in a row", iconResName = "ic_badge_streak_30", pointsReward = 200),
                Badge(badgeKey = "BUDGET_MASTER", name = "Budget Master", description = "Stayed under budget 3 months in a row", iconResName = "ic_badge_budget_master", pointsReward = 100),
                Badge(badgeKey = "QUICK_LOGGER", name = "Quick Logger", description = "Logged 10 expenses in one day", iconResName = "ic_badge_quick_logger", pointsReward = 25),
                Badge(badgeKey = "ZERO_SPEND", name = "Zero Spend Day", description = "Logged a day with no spending", iconResName = "ic_badge_zero_spend", pointsReward = 15),
                Badge(badgeKey = "GOAL_CRUSHER", name = "Goal Crusher", description = "All categories within limits for a full month", iconResName = "ic_badge_goal_crusher", pointsReward = 100)
            )
            badgeDao.insertAllBadges(badges)
            Log.d(TAG, "populateBadges() complete — ${badges.size} badges seeded")
        }
    }
}