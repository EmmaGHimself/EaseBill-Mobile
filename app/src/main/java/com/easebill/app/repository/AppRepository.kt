package com.easebill.app.repository

import android.content.Context
import com.easebill.app.network.api.EaseBillCloud
import com.easebill.app.network.model.UserRequestBodies
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

class AppRepository(private val context: Context) {


    // Create an empty JSON object
    val emptyJsonObject = "{}"

    // Convert the empty JSON object to RequestBody
    val requestBody = emptyJsonObject.toRequestBody("application/json".toMediaType())


    /* Authentication Endpoints v1.0 */

    suspend fun login(body: UserRequestBodies.SignInBody) =
        EaseBillCloud.easeBillAPI.login(body)

    suspend fun register(body: UserRequestBodies.SignUpBody) =
        EaseBillCloud.easeBillAPI.register(body)

    suspend fun resetPassword(body: UserRequestBodies.ResetPasswordBody) =
        EaseBillCloud.easeBillAPI.resetPassword(body)

    suspend fun forgotPassword(body: UserRequestBodies.ForgotPassword) =
        EaseBillCloud.easeBillAPI.forgotPassword(body)

    suspend fun validateForgotPassword(body: UserRequestBodies.ValidateForgotPassword) =
        EaseBillCloud.easeBillAPI.validateForgotPassword(body)

    suspend fun resendOTP(token: String) =
        EaseBillCloud.easeBillAPI.resendOTP(token, requestBody)

    suspend fun resendVerification(token: String) =
        EaseBillCloud.easeBillAPI.resendVerification(
            token,
            requestBody
        )

    suspend fun verifyEmail(
        token: String,
        body: UserRequestBodies.VerifyEmailOTP
    ) =
        EaseBillCloud.easeBillAPI.verifyEmail(token, body)

    suspend fun updateTransactionPIN(
        token: String,
        body: UserRequestBodies.ResetTransactionPINBody
    ) =
        EaseBillCloud.easeBillAPI.updateTransactionPIN(token, body)

    suspend fun changePassword(
        token: String,
        body: UserRequestBodies.ChangePasswordBody
    ) =
        EaseBillCloud.easeBillAPI.changePassword(token, body)

    suspend fun deleteDeviceMapping(
        token: String,
        body: UserRequestBodies.DeleteDeviceBody
    ) =
        EaseBillCloud.easeBillAPI.deleteDeviceMapping(token, body)

    suspend fun getProfile(
        token: String
    ) =
        EaseBillCloud.easeBillAPI.getProfile(token, requestBody)

    suspend fun getDeviceMapping(
        token: String
    ) =
        EaseBillCloud.easeBillAPI.getDeviceMapping(token, requestBody)

    suspend fun freezeTransaction(
        token: String
    ) =
        EaseBillCloud.easeBillAPI.freezeTransaction(
            token,
            requestBody
        )

    suspend fun unFreezeTransaction(
        token: String
    ) =
        EaseBillCloud.easeBillAPI.unFreezeTransaction(
            token,
            requestBody
        )

    suspend fun verifyTransactionPIN(
        token: String,
        body: UserRequestBodies.TransactionPINBody
    ) =
        EaseBillCloud.easeBillAPI.verifyTransactionPIN(token, body)

    suspend fun refreshToken(
        token: String
    ) =
        EaseBillCloud.easeBillAPI.refreshToken(token, requestBody)


    suspend fun createTransactionPIN(
        token: String,
        body: UserRequestBodies.TransactionPINBody
    ) =
        EaseBillCloud.easeBillAPI.createTransactionPIN(token, body)
}