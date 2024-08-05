package com.easebill.app.network.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

class EaseBillCloud {
    companion object {
        private val easeBillCloud by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // Set custom timeouts
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .readTimeout(10, TimeUnit.MINUTES) // Increase read timeout to 10 seconds
                .writeTimeout(10, TimeUnit.MINUTES) // Increase write timeout to 10 seconds
                .connectTimeout(10, TimeUnit.MINUTES) // Increase connect timeout to 10 seconds
                .build()

            Retrofit.Builder()
                .baseUrl(EaseBillCloudConfig.instance!!.baseUrl.toString())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }


        val easeBillAPI: EaseBillAPI by lazy {
            easeBillCloud.create(EaseBillAPI::class.java)
        }
    }
}