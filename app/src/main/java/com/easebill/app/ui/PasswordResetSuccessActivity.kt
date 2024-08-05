package com.easebill.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.easebill.app.R
import com.easebill.app.databinding.ActivityPasswordSuccessBinding

class PasswordResetSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        Glide.with(this).asGif().load(R.raw.ic_great_news).into(binding.contentImg);


        binding.continueBT.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    SignInActivity::class.java
                )
            )
            finish()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(
                applicationContext,
                SignInActivity::class.java
            )
        )
        finish()
    }
}