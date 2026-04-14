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

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        private const val TAG = "SaveSmartDatabase"
        private const val DATABASE_NAME = "savesmart.db"

        @Volatile
        private var INSTANCE: SaveSmartDatabase? = null

        fun getInstance(context: Context): SaveSmartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaveSmartDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context): SaveSmartDatabase = getInstance(context)
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateBadges(database.badgeDao())
                }
            }
        }

        private suspend fun populateBadges(badgeDao: BadgeDao) {
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
        }
    }
}