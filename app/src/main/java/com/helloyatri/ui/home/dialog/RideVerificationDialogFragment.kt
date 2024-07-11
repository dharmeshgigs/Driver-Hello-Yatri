package com.helloyatri.ui.home.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.helloyatri.R
import com.helloyatri.databinding.RideVerificationDialogFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.ui.base.BaseDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment.Companion.FAILED
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment.Companion.SUCCESS
import com.helloyatri.utils.OTPWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class RideVerificationDialogFragment(private val callBack: (action: String) -> Unit) :
        BaseDialogFragment<RideVerificationDialogFragmentBinding>() {

    private val handler = Handler(Looper.getMainLooper())
    private val timer = Timer()
    private var seconds = 0

    private lateinit var otpWatcher: OTPWatcher

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): RideVerificationDialogFragmentBinding {
        return RideVerificationDialogFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData(): Unit = with(binding) {
        isCancelable = false
        otpWatcher = OTPWatcher(editTextOTP1, editTextOTP2, editTextOTP3, editTextOTP4)
        init()
        setClickListener()
        setUpTimer()
    }

    private fun init() = with(binding) {
        if (arguments?.getBoolean("isForRegenerateCode") == true) {
            textViewTitle.text = getString(R.string.label_enter_pooja_s_regenerate_code)
        }
    }

    private fun setUpTimer() = with(binding) {

        CoroutineScope(Dispatchers.Main).launch {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val minutes = seconds / 60
                    val remainingSeconds = seconds % 60
                    handler.post {
                        textViewTimer.text = String.format("%02d:%02d", minutes, remainingSeconds)
                    }
                    seconds++
                }
            }, 0, 1000)
        }


    }

    private fun setClickListener() = with(binding) {
        otpWatcher.setOtpCompletionListener {
            val inputManager = requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }

        textViewStartTheRide.setOnClickListener {
            validate()
        }
    }

    private fun validate() = try {
        when {
            otpWatcher.otp.isEmpty() -> {
                Toast.makeText(requireContext(), (getString(R.string.validation_please_enter_otp)),
                        Toast.LENGTH_SHORT).show()
            }

            otpWatcher.otp.length != 4 -> {
                Toast.makeText(requireContext(),
                        getString(R.string.validation_please_enter_valid_otp), Toast.LENGTH_SHORT)
                        .show()
            }

            otpWatcher.otp != "1234" -> {
                callBack.invoke(FAILED)
                otpWatcher.clearOtp()
                timer.cancel()
                seconds = 0
                dismissAllowingStateLoss()
            }

            else -> {
                callBack.invoke(SUCCESS)
                otpWatcher.clearOtp()
                timer.cancel()
                seconds = 0
                dismissAllowingStateLoss()
            }
        }
    } catch (e: ApplicationException) {
        //
    }
}