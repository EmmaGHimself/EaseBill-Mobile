package com.easebill.app.viewmodel.v1

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.easebill.app.MyApplication
import com.easebill.app.R
import com.easebill.app.network.model.Auth
import com.easebill.app.network.model.CustomResponse
import com.easebill.app.network.model.DeviceMapping
import com.easebill.app.network.model.ErrorMessage
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
class UserViewModel(
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
                    if (data is Auth || data is CustomResponse || data is DeviceMapping
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


    fun updateTransactionPIN(
        token: String,
        body: UserRequestBodies.ResetTransactionPINBody
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.updateTransactionPIN("Bearer $token", body)
    }

    fun changePassword(
        token: String,
        body: UserRequestBodies.ChangePasswordBody
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.changePassword("Bearer $token", body)
    }

    fun deleteDeviceMapping(
        token: String,
        body: UserRequestBodies.DeleteDeviceBody
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.deleteDeviceMapping("Bearer $token", body)
    }

    fun getDeviceMapping(
        token: String
    ): Flow<Resources<DeviceMapping>> = handleApiCall {
        repository.getDeviceMapping("Bearer $token")
    }


    fun verifyTransactionPIN(
        token: String,
        body: UserRequestBodies.TransactionPINBody
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.verifyTransactionPIN("Bearer $token", body)
    }

    fun createTransactionPIN(
        token: String,
        body: UserRequestBodies.TransactionPINBody
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.createTransactionPIN("Bearer $token", body)
    }

    fun freezeTransaction(
        token: String
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.freezeTransaction("Bearer $token")
    }

    fun unFreezeTransaction(
        token: String
    ): Flow<Resources<CustomResponse>> = handleApiCall {
        repository.unFreezeTransaction("Bearer $token")
    }
}