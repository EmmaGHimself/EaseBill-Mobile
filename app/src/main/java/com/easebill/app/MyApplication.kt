package com.easebill.app

import android.app.Application

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}