package com.helloyatri.ui.home.dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.RideVerificationDialogFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment.Companion.FAILED
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment.Companion.SUCCESS
import com.helloyatri.utils.AppUtils
import com.helloyatri.utils.OTPWatcher
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.visible
import com.helloyatri.utils.textdecorator.TextDecorator
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
    private val apiViewModel: ApiViewModel by activityViewModels()
    private var name: String = ""

    private lateinit var otpWatcher: OTPWatcher
    private var tripRiderModel: TripRiderModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripRiderModel = apiViewModel.tripRequest.value
        name = AppUtils.getFirstName(tripRiderModel?.riderDetails?.name ?: "")
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): RideVerificationDialogFragmentBinding {
        return RideVerificationDialogFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData(): Unit = with(binding) {
        isCancelable = false
        otpWatcher = OTPWatcher(editTextOTP1, editTextOTP2, editTextOTP3, editTextOTP4)
        init()
        initObservers()
        setClickListener()
        setUpTimer()
    }

    private fun initObservers() {
        apiViewModel.pickupNoteLiveData.observe(requireActivity()) {
            it?.let {
                var note = ""
                if (name.isNotEmpty()) {
                    note = name.plus(": ").plus(it)
                }
                TextDecorator.decorate(
                    binding.textViewNote, note
                ).setTextColor(R.color.colorPrimary, it).build()
            }
        }

        apiViewModel.verifyTripLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        apiViewModel.popUp?.let {
                            when (it.status) {
                                "success" -> {
                                    binding.textViewStartTheRide.visible()
                                    binding.progressBar.gone()
                                    callBack.invoke(SUCCESS)
                                    otpWatcher.clearOtp()
                                    dismissAllowingStateLoss()
                                }

                                else -> {
                                    callBack.invoke(FAILED)
                                    otpWatcher.clearOtp()
                                    timer.cancel()
                                    seconds = 0
                                    dismissAllowingStateLoss()
                                }
                            }
                        } ?: run {
                            callBack.invoke(FAILED)
                            otpWatcher.clearOtp()
                            timer.cancel()
                            seconds = 0
                            dismissAllowingStateLoss()
                        }
                        apiViewModel.verifyTripLiveData.value = null
                    }

                    Status.ERROR -> {
                        binding.textViewStartTheRide.visible()
                        binding.progressBar.gone()
                        callBack.invoke(FAILED)
                        otpWatcher.clearOtp()
                        timer.cancel()
                        seconds = 0
                        dismissAllowingStateLoss()
                        apiViewModel.verifyTripLiveData.value = null
                    }

                    Status.LOADING -> {
                        binding.textViewStartTheRide.gone()
                        binding.progressBar.visible()
                    }
                }
            }
        }
    }

    private fun init() = with(binding) {
        if (arguments?.getBoolean("isForRegenerateCode") == true) {
            textViewTitle.text = String.format(
                getString(R.string.label_dynamic_enter_pooja_s_regenerate_code), name
            )
        } else {
            textViewTitle.text = String.format(
                getString(R.string.label_dynamic_enter_pooja_s_verification_code), name
            )
        }
        apiViewModel.pickupNoteLiveData.value?.let {
            if (it.isNotEmpty()) {
                var note = ""
                if (name.isNotEmpty()) {
                    note = name.plus(": ").plus(it)
                }
                TextDecorator.decorate(
                    binding.textViewNote, note
                ).setTextColor(R.color.colorPrimary, it).build()
            }
        }
    }

    private fun setUpTimer() = with(binding) {
        CoroutineScope(Dispatchers.Main).launch {
            timer.schedule(object : TimerTask() {
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
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        textViewStartTheRide.setOnClickListener {
            validate()
        }
    }

    private fun validate() = try {
        when {
            otpWatcher.otp.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    (getString(R.string.validation_please_enter_otp)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            otpWatcher.otp.length != 4 -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.validation_please_enter_valid_otp),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                timer.cancel()
                seconds = 0
                apiViewModel.verifyTrip(
                    Request(
                        trip_id = tripRiderModel?.tripDetails?.id.toString() ?: "",
                        verification_code = otpWatcher.otp
                    )
                )
            }
        }
    } catch (e: ApplicationException) {
        //
    }
}