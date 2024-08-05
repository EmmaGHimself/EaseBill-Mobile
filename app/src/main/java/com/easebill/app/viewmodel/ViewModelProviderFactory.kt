package com.easebill.app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.easebill.app.repository.AppRepository
import com.easebill.app.viewmodel.v1.AuthViewModel
import com.easebill.app.viewmodel.v1.UserViewModel

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

class ViewModelProviderFactory(
    private val app: Application,
    private val appRepository: AppRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(app, appRepository) as T
        }

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(app, appRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}