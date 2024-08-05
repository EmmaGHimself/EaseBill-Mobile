package com.easebill.app.network.api

import com.easebill.app.network.model.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

interface EaseBillAPI {
    /*
        Auth API v1.0
    */

    @POST("auth/login")
    suspend fun login(
        @Body body: UserRequestBodies.SignInBody
    ): Response<Auth>

    @POST("auth/register")
    suspend fun register(
        @Body body: UserRequestBodies.SignUpBody
    ): Response<Auth>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body body: UserRequestBodies.ResetPasswordBody
    ): Response<CustomResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body body: UserRequestBodies.ForgotPassword
    ): Response<CustomResponse>

    @POST("auth/validate-forgot-password")
    suspend fun validateForgotPassword(
        @Body body: UserRequestBodies.ValidateForgotPassword
    ): Response<CustomResponse>

    @POST("users/resend-otp")
    suspend fun resendOTP(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<CustomResponse>

    @POST("users/resend-email-verification")
    suspend fun resendVerification(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<CustomResponse>

    @POST("users/verify-email")
    suspend fun verifyEmail(
        @Header("Authorization") token: String,
        @Body body: UserRequestBodies.VerifyEmailOTP
    ): Response<CustomResponse>

    @POST("users/user-update-transaction-pin")
    suspend fun updateTransactionPIN(
        @Header("Authorization") token: String,
        @Body body: UserRequestBodies.ResetTransactionPINBody
    ): Response<CustomResponse>

    @POST("users/user-create-transaction-pin")
    suspend fun createTransactionPIN(
        @Header("Authorization") token: String,
        @Body body: UserRequestBodies.TransactionPINBody
    ): Response<CustomResponse>

    @POST("users/user-change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body body: UserRequestBodies.ChangePasswordBody
    ): Response<CustomResponse>

    @POST("users/user-profile")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<UserProfile>

    @POST("users/fetch-devices")
    suspend fun getDeviceMapping(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<DeviceMapping>

    @POST("users/delete-device")
    suspend fun deleteDeviceMapping(
        @Header("Authorization") token: String,
        @Body body: UserRequestBodies.DeleteDeviceBody
    ): Response<CustomResponse>

    @POST("users/user-verify-transaction-pin")
    suspend fun verifyTransactionPIN(
        @Header("Authorization") token: String,
        @Body body: UserRequestBodies.TransactionPINBody
    ): Response<CustomResponse>

    @POST("users/user-freeze-transactions")
    suspend fun freezeTransaction(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<CustomResponse>

    @POST("users/users-unfreeze-transactions")
    suspend fun unFreezeTransaction(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<CustomResponse>

    @POST("users/refresh-token")
    suspend fun refreshToken(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<RefreshToken>
}