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
import com.easebill.app.databinding.ActivityOtpVerificationBinding
import com.easebill.app.network.model.CustomResponse
import com.easebill.app.network.model.UserRequestBodies
import com.easebill.app.repository.AppRepository
import com.easebill.app.util.Status
import com.easebill.app.util.errorSnack
import com.easebill.app.viewmodel.ViewModelProviderFactory
import com.easebill.app.viewmodel.v1.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerificationBinding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        init()
        email = intent.getStringExtra(INTENT_EMAIL).toString()

        binding.btClose.setOnClickListener { onBackPressed() }

        with(binding) {
            continueBT.isEnabled = false
            continueBT.alpha = 0.5F
            otpPin.addTextChangedListener(otpTextWatcher)

            timer.setOnClickListener {
                handleResendVerification()
            }
        }
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
                        handleVerification(email, pin)
                    }
                }
            }
        }
    }

    private fun handleVerification(email: String, pin: String) {
        val body =
            UserRequestBodies.ValidateForgotPassword(email, pin)

        authViewModel.viewModelScope.launch {
            authViewModel.validateForgotPassword(body).collect {
                when (it.status) {
                    Status.Loading -> showProgressBar()
                    Status.Success -> handleVerificationSuccess(it.data)
                    Status.Error -> handleVerificationError(it.message)
                }
            }
        }
    }

    private fun handleResendVerification() {
        val body = UserRequestBodies.ForgotPassword(email)
        authViewModel.viewModelScope.launch {
            authViewModel.forgotPassword(body).collect {
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
                                val body = UserRequestBodies.ForgotPassword(email)
                                authViewModel.viewModelScope.launch {
                                    authViewModel.forgotPassword(body).collect {
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

    private fun handleVerificationError(errorMessage: String?) {
        hideProgressBar()
        errorMessage?.let {
            binding.continueBT.errorSnack(it, Snackbar.LENGTH_SHORT)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(applicationContext, ResetPasswordActivity::class.java).apply {
            putExtra(ResetPasswordActivity.INTENT_EMAIL, email)
        }
        startActivity(intent)
    }

    private fun init() {
        val repository = AppRepository(applicationContext)
        val factory = ViewModelProviderFactory(application, repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
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

    private fun showProgressBar() {
        binding.continueBT.background = ContextCompat.getDrawable(this, R.drawable.primary_btn)
        binding.continueBT.startAnimation()
    }

    private fun hideProgressBar() {
        binding.continueBT.revertAnimation()
        binding.continueBT.background = ContextCompat.getDrawable(this, R.drawable.primary_btn)
    }

    companion object {
        const val INTENT_EMAIL = "email"
    }
}