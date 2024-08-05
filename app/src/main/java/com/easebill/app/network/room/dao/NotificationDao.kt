package com.easebill.app.network.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.easebill.app.network.room.entity.Notification

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: Notification): Long

    @Query("SELECT * FROM notification WHERE userId = :userId")
    suspend fun getNotificationsByUserId(userId: String): List<Notification>

    @Query("DELETE FROM notification WHERE userId = :userId")
    suspend fun deleteNotificationsByUserId(userId: String)

    @Query("DELETE FROM notification WHERE userId = :userId AND id = :notificationId")
    suspend fun deleteNotificationsByUserIdAndNotificationId(userId: String, notificationId: String)

    @Query("DELETE FROM notification")
    suspend fun deleteAllNotifications()
}
