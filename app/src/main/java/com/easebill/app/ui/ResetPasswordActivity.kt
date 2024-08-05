package com.easebill.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.easebill.app.R
import com.easebill.app.databinding.ActivityResetPasswordBinding
import com.easebill.app.network.model.UserRequestBodies
import com.easebill.app.repository.AppRepository
import com.easebill.app.util.Status
import com.easebill.app.util.errorSnack
import com.easebill.app.viewmodel.ViewModelProviderFactory
import com.easebill.app.viewmodel.v1.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        init()
        email = intent.getStringExtra(INTENT_EMAIL).toString()

        binding.newPinET.addTextChangedListener(pinTextWatcher)
        binding.confirmPinET.addTextChangedListener(pinTextWatcher)

        binding.btClose.setOnClickListener { onBackPressed() }

        binding.toggleNewPassword.setOnCheckedChangeListener { _, isChecked ->
            val selectionStart = binding.newPinET.selectionStart
            val selectionEnd = binding.newPinET.selectionEnd

            binding.newPinET.transformationMethod =
                if (isChecked) null
                else android.text.method.PasswordTransformationMethod.getInstance()

            // Restore cursor position
            binding.newPinET.setSelection(selectionStart, selectionEnd)
        }

        binding.toggleConfirmPassword.setOnCheckedChangeListener { _, isChecked ->
            val selectionStart = binding.confirmPinET.selectionStart
            val selectionEnd = binding.confirmPinET.selectionEnd

            binding.confirmPinET.transformationMethod =
                if (isChecked) null
                else android.text.method.PasswordTransformationMethod.getInstance()

            // Restore cursor position
            binding.confirmPinET.setSelection(selectionStart, selectionEnd)
        }

    }

    private fun init() {
        val repository = AppRepository(applicationContext)
        val factory = ViewModelProviderFactory(application, repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }


    private val pinTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            handlePINResetTextChanged()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun handlePINResetTextChanged() {
        val newPassword = binding.newPinET.text.toString().trim()
        val confirmPassword = binding.confirmPinET.text.toString().trim()

        val enableButton =
            newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword == confirmPassword

        binding.continueBT.isEnabled = enableButton
        binding.continueBT.alpha = if (enableButton) 1F else 0.5F


        binding.continueBT.setOnClickListener {
            hideKeyboard()
            handleContinueButtonClick(email, confirmPassword)
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun handleContinueButtonClick(email: String, password: String) {
        val body = UserRequestBodies.ResetPasswordBody(email, password)
        authViewModel.viewModelScope.launch {
            authViewModel.resetPassword(body).collect { result ->
                when (result.status) {
                    Status.Loading -> showProgressBar()
                    Status.Success -> showSuccessDialog()
                    Status.Error -> showErrorSnackBar(result.message!!)
                }
            }
        }
    }

    private fun showSuccessDialog() {
        hideProgressBar()
        startActivity(Intent(applicationContext, PasswordResetSuccessActivity::class.java))
        finish()
    }


    private fun showErrorSnackBar(message: String) {
        binding.continueBT.errorSnack(message, Snackbar.LENGTH_SHORT)
        hideProgressBar()
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