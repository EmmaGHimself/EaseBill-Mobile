package com.easebill.app.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */
class SecurePhoneNumberTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isNumberHidden = false

    fun hideNumber() {
        if (!isNumberHidden) {
            text?.let {
                val secureText = StringBuilder()
                val lastThreeDigits = it.substring(maxOf(it.length - 3, 0))
                for (i in 0 until maxOf(it.length - 3, 0)) {
                    secureText.append("*")
                }
                secureText.append(lastThreeDigits)
                text = secureText.toString()
            }
            isNumberHidden = true
        }
    }

    fun showNumber(originalNumber: String) {
        if (isNumberHidden) {
            text = originalNumber // Replace originalNumber with your complete number variable
            isNumberHidden = false
        }
    }

}