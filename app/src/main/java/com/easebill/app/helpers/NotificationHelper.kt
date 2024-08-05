package com.easebill.app.helpers

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.annotation.TargetApi
import android.app.*
import android.os.Build
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Intent
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import java.io.IOException
import java.net.URL
import android.provider.Settings;
import com.easebill.app.R
import com.easebill.app.ui.SplashActivity

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val myChannel = NotificationChannel(
            EASEBILL_CHANNEL_ID, EASEBILL_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        myChannel.enableLights(true)
        myChannel.enableVibration(true)
        myChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(myChannel)
    }

    val manager: NotificationManager?
        get() {
            if (notificationManager == null) notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            return notificationManager
        }

    @TargetApi(Build.VERSION_CODES.O)
    fun getMySMENotification(
        title: String?,
        message: String?,
        soundUri: Uri?,
        url: String?,
        data: Map<String?, String?>
    ): Notification.Builder {
        var bmp: Bitmap? = null
        try {
            val `in` = URL(url).openStream()
            bmp = BitmapFactory.decodeStream(`in`)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, SplashActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        resultIntent.putExtra("id", data["id"])

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val builder = Notification.Builder(applicationContext, EASEBILL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setLargeIcon(bmp).setPriority(Notification.PRIORITY_DEFAULT)
            .setSound(soundUri)
        builder.setContentIntent(resultPendingIntent)
        return builder
    }

    @SuppressLint("NewApi")
    fun createNotification(title: String?, message: String?) {
        val intent = Intent(this, SplashActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val resultPendingIntent = PendingIntent.getActivity(
            this,
            0,  /* Request code */intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val mBuilder = Notification.Builder(this, EASEBILL_CHANNEL_ID)
        mBuilder
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setPriority(Notification.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)

        val mNotificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel =
            NotificationChannel(EASEBILL_CHANNEL_ID, "Follow up", importance)
        notificationChannel.enableLights(true)
        notificationChannel.setShowBadge(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        mBuilder.setChannelId(EASEBILL_CHANNEL_ID)
        mNotificationManager.createNotificationChannel(notificationChannel)
        mNotificationManager.notify(0,  /* Request Code */mBuilder.build())
    }

    companion object {
        private const val EASEBILL_CHANNEL_ID = "com.easebill.app"
        private const val EASEBILL_CHANNEL_NAME = "EASEBILL"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }
}