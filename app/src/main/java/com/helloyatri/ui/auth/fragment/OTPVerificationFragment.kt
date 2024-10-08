package com.helloyatri.ui.auth.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.response.Driver
import com.helloyatri.data.response.DriverStatus
import com.helloyatri.data.response.DriverStatusResponse
import com.helloyatri.data.response.Login
import com.helloyatri.data.response.LoginResponse
import com.helloyatri.data.response.OTPVerificationResponse
import com.helloyatri.databinding.AuthVerificationFragmentBinding
import com.helloyatri.network.APIFactory
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.ResBody
import com.helloyatri.ui.activity.DriverDocumentsActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.utils.extension.clear
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.genericType
import com.helloyatri.utils.getJsonString
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OTPVerificationFragment : BaseFragment<AuthVerificationFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()
    private var countDownTimer: CountDownTimer? = null
    private var start = false
    private var getOTP: String? = null

    companion object {
        const val SOURCE_SCREEN = "sourceScreen"

        fun createBundle(
            sourceScreen: String? = null,
            phonenumber: String? = null,
            countrycode: String? = null,
            name: String? = null,
            userId: String? = null,
            pwd: String? = null
        ) = bundleOf(
            SOURCE_SCREEN to sourceScreen,
            "phone" to phonenumber,
            "countrycode" to countrycode,
            "name" to name,
            "userid" to userId,
            "pwd" to pwd
        )
    }

    private val getSourceScreen by lazy {
        arguments?.getString(SOURCE_SCREEN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiViewModel.getDriverStatus.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        hideLoader()
                        it.data?.let { it ->
                            val response =
                                Gson().fromJson(it, DriverStatusResponse::class.java)
                            response?.data?.let { driverStatus ->
                                driverStatus.addVehicle?.let {
                                    session.isAddVehicle = it.status ?: false
                                }
                                driverStatus.profileImage?.let {
                                    session.isProfilePictureAdded = it.status ?: false
                                }
                                driverStatus.profileInfo?.let {
                                    session.isPersonalDetailsAdded = it.status ?: false
                                }
                                driverStatus.requiredDocuments?.let {
                                    session.isRequiredDocumentsAdded = it.status ?: false
                                }
                                driverStatus.vehicleDocuments?.let {
                                    session.isVehicleDocumentsAdded = it.status ?: false
                                }
                                driverStatus.vehicleImages?.let {
                                    session.isVehiclePhotosAdded = it.status ?: false
                                }
                                driverStatus.verificationPending?.let {
                                    session.isDriverVerified = it.status ?: false
                                }
                                if(session.isAllDocumentUploaded()) {
                                    if(session.isDriverVerified) {
                                        navigator.loadActivity(HomeActivity::class.java)
                                            .byFinishingAll()
                                            .start()
                                    } else {
                                        navigator.load(DriverVerificationFragment::class.java)
                                                .replace(false)
                                    }
                                } else {
                                    navigator.loadActivity(DriverDocumentsActivity::class.java)
                                        .byFinishingAll()
                                        .start()
                                }
                            }?: run {
                                showSomethingMessage()
                            }
                        } ?: run {
                            showSomethingMessage()
                        }
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }
                }
            }
        }

        apiViewModel.verifyOtpLiveData.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                        it.data?.let {
                            val response = Gson().fromJson(it, OTPVerificationResponse::class.java)
                            response?.data?.token?.let { token ->
                                session.user = response.data
                                session.userSession = token
                                getDriverStatus()
                            } ?: run {
                                showSomethingMessage()
                            }
                        } ?: run {
                            showSomethingMessage()
                        }
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        binding.otpView.clear()
                        showErrorMessage(error)
                    }
                }
            }
        }

        apiViewModel.resendOtpLiveData.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                        it.data?.let {
                            val response = Gson().fromJson(it, LoginResponse::class.java)
                            response?.let { it1 ->
                                showMessage(it1.message)
                            } ?: run {
                                showSomethingMessage()
                            }
                        } ?: run {
                            showSomethingMessage()
                        }
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error = resource.message ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }
                }
            }
//            when (it.code) {
//                APIFactory.ResponseCode.SUCCESS -> {
//                    this@OTPVerificationFragment.getOTP = it.data?.otp
//                    countDownTimer()
//                }
//
//                else -> {
//                    showErrorMessage(it.message)
//                }
//            }
        }

        apiViewModel.driverRegisterLiveData.get(this, {
            hideLoader()

            navigator.load(DriverVerificationFragment::class.java).replace(false)
        })
    }

    private fun getDriverStatus() {
        apiViewModel.getDriverStatus()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): AuthVerificationFragmentBinding {
        return AuthVerificationFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()

        setUpClickListener()
        setUpTextDecorator()
        setUpOTPView()
        setUpButtonVerify()
        countDownTimer()
        //  getOtp()
    }

    private fun setUpButtonVerify() = with(binding) {
        buttonVerify.isClickable = false
        buttonVerify.isEnabled = false
        buttonVerify.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.grey)
    }

    private fun setUpOTPView() = with(binding) {
        otpView.setOtpCompletionListener {
            if (otpView.trimmedText.isNotEmpty()) {
                buttonVerify.isClickable = true
                buttonVerify.isEnabled = true
                buttonVerify.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            }
        }
    }

    private fun getOtp() {
        apiViewModel.resendOtp(Request(type = "driver", arguments?.getString("phone")))
    }

    @SuppressLint("SetTextI18n")
    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_hello_s, "Rahul")
        includedTopContent.textViewWelcomeBack.text =
            getString(R.string.label_enter_6_digit_verification_code)
        includedTopContent.textViewYouHaveMissed.text =
            getString(R.string.label_we_have_sent_you_a_six_digit_code_on_your_s)
        includedTopContent.textViewPhoneNumber.text =
            arguments?.getString("countrycode").plus(" ") + arguments?.getString("phone")
    }

    private fun setUpClickListener() = with(binding) {
        textViewResendCodeIn.setOnClickListener {
            if (textViewResendCodeIn.text == getString(R.string.label_resend)) {
                getOtp()
                // start = true
                // countDownTimer()
            }
        }

        buttonVerify.setOnClickListener {
            if (otpView.trimmedText.isEmpty()) {
                showErrorMessage(getString(R.string.validation_please_enter_otp))
            } else if (otpView.trimmedText.length != 6) {
                showErrorMessage(getString(R.string.validation_please_enter_otp))
            } /*else if (otpView.text.toString() != getOTP) {
                showMessage(getString(R.string.validation_please_enter_valid_otp))
            }*/ else {
                hideKeyBoard()
                apiViewModel.verifyOtp(
                    Request(
                        type = "driver",
                        mobile = arguments?.getString("phone"),
                        otp = otpView.text.toString()
                    )
                )

            }
        }

        otpView.setOtpCompletionListener {
            hideKeyBoard()
        }
    }

    private fun countDownTimer() = with(binding) {
        countDownTimer = object : CountDownTimer(60000 - 1, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textViewResendCodeIn.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.hintColor)
                )
                textViewResendCodeIn.text = String.format(
                    Locale.ENGLISH,
                    "${getString(R.string.label_resend_code_in)} %02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(
                        millisUntilFinished
                    ) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
            }

            override fun onFinish() {
                textViewResendCodeIn.text = getString(R.string.label_resend)
                textViewResendCodeIn.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                )
                start = false
            }
        }.start()
    }

    private fun setUpTextDecorator() = with(binding) {
        TextDecorator.decorate(
            includedTopContent.textViewYouHaveMissed,
            includedTopContent.textViewYouHaveMissed.trimmedText
        ).setTextColor(R.color.colorPrimary, "+91 98765 43210")
            .setTypeface(R.font.lufga_regular, "+91 98765 43210").build()
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        super.onDestroyView()
    }
}