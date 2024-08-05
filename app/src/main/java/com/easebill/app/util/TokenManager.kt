package com.easebill.app.util

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.easebill.app.repository.AppRepository
import com.easebill.app.sharedpref.SharedPreferencesHelper
import com.easebill.app.viewmodel.ViewModelProviderFactory
import com.easebill.app.viewmodel.v1.AuthViewModel
import kotlinx.coroutines.launch

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */
class TokenManager(private val viewModelStoreOwner: ViewModelStoreOwner) {
    var tokenExpirationTime: Long = 0

    private var authViewModel: AuthViewModel

    init {
        val context = viewModelStoreOwner as Context
        val application = context.applicationContext as Application
        val repository = AppRepository(context)
        val factory = ViewModelProviderFactory(application, repository)
        authViewModel =
            ViewModelProvider(viewModelStoreOwner, factory)[AuthViewModel::class.java]
    }

    fun refreshToken() {
        val context = viewModelStoreOwner as Context
        val token = SharedPreferencesHelper.getInstance(context)!!.getStoredUserToken().toString()

        authViewModel.viewModelScope.launch {
            authViewModel.refreshToken(token).collect { result ->
                when (result.status) {
                    Status.Success -> {
                        SharedPreferencesHelper.getInstance(context)!!
                            .storeCurrentUserToken(result.data!!.token!!.token)

                        SharedPreferencesHelper.getInstance(context)!!
                            .storeCurrentUserTokenExpire(result.data.token!!.expireAt)
                    }
                    // Handle loading and error states if needed
                    else -> {}
                }
            }
        }
    }
}
