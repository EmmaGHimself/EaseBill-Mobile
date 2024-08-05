package com.easebill.app.services

import android.app.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.os.Build
import android.media.RingtoneManager
import android.app.NotificationManager
import com.easebill.app.R
import com.easebill.app.helpers.NotificationHelper
import java.util.*

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotAPI26(remoteMessage)
        } else {
            sendNot(remoteMessage)
        }
    }

    private fun sendNotAPI26(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val title = data["title"]
        val message = data["message"]
        val route = data["data"]
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val helper: NotificationHelper = NotificationHelper(this)
        val builder: Notification.Builder =
            helper.getMySMENotification(title, message, defaultSoundUri, null, data)
        helper.manager?.notify(Random().nextInt(), builder.build())
    }

    private fun sendNot(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val title = data["title"]
        val message = data["message"]
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random().nextInt(), builder.build())
    }

}