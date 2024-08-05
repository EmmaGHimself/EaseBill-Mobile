package com.easebill.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.easebill.app.databinding.ActivitySignUpBinding
import com.easebill.app.network.model.Auth
import com.easebill.app.network.model.UserRequestBodies
import com.easebill.app.repository.AppRepository
import com.easebill.app.sharedpref.SharedPreferencesHelper
import com.easebill.app.util.Status
import com.easebill.app.util.errorSnack
import com.easebill.app.viewmodel.ViewModelProviderFactory
import com.easebill.app.viewmodel.v1.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        init()

        binding.apply {
            emailET.addTextChangedListener(signUpTextWatcher)
            pinET.addTextChangedListener(signUpTextWatcher)
            firstNameET.addTextChangedListener(signUpTextWatcher)
            lastNameET.addTextChangedListener(signUpTextWatcher)
            userNameET.addTextChangedListener(signUpTextWatcher)
            phoneET.addTextChangedListener(signUpTextWatcher)
        }

        with(binding) {

            btClose.setOnClickListener {
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }
        }

        binding.togglePassword.setOnCheckedChangeListener { _, isChecked ->
            val selectionStart = binding.pinET.selectionStart
            val selectionEnd = binding.pinET.selectionEnd

            // Set the transformation method based on the toggle state
            binding.pinET.transformationMethod =
                if (isChecked) null
                else android.text.method.PasswordTransformationMethod.getInstance()

            // Restore cursor position
            binding.pinET.setSelection(selectionStart, selectionEnd)
        }
    }

    private val signUpTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateContinueButtonState()
        }

        override fun afterTextChanged(s: Editable?) {}

        private fun updateContinueButtonState() {
            binding.apply {
                val email = emailET.text.toString().trim()
                val pin = pinET.text.toString().trim()
                val firstName = firstNameET.text.toString().trim()
                val lastName = lastNameET.text.toString().trim()
                val phoneNumber = phoneET.text.toString().trim()
                val userName = userNameET.text.toString().trim()

                val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

                val isEmailValid = email.trim().matches(emailPattern)
                if (!isEmailValid) {
                    emailET.error = "Invalid email address"
                } else {
                    emailET.error = null
                }

                val isUserNameValid = userName.trim().length >= 3
                if (!isUserNameValid) {
                    userNameET.error = "Username must be at least 3 characters"
                } else {
                    userNameET.error = null
                }

                val isFirstNameValid = firstName.trim().length >= 3
                if (!isFirstNameValid) {
                    firstNameET.error = "Firstname must be at least 3 characters"
                } else {
                    firstNameET.error = null
                }

                val isLastNameValid = lastName.trim().length >= 3
                if (!isLastNameValid) {
                    lastNameET.error = "Lastname must be at least 3 characters"
                } else {
                    lastNameET.error = null
                }

                val isPinValid = pin.length >= 8
                if (!isPinValid) {
                    pinET.error = "Password must be at least 8 digits"
                } else {
                    pinET.error = null
                }

                continueBT.isEnabled = emailET.error == null && pinET.error == null &&
                        firstNameET.error == null && lastNameET.error == null && userNameET.error == null

                continueBT.alpha = if (continueBT.isEnabled) 1F else 0.5F

                continueBT.setOnClickListener {
                    hideKeyboard()
                    if (emailET.error == null && pinET.error == null && firstNameET.error == null &&
                        lastNameET.error == null && userNameET.error == null
                    ) {
                        handleSignUp(
                            firstName,
                            lastName,
                            userName,
                            email,
                            pin,
                            phoneNumber
                        )
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Please fill all fields correctly",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }


    private fun init() {
        val repository = AppRepository(applicationContext)
        val factory = ViewModelProviderFactory(application, repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
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


    private fun handleSignUp(
        firstName: String,
        lastName: String,
        userName: String,
        email: String,
        pin: String,
        phoneNumber: String
    ) {
        val body =
            UserRequestBodies.SignUpBody(firstName, lastName, userName, phoneNumber, email, pin)

        authViewModel.viewModelScope.launch {
            authViewModel.userRegistration(body).collect {
                when (it.status) {
                    Status.Loading -> showProgressBar()
                    Status.Success -> handleSignUpSuccess(it.data)
                    Status.Error -> handleSignUpError(it.message)
                }
            }
        }
    }


    private fun handleSignUpSuccess(data: Auth?) {
        hideProgressBar()
        data?.let {
            binding.apply {
                SharedPreferencesHelper.getInstance(applicationContext)
                    ?.storeCurrentUser(it.data!!.user)
                SharedPreferencesHelper.getInstance(applicationContext)
                    ?.storeCurrentUserToken(it.data!!.token)
                SharedPreferencesHelper.getInstance(applicationContext)
                    ?.storeCurrentUserTokenExpire(it.data!!.expireAt)

                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(applicationContext, EmailVerificationActivity::class.java))
    }

    private fun handleSignUpError(errorMessage: String?) {
        hideProgressBar()
        errorMessage?.let {
            binding.continueBT.errorSnack(it, Snackbar.LENGTH_SHORT)
        }
    }

}