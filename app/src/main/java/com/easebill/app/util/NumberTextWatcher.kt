package com.easebill.app.util

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.ParseException

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */

class NumberTextWatcher(private val editText: EditText) : TextWatcher {

    private val df: DecimalFormat = DecimalFormat("#,###")

    init {
        df.isGroupingUsed = true
    }

    override fun afterTextChanged(editable: Editable?) {
        editText.removeTextChangedListener(this)
        try {
            val originalString = editable.toString()

            // Remove any commas from the original string
            val longval: Long
            longval = try {
                df.parse(originalString).toLong()
            } catch (e: ParseException) {
                0
            }

            // Format the long value with commas
            val formattedString = df.format(longval)

            // Setting text after format to EditText
            editText.setText(formattedString)
            editText.setSelection(editText.text.length)
        } catch (nfe: NumberFormatException) {
            // Do nothing
        }
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not needed
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not needed
    }
}

fun EditText.addCommaFormatting() {
    val inputFilters = arrayOf<InputFilter>(InputFilter.LengthFilter(Int.MAX_VALUE))
    this.filters = inputFilters
    this.addTextChangedListener(NumberTextWatcher(this))
}