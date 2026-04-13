package com.savesmart.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.savesmart.data.dao.BadgeDao
import com.savesmart.data.dao.CategoryDao
import com.savesmart.data.dao.ExpenseDao
import com.savesmart.data.dao.UserDao
import com.savesmart.data.entity.Badge
import com.savesmart.data.entity.Category
import com.savesmart.data.entity.Expense
import com.savesmart.data.entity.User
import com.savesmart.data.entity.UserBadge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * SaveSmartDatabase — Main Room database for the SaveSmart application.
 *
 * References:
 * - Android Developers (2024) Room persistence library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room
 *   (Accessed: 24 March 2026).
 * - Android Developers (2024) Prepopulate your Room database. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room/prepopulate
 *   (Accessed: 24 March 2026).
 * - Stack Overflow (2020) Room database singleton pattern with companion object.
 *   Available at: https://stackoverflow.com/questions/44167111
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - Singleton pattern ensures only one database instance exists (T02).
 * - Pre-populated with badge definitions on first creation (R20).
 * - exportSchema = false to avoid schema export file requirements in tests.
 * - fallbackToDestructiveMigration used during development only — remove
 *   before production release.
 * - Version 1: initial schema.
 */
@Database(
    entities = [
        User::class,
        Category::class,
        Expense::class,
        Badge::class,
        UserBadge::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SaveSmartDatabase : RoomDatabase() {

    // ── Abstract DAO accessors ────────────────────────────────────────────────

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun badgeDao(): BadgeDao

    // ── Singleton companion object ────────────────────────────────────────────

    companion object {

        private const val TAG = "SaveSmartDatabase"
        private const val DATABASE_NAME = "savesmart.db"

        /**
         * Volatile ensures INSTANCE is immediately visible to all threads.
         * Reference: Android Developers (2024) Thread safety for Room.
         */
        @Volatile
        private var INSTANCE: SaveSmartDatabase? = null

        /**
         * Returns the singleton database instance, creating it if necessary.
         * Uses double-checked locking for thread safety.
         *
         * @param context Application context — must be Application context to
         *   avoid memory leaks.
         * @return The singleton SaveSmartDatabase instance.
         */
        fun getInstance(context: Context): SaveSmartDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "Creating new database instance")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaveSmartDatabase::class.java,
                    DATABASE_NAME
                )
                    // Pre-populate badges when database is first created (R20)
                    .addCallback(DatabaseCallback())
                    // Destructive migration during development only
                    // TODO: Replace with proper migrations before release
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Destroys the singleton instance — used in unit tests only (T05).
         * Ensures a clean database for each test run.
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    // ── Database Callback ─────────────────────────────────────────────────────

    /**
     * DatabaseCallback — Called when the database is first created.
     * Pre-populates the badges table with all defined milestone badges (R20).
     *
     * Reference:
     * - Android Developers (2024) RoomDatabase.Callback. Google LLC.
     *   Available at: https://developer.android.com/reference/androidx/room/RoomDatabase.Callback
     *   (Accessed: 24 March 2026).
     */
    private class DatabaseCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "Database created — pre-populating badges")
            // Run pre-population on IO dispatcher
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateBadges(database.badgeDao())
                }
            }
        }

        /**
         * Inserts all predefined badge definitions into the badges table (R20).
         * Called once on database creation.
         *
         * @param badgeDao The BadgeDao to use for insertion.
         */
        private suspend fun populateBadges(badgeDao: BadgeDao) {
            val badges = listOf(
                Badge(
                    badgeKey = "FIRST_SAVE",
                    name = "First Save",
                    description = "Saved money for the first time",
                    iconResName = "ic_badge_first_save",
                    pointsReward = 10
                ),
                Badge(
                    badgeKey = "STREAK_7",
                    name = "7-Day Streak",
                    description = "Logged expenses 7 days in a row",
                    iconResName = "ic_badge_streak_7",
                    pointsReward = 50
                ),
                Badge(
                    badgeKey = "STREAK_30",
                    name = "30-Day Streak",
                    description = "Logged expenses 30 days in a row",
                    iconResName = "ic_badge_streak_30",
                    pointsReward = 200
                ),
                Badge(
                    badgeKey = "BUDGET_MASTER",
                    name = "Budget Master",
                    description = "Stayed under budget 3 months in a row",
                    iconResName = "ic_badge_budget_master",
                    pointsReward = 100
                ),
                Badge(
                    badgeKey = "QUICK_LOGGER",
                    name = "Quick Logger",
                    description = "Logged 10 expenses in one day",
                    iconResName = "ic_badge_quick_logger",
                    pointsReward = 25
                ),
                Badge(
                    badgeKey = "ZERO_SPEND",
                    name = "Zero Spend Day",
                    description = "Logged a day with no spending",
                    iconResName = "ic_badge_zero_spend",
                    pointsReward = 15
                ),
                Badge(
                    badgeKey = "GOAL_CRUSHER",
                    name = "Goal Crusher",
                    description = "All categories within limits for a full month",
                    iconResName = "ic_badge_goal_crusher",
                    pointsReward = 100
                )
            )

            badgeDao.insertAllBadges(badges)
            Log.d(TAG, "Pre-populated ${badges.size} badges successfully")
        }
    }
}
