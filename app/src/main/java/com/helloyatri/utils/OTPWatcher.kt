package com.helloyatri.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.helloyatri.R
import com.helloyatri.utils.extension.clear
import com.mukeshsolanki.OnOtpCompletionListener

class OTPWatcher(
    private val editTextOTP1: AppCompatEditText,
    private val editTextOTP2: AppCompatEditText,
    private val editTextOTP3: AppCompatEditText,
    private val editTextOTP4: AppCompatEditText
) : View.OnKeyListener {

    private var onOtpCompletionListener: OnOtpCompletionListener? = null

    override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyEvent.action == KeyEvent.ACTION_UP) {
            if (keyEvent.keyCode == KeyEvent.KEYCODE_DEL) {
                when (view.id) {
                    R.id.editTextOTP2 -> {
                        editTextOTP1.requestFocus()
                        editTextOTP1.setSelection(editTextOTP1.text!!.length)
                    }

                    R.id.editTextOTP3 -> {
                        editTextOTP2.requestFocus()
                        editTextOTP2.setSelection(editTextOTP2.text!!.length)
                    }

                    R.id.editTextOTP4 -> {
                        editTextOTP3.requestFocus()
                        editTextOTP3.setSelection(editTextOTP3.text!!.length)
                    }
                }
            }
        }
        return false
    }

    fun clearOtp() {
        editTextOTP2.clear()
        editTextOTP1.clear()
        editTextOTP4.clear()
        editTextOTP3.clear()
    }

    val otp: String
        get() = (editTextOTP1.text.toString().trim { it <= ' ' } +
                editTextOTP2.text.toString().trim { it <= ' ' } + editTextOTP3.text.toString()
            .trim { it <= ' ' }
                + editTextOTP4.text.toString().trim { it <= ' ' })

    private inner class OTPTextWatcher constructor(
        private val currentEditText: EditText,
        private val nextEditText: EditText
    ) : TextWatcher {

        private var isSelected = false

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            isSelected = currentEditText.isFocused && currentEditText.text.isNotEmpty()
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if ((editTextOTP1.text.toString().trim { it <= ' ' } +
                        editTextOTP2.text.toString()
                            .trim { it <= ' ' } + editTextOTP3.text.toString()
                    .trim { it <= ' ' }
                        + editTextOTP4.text.toString().trim { it <= ' ' }).length == 4) {
                onOtpCompletionListener?.onOtpCompleted(
                    (editTextOTP1.text.toString().trim { it <= ' ' } +
                            editTextOTP2.text.toString()
                                .trim { it <= ' ' } + editTextOTP3.text.toString()
                        .trim { it <= ' ' }
                            + editTextOTP4.text.toString().trim { it <= ' ' })
                )
            }
        }

        override fun afterTextChanged(editable: Editable) {
            if (editable.isNotEmpty()) {
                if (editable.length > 1) {
                    nextEditText.setText(editable.toString()[editable.toString().length - 1].toString())
                    currentEditText.setText(
                        editable.toString().substring(0, editable.toString().length - 1)
                    )
                    nextEditText.setSelection(nextEditText.text.length)
                }
                if (!isSelected) {
                    currentEditText.isSelected = true
                }
                nextEditText.requestFocus()
            } else {
                currentEditText.isSelected = false
            }
        }


        init {
            currentEditText.addTextChangedListener(this)
        }
    }

    init {

        editTextOTP1.addTextChangedListener(OTPTextWatcher(editTextOTP1, editTextOTP2))
        editTextOTP2.addTextChangedListener(OTPTextWatcher(editTextOTP2, editTextOTP3))
        editTextOTP3.addTextChangedListener(OTPTextWatcher(editTextOTP3, editTextOTP4))
        editTextOTP4.addTextChangedListener(OTPTextWatcher(editTextOTP4, editTextOTP4))

        editTextOTP1.setOnKeyListener(this)
        editTextOTP2.setOnKeyListener(this)
        editTextOTP3.setOnKeyListener(this)
        editTextOTP4.setOnKeyListener(this)
    }

    fun setOtpCompletionListener(otpCompletionListener: OnOtpCompletionListener?) {
        onOtpCompletionListener = otpCompletionListener
    }
}