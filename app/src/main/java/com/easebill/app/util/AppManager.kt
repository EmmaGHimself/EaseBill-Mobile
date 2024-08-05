package com.easebill.app.util

import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat

class AppManager(private val activity: AppCompatActivity) {
    private val tokenManager = TokenManager(activity)
    private var lastActivityTime: Long = 0
    private lateinit var tokenRefreshTimer: CountDownTimer

    fun initialize(expireAt: String) {
        tokenManager.tokenExpirationTime = timestampToMillis(expireAt)
        lastActivityTime = System.currentTimeMillis()

        startTokenRefreshTimer()
    }

    fun onUserActivity() {
        lastActivityTime = System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    fun timestampToMillis(timestamp: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = format.parse(timestamp)
        return date!!.time
    }

    fun onUserInactivity() {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastActivity = currentTime - lastActivityTime
        if (timeSinceLastActivity > INACTIVITY_THRESHOLD) {
            showInactivePopup()
        }
    }

    private fun showInactivePopup() {
        val sheet = BottomSheetDialog(activity)
       /* val sheetBinding =
            SessionExpiredBottomsheetBinding.inflate(activity.layoutInflater, null, false)
        sheet.setContentView(sheetBinding.root)
        sheet.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        sheetBinding.continueBT.setOnClickListener {
            sheet.dismiss()
            activity.startActivity(Intent(activity, WelcomeBackActivity::class.java))
            activity.finish()
        }*/

        sheet.apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun startTokenRefreshTimer() {
        val timeToExpire = tokenManager.tokenExpirationTime - System.currentTimeMillis()
        val refreshTime = timeToExpire - TOKEN_REFRESH_INTERVAL
        tokenRefreshTimer = object : CountDownTimer(refreshTime, refreshTime) {
            override fun onTick(millisUntilFinished: Long) {
                // Do nothing, just wait for token refresh time
            }

            override fun onFinish() {
                tokenManager.refreshToken()
            }
        }
        tokenRefreshTimer.start()
    }

    companion object {
        private const val INACTIVITY_THRESHOLD = 5 * 60 * 1000 // 5 minutes in milliseconds
        private const val TOKEN_REFRESH_INTERVAL = 5 * 60 * 1000 // 5 minutes in milliseconds
    }
}