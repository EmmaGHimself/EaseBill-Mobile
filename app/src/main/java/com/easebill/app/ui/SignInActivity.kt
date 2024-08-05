package com.easebill.app.ui

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.format.Formatter
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.easebill.app.R
import com.easebill.app.databinding.ActivitySignInBinding
import com.easebill.app.network.model.Auth
import com.easebill.app.network.model.UserRequestBodies
import com.easebill.app.repository.AppRepository
import com.easebill.app.sharedpref.SharedPreferencesHelper
import com.easebill.app.util.Status
import com.easebill.app.util.errorSnack
import com.easebill.app.viewmodel.ViewModelProviderFactory
import com.easebill.app.viewmodel.v1.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.launch
import java.net.NetworkInterface
import java.net.SocketException

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var authViewModel: AuthViewModel

    private val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val firebaseDefaultMap: HashMap<String, Any> = hashMapOf()
    private val VERSION_CODE_KEY = "latest_app_version"
    private val TAG = "SignInActivity"

    private lateinit var deviceId: String
    private lateinit var ipAddress: String
    private lateinit var deviceToken: String
    private lateinit var deviceOS: String

    private val REQUEST_PUSH_NOTIFICATIONS_PERMISSION = 12


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        init()

        // Check and request push notification permissions
        if (!checkPushNotificationPermission()) {
            requestPushNotificationPermission()
        }

        getDeviceDetails()

        with(binding) {
            emailET.addTextChangedListener(loginTextWatcher)
            pinET.addTextChangedListener(loginTextWatcher)

            forgotPassword.setOnClickListener {
                startActivity(Intent(applicationContext, ForgotPassActivity::class.java))
            }

            signUp.setOnClickListener {
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
            }


            val signUpText = "Don't have an Account?<font color= '#062FA1'><b> Sign Up</b></font>"
            signUp.text = Html.fromHtml(signUpText)
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

    private fun checkPushNotificationPermission(): Boolean {
        // Check if the necessary permissions for push notifications are granted
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val areNotificationsEnabled = notificationManager.areNotificationsEnabled()
        return areNotificationsEnabled
    }

    private fun requestPushNotificationPermission() {
        // Request permission for push notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "com.easebill.app",
                "EASEBILL",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val areNotificationsEnabled = notificationManager.areNotificationsEnabled()

        if (!areNotificationsEnabled) {
            // Notifications are disabled, prompt user to enable
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivityForResult(intent, REQUEST_PUSH_NOTIFICATIONS_PERMISSION)
        }
    }

    private val loginTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateContinueButtonState()
        }

        override fun afterTextChanged(s: Editable?) {}

        private fun updateContinueButtonState() {
            binding.apply {
                val email = emailET.text.toString().trim()
                val pin = pinET.text.toString().trim()

                val isEmailValid = email.isNotEmpty()
                val isPinValid = pin.isNotEmpty() && pin.length > 5

                continueBT.isEnabled = isEmailValid && isPinValid
                continueBT.alpha = if (continueBT.isEnabled) 1F else 0.5F

                pinET.setOnEditorActionListener { _, actionId, event ->
                    if (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                        continueBT.performClick()
                    }
                    false
                }

                continueBT.setOnClickListener {
                    hideKeyboard()
                    if (isEmailValid && isPinValid) {
                        handleSignIn(email, pin)
                    }
                }
            }
        }
    }

    private fun handleSignIn(email: String, pin: String) {
        val deviceDetails =
            UserRequestBodies.DeviceInfoBody(deviceToken, deviceOS, "ANDROID", deviceId)
        val body = UserRequestBodies.SignInBody(email, pin, "password", deviceDetails)

        authViewModel.viewModelScope.launch {
            authViewModel.userLogin(body).collect {
                when (it.status) {
                    Status.Loading -> showProgressBar()
                    Status.Success -> handleLoginSuccess(it.data)
                    Status.Error -> handleLoginError(it.message)
                }
            }
        }
    }

    private fun handleLoginSuccess(data: Auth?) {
        hideProgressBar()
        data?.let {
            binding.apply {
                emailET.setText("")
                pinET.setText("")
                SharedPreferencesHelper.getInstance(applicationContext)
                    ?.storeCurrentUser(it.data!!.user)
                SharedPreferencesHelper.getInstance(applicationContext)
                    ?.storeCurrentUserToken(it.data!!.token)
                SharedPreferencesHelper.getInstance(applicationContext)
                    ?.storeCurrentUserTokenExpire(it.data!!.expireAt)
                SharedPreferencesHelper.getInstance(applicationContext)
                    ?.storeCurrentUserSetPIN(it.data!!.user!!.hasSetTransactionPin)


                startMainActivity()

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        finishAffinity()
    }


    private fun init() {
        val repository = AppRepository(applicationContext)
        val factory = ViewModelProviderFactory(application, repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun startMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("just_logged_in", true)
        })
        finish()
    }

    private fun handleLoginError(errorMessage: String?) {
        hideProgressBar()
        errorMessage?.let {
            binding.continueBT.errorSnack(it, Snackbar.LENGTH_SHORT)
        }
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

    private fun setupFirebaseRemoteConfig() {
        firebaseDefaultMap[VERSION_CODE_KEY] = getCurrentVersionCode()
        mFirebaseRemoteConfig.setDefaultsAsync(firebaseDefaultMap)

        mFirebaseRemoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setFetchTimeoutInSeconds(60).build()
        )

        mFirebaseRemoteConfig.fetch().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mFirebaseRemoteConfig.fetchAndActivate()
                val fetchedVersionCode = mFirebaseRemoteConfig.getString(VERSION_CODE_KEY)
                Log.d(TAG, "Fetched value: $fetchedVersionCode")
                checkForUpdate(fetchedVersionCode.toInt())
            } else {
                showSnackbarError()
            }
        }

        val defaultValue = mFirebaseRemoteConfig.getString(VERSION_CODE_KEY)
        Log.d(TAG, "Default value: $defaultValue")
    }

    private fun showSnackbarError() {
        Snackbar.make(
            binding.root,
            "Something went wrong. Please try again.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun checkForUpdate(latestAppVersion: Int) {
        if (latestAppVersion > getCurrentVersionCode()) {
            showUpdateDialog()
        }
    }

    private fun showUpdateDialog() {
        AlertDialog.Builder(this)
            .setTitle("New Update")
            .setMessage("A new version is available.")
            .setPositiveButton("Update") { dialog, which ->
                redirectStore("https://mysme.ecobank.com/download")
            }
            .setCancelable(false)
            .show()
    }

    private fun redirectStore(updateUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun getCurrentVersionCode(): Int {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    private fun getNetworkIp(): String {
        try {
            NetworkInterface.getNetworkInterfaces().asSequence().forEach { networkInterface ->
                networkInterface.inetAddresses.asSequence().forEach { inetAddress ->
                    if (!inetAddress.isLoopbackAddress) {
                        ipAddress = Formatter.formatIpAddress(inetAddress.hashCode())
                        Log.i("_&deice_ip", "***** IP=$ipAddress")
                        return ipAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("_&deice_ip_error", ex.toString())
        }
        return ipAddress
    }

    private fun getDeviceDetails() {
        FirebaseInstallations.getInstance().id.addOnSuccessListener { installationId ->
            deviceId = installationId
            SharedPreferencesHelper.getInstance(applicationContext)
                ?.storeCurrentUserDeviceID(deviceId)
            deviceOS = getDeviceOS()
            getDeviceToken()
            getNetworkIp()
            Log.d("_&check", "device ID: $deviceId")
        }.addOnFailureListener { exception ->
            Log.d("_&check", "Error fetching device ID: $exception")
        }
    }

    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                deviceToken = task.result.toString()
                Log.d("_&check", "device Token: $deviceToken")
            }
        }
    }

    private fun getDeviceOS(): String {
        return "Android " + Build.VERSION.RELEASE
    }
}