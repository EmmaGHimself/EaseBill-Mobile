package com.easebill.app.viewmodel.v1

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.easebill.app.MyApplication
import com.easebill.app.R
import com.easebill.app.network.model.Auth
import com.easebill.app.network.model.CustomResponse
import com.easebill.app.network.model.ErrorMessage
import com.easebill.app.network.model.RefreshToken
import com.easebill.app.network.model.UserProfile
import com.easebill.app.network.model.UserRequestBodies
import com.easebill.app.repository.AppRepository
import com.easebill.app.util.Constants
import com.easebill.app.util.Resources
import com.easebill.app.util.Utils
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response


/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */
class AuthViewModel(
    app: Application,
    private val repository: AppRepository
) : AndroidViewModel(app) {

    private inline fun <reified T> handleApiCall(
        crossinline apiCall: suspend () -> Response<T>
    ): Flow<Resources<T>> = flow {
        emit(Resources.loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = apiCall.invoke()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    if (data is Auth || data is CustomResponse || data is RefreshToken || data is UserProfile
                    ) {
                        emit(Resources.success("success", data))
                        Log.d("RESPONSE:{}{}", "SUCCESS: ${data}")
                    } else {
                        emit(Resources.error("Unknown error occurred"))
                    }
                } else {
                    val gson = Gson()
                    val message: ErrorMessage = gson.fromJson(
                        response.errorBody()?.charStream(),
                        ErrorMessage::class.java
                    )
                    if (message.message == null) {
                        emit(Resources.error("The service is down, contact support"))
                    }
                    emit(Resources.error(message.message.toString()))
                    Log.d("_&check_device", "error: ${response.raw().networkResponse}")
                }
            } else emit(Resources.error(Constants.NO_INTERNET))
        } catch (e: Exception) {
            emit(
                Resources.error(
                    getApplication<MyApplication>()
                        .getString(R.string.network_failure)
                )
            )
            Log.d("_&check_device", "error: $e")
        }
    }

    fun userLogin(
        body: UserRequestBodies.SignInBody
    ): Flow<Resources<Auth>> = handleApiCall {
        repository.login(body)
    }

    fun userRegistration(
        body: UserRequestBodies.SignUpBody
    ): Flow<Resources<Auth>> = handleApiCall {
        repository.register(body)
    }

    fun resetPassword(
        body: UserRequestBodies.ResetPasswordBody
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.resetPassword(body)
    }

    fun forgotPassword(
        body: UserRequestBodies.ForgotPassword
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.forgotPassword(body)
    }

    fun verifyEmail(
        token: String,
        body: UserRequestBodies.VerifyEmailOTP
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.verifyEmail("Bearer $token", body)
    }

    fun validateForgotPassword(
        body: UserRequestBodies.ValidateForgotPassword
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.validateForgotPassword(body)
    }

    fun resendOTP(
        token: String
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.resendOTP("Bearer $token")
    }

    fun resendVerification(
        token: String
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.resendVerification("Bearer $token")
    }

    fun getProfile(
        token: String
    ): Flow<Resources<UserProfile>> = handleApiCall {
        repository.getProfile("Bearer $token")
    }

    fun refreshToken(
        token: String
    ): Flow<Resources<RefreshToken>> = handleApiCall {
        repository.refreshToken("Bearer $token")
    }

}