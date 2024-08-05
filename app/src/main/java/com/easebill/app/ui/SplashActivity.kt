package com.easebill.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.easebill.app.sharedpref.SharedPreferencesHelper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val setTransactionPIN = SharedPreferencesHelper.getInstance(applicationContext)
            ?.getStoredUser()?.hasSetTransactionPin

        if (setTransactionPIN == "Y") {
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        } else {
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        }
    }
}