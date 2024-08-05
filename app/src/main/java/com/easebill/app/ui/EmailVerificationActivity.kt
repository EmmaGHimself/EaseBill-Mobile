package com.easebill.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.easebill.app.R
import com.easebill.app.databinding.ActivityEmailOtpVerificationBinding
import com.easebill.app.network.model.CustomResponse
import com.easebill.app.network.model.UserRequestBodies
import com.easebill.app.repository.AppRepository
import com.easebill.app.sharedpref.SharedPreferencesHelper
import com.easebill.app.util.Status
import com.easebill.app.util.errorSnack
import com.easebill.app.viewmodel.ViewModelProviderFactory
import com.easebill.app.viewmodel.v1.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailOtpVerificationBinding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var token: String
    private lateinit var emailAddress: String
    private lateinit var emailView: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        init()

        emailView = "We sent an OTP code to your email address ${hideEmail(emailAddress)}"

        sendEmailOTP()

        binding.desc.text =
            emailView

        with(binding) {
            continueBT.isEnabled = false
            continueBT.alpha = 0.5F
            otpPin.addTextChangedListener(otpTextWatcher)

            timer.setOnClickListener {
                handleResendVerification()
            }
        }
    }

    private fun sendEmailOTP() {
        authViewModel.viewModelScope.launch {
            authViewModel.resendVerification(token).collect {
                when (it.status) {
                    Status.Loading -> {

                    }

                    Status.Success -> {

                    }

                    Status.Error -> handleVerificationError(it.message)
                }
            }
        }
    }

    private fun hideEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 4) return email // Not enough characters to hide properly

        val visibleStart = email.substring(0, 2)
        val visibleEnd = email.substring(atIndex - 2, atIndex)
        val hiddenPart = "*".repeat(atIndex - 4)
        val domainPart = email.substring(atIndex)

        return "$visibleStart$hiddenPart$visibleEnd$domainPart"
    }

    private val otpTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateContinueButtonState()
        }

        override fun afterTextChanged(s: Editable?) {}

        private fun updateContinueButtonState() {
            binding.apply {
                val pin = otpPin.text.toString().trim()

                val isPinValid =
                    pin.isNotEmpty() && pin.length >= 4

                continueBT.isEnabled =
                    isPinValid
                continueBT.alpha = if (continueBT.isEnabled) 1F else 0.5F


                continueBT.setOnClickListener {
                    hideKeyboard()
                    if (isPinValid) {
                        handleVerification(pin)
                    }
                }
            }
        }
    }

    private fun handleVerification(pin: String) {
        val body =
            UserRequestBodies.VerifyEmailOTP(pin)

        authViewModel.viewModelScope.launch {
            authViewModel.verifyEmail(token, body).collect {
                when (it.status) {
                    Status.Loading -> showProgressBar()
                    Status.Success -> handleVerificationSuccess(it.data)
                    Status.Error -> handleVerificationError(it.message)
                }
            }
        }
    }

    private fun handleResendVerification() {
        authViewModel.viewModelScope.launch {
            authViewModel.resendVerification(token).collect {
                when (it.status) {
                    Status.Loading -> showProgressBar()
                    Status.Success -> handleSendingVerificationSuccess(it.data)
                    Status.Error -> handleVerificationError(it.message)
                }
            }
        }
    }

    private fun handleVerificationSuccess(data: CustomResponse?) {
        hideProgressBar()
        data?.let {
            binding.apply {
                otpPin.setText("")
                startMainActivity()
            }
        }
    }


    private fun handleSendingVerificationSuccess(data: CustomResponse?) {
        hideProgressBar()
        data?.let {
            binding.apply {
                otpPin.setText("")
                Snackbar.make(
                    binding.root,
                    "OTP sent successfully!",
                    Snackbar.LENGTH_SHORT
                ).show()

                val timer =
                    object :
                        CountDownTimer(60000, 1000) {
                        override fun onTick(
                            millisUntilFinished: Long
                        ) {
                            timer.text =
                                "Resend in " + millisUntilFinished / 1000 + " secs"
                            timer.isEnabled = false
                        }

                        override fun onFinish() {
                            timer.text =
                                "Resend OTP"
                            timer.isEnabled = true

                            timer.setOnClickListener {
                                authViewModel.viewModelScope.launch {
                                    authViewModel.resendVerification(token).collect {
                                        when (it.status) {
                                            Status.Loading -> showProgressBar()
                                            Status.Success -> handleSendingVerificationSuccess(it.data)
                                            Status.Error -> handleVerificationError(it.message)
                                        }
                                    }
                                }
                            }
                        }
                    }
                timer.start()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(applicationContext, AccountCreatedActivity::class.java))
        finish()
    }

    private fun handleVerificationError(errorMessage: String?) {
        hideProgressBar()
        errorMessage?.let {
            binding.continueBT.errorSnack(it, Snackbar.LENGTH_SHORT)
        }
    }

    private fun init() {
        val repository = AppRepository(applicationContext)
        val factory = ViewModelProviderFactory(application, repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        token = SharedPreferencesHelper.getInstance(applicationContext)!!
            .getStoredUserToken().toString()
        emailAddress = SharedPreferencesHelper.getInstance(applicationContext)!!
            .getStoredUser()!!.emailAddress
    }

    private fun showProgressBar() {
        binding.continueBT.background = ContextCompat.getDrawable(this, R.drawable.primary_btn)
        binding.continueBT.startAnimation()
    }

    private fun hideProgressBar() {
        binding.continueBT.revertAnimation()
        binding.continueBT.background = ContextCompat.getDrawable(this, R.drawable.primary_btn)
    }

    private fun hideKeyboard() {
        val view: View? = currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.continueBT.dispose()
    }
}