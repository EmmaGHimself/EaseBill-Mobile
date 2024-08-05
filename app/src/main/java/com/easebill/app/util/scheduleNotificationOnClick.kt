package com.easebill.app.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.easebill.app.network.room.AppDatabase
import com.easebill.app.network.room.entity.Notification
import com.easebill.app.services.NotificationReceiver
import com.easebill.app.sharedpref.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ScheduleNotificationOnClick(context: Context) {
    private val applicationContext: Context = context.applicationContext

    private val db = AppDatabase.getDatabase(applicationContext)
    private val notificationDao = db.notificationDao() // Obtain the DAO instance

    @SuppressLint("NewApi")
    suspend fun scheduleNotification(title: String, message: String) {
        withContext(Dispatchers.IO) {
            val notification =
                Notification(
                    userId = SharedPreferencesHelper.getInstance(applicationContext)!!
                        .getStoredUser()!!.userId,
                    title = title,
                    description = message,
                    createdAt = LocalDateTime.now().toString()
                )
            notificationDao.insert(notification)

            val intent = Intent(applicationContext, NotificationReceiver::class.java)
            intent.putExtra("title", title)
            intent.putExtra("message", message)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1000, // Example: trigger notification after 1 seconds
                pendingIntent
            )
        }
    }
}