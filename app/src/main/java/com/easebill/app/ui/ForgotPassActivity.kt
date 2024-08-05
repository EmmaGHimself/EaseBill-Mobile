package com.easebill.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.easebill.app.R
import com.easebill.app.databinding.ActivityForgotPasswordBinding
import com.easebill.app.databinding.PasswordForgotBottomsheetBinding
import com.easebill.app.network.model.UserRequestBodies
import com.easebill.app.repository.AppRepository
import com.easebill.app.util.Status
import com.easebill.app.util.errorSnack
import com.easebill.app.viewmodel.ViewModelProviderFactory
import com.easebill.app.viewmodel.v1.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        init()
        setupTextWatcher()

        binding.btClose.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun init() {
        val repository = AppRepository(applicationContext)
        val factory = ViewModelProviderFactory(application, repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun setupTextWatcher() {
        binding.emailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleEmailTextChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleEmailTextChanged() {
        val email = binding.emailET.text.toString().trim()
        binding.continueBT.isEnabled = email.isNotEmpty()
        binding.continueBT.alpha = if (email.isNotEmpty()) 1F else 0.5F

        binding.emailET.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.continueBT.performClick()
            }
            false
        }

        binding.continueBT.setOnClickListener {
            hideKeyboard()
            handleContinueButtonClick(email)
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun handleContinueButtonClick(email: String) {
        val body = UserRequestBodies.ForgotPassword(email)
        authViewModel.viewModelScope.launch {
            authViewModel.forgotPassword(body).collect { result ->
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
        hideKeyboard()

        val intent = Intent(applicationContext, OTPVerificationActivity::class.java).apply {
            putExtra(OTPVerificationActivity.INTENT_EMAIL, binding.emailET.text.toString().trim())
        }

        BottomSheetDialog(this@ForgotPassActivity).apply {
            val sheetBinding = PasswordForgotBottomsheetBinding.inflate(layoutInflater)
            setContentView(sheetBinding.root)

            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

            Glide.with(this@ForgotPassActivity).asGif().load(R.raw.ic_great_news)
                .into(sheetBinding.contentImg);

            sheetBinding.continueBT.setOnClickListener {
                dismiss()
                startActivity(intent)
                finish()
            }

            show()
        }
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
}