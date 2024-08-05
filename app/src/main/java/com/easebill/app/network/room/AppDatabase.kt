package com.easebill.app.network.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.easebill.app.network.room.dao.NotificationDao
import com.easebill.app.network.room.dao.RatingDao
import com.easebill.app.network.room.entity.Notification
import com.easebill.app.network.room.entity.Rating
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Notification::class, Rating::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun ratingDao(): RatingDao

    // Define a method to clear all data
    fun clearAllData() {
        // Execute database operations in a background thread
        // to avoid blocking the main thread
        CoroutineScope(Dispatchers.IO).launch {
            notificationDao().deleteAllNotifications()
            ratingDao().deleteAllRatings()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sme_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}