package com.easebill.app.sharedpref

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.easebill.app.network.api.EaseBillCloud
import com.easebill.app.network.model.User
import com.google.gson.Gson

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

class SharedPreferencesHelper(context: Context) {

    fun storeCurrentUser(data: User?) {
        val gson = Gson()
        val json = gson.toJson(data)
        prefs.edit().putString(SharedPreferenceContract.USER, json).apply()
        Log.d("Json response", json.toString())
    }


    fun storeCurrentUserToken(token: String?) {
        prefs.edit().putString(SharedPreferenceContract.JWT_TOKEN, token).apply()
    }

    fun getStoredUserToken(): String? {
        return prefs.getString(SharedPreferenceContract.JWT_TOKEN, "")
    }

    fun storeCurrentUserTokenExpire(expireAt: String?) {
        prefs.edit().putString(SharedPreferenceContract.TOKEN_EXPIRES, expireAt).apply()
    }

    fun getStoredUserTokenExpire(): String? {
        return prefs.getString(SharedPreferenceContract.TOKEN_EXPIRES, "")
    }

    fun storeCurrentUserImage(image: String?) {
        prefs.edit().putString(SharedPreferenceContract.USER_IMAGE, image).apply()
    }

    fun getStoredUserImage(): String? {
        return prefs.getString(SharedPreferenceContract.USER_IMAGE, "")
    }

    fun storeCurrentUserDeviceID(deviceID: String?) {
        prefs.edit().putString(SharedPreferenceContract.DEVICE_ID, deviceID).apply()
    }

    fun getStoredUserDeviceID(): String? {
        return prefs.getString(SharedPreferenceContract.DEVICE_ID, "")
    }

    fun storeCurrentUserSetPIN(setPIN: String?) {
        prefs.edit().putString(SharedPreferenceContract.HAS_SET_PIN, setPIN).apply()
    }

    fun getStoredUserSetPIN(): String? {
        return prefs.getString(SharedPreferenceContract.HAS_SET_PIN, "")
    }

    fun getStoredUser(): User? {
        val gson = Gson()
        val json: String? = prefs.getString(SharedPreferenceContract.USER, "")
        return gson.fromJson(
            json,
            User::class.java
        )
    }

    fun clearAll() {
        prefs.edit().putString(SharedPreferenceContract.USER, "").apply()
        prefs.edit().putString(SharedPreferenceContract.JWT_TOKEN, "").apply()
        prefs.edit().putString(SharedPreferenceContract.TOKEN_EXPIRES, "").apply()
        prefs.edit().putString(SharedPreferenceContract.USER_IMAGE, "").apply()
        prefs.edit().putString(SharedPreferenceContract.DEVICE_ID, "").apply()
        prefs.edit().putString(SharedPreferenceContract.HAS_SET_PIN, "").apply()
    }

    companion object {
        private var instance: SharedPreferencesHelper? = null
        lateinit var prefs: SharedPreferences
        var appContext: Context? = null
        private const val PREFERENCE = "EASEBILL"

        fun getInstance(context: Context): SharedPreferencesHelper? {
            if (instance == null) {
                synchronized(EaseBillCloud::class.java) {
                    if (instance == null) {
                        instance = SharedPreferencesHelper(context)
                    }
                }
            }
            return instance
        }

        fun isConnectingToInternet(context: Context): Boolean {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
            return false
        }

        fun setSharedPreference(
            context: Context, name: String?,
            value: String?
        ) {
            appContext = context
            val settings = context.getSharedPreferences(PREFERENCE, 0)
            val editor = settings.edit()
            editor.putString(name, value)
            editor.apply()
        }

        fun getSharedPreferences(context: Context, name: String?): String? {
            val settings = context
                .getSharedPreferences(PREFERENCE, 0)
            return settings.getString(name, "")
        }
    }

    init {
        prefs = context.getSharedPreferences(SharedPreferenceContract.E_EASEBILL_PREF, 0)
    }
}