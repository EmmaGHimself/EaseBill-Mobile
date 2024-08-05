package com.easebill.app.network.api

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

class EaseBillCloudConfig private constructor() {
    val baseUrl: String?
        get() = Companion.baseUrl

    companion object {
        @JvmStatic
        var instance: EaseBillCloudConfig? = null
            get() {
                if (field == null) {
                    synchronized(EaseBillCloud::class.java) {
                        if (field == null) {
                            field = EaseBillCloudConfig()
                        }
                    }
                }
                return field
            }
            private set
        private var baseUrl: String? = null
    }

    init {
        /*if (BuildConfig.DEBUG) {
            Companion.baseUrl = BuildConfig.DEV_API_BASE_URL
        } else {
            Companion.baseUrl = BuildConfig.PRODUCTION_API_BASE_URL
        }*/
        Companion.baseUrl = "https://seahorse-app-tw2yx.ondigitalocean.app/v1/api/"
    }
}