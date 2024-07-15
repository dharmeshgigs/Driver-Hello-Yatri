package com.helloyatri.ui.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.LoginResponse
import com.helloyatri.databinding.AuthForgotPasswordFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.trimmedText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<AuthForgotPasswordFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()

    private lateinit var countryCodePicker: CountryCodePicker
    private var countryCode: String? = null
    private var countryShortCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        apiViewModel.sendOTPByMobileNumberLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource.data?.let { it ->
                        val response =
                            Gson().fromJson(it.toString(), LoginResponse::class.java)
                        navigator.load(OTPVerificationFragment::class.java).setBundle(
                            OTPVerificationFragment.createBundle(
                                phonenumber = binding.includedMobileNumber.editText.text.toString().trim(),
                                countrycode = "+91 "+binding.includedMobileNumber.editText.text.toString().trim(),
                                name = "Rahul",
                                sourceScreen = ForgotPasswordFragment::class.java.simpleName
                            )
                        ).replace(true)
//                        response?.data?.let {
//                            navigator.load(OTPVerificationFragment::class.java).setBundle(
//                                OTPVerificationFragment.createBundle(
//                                    phonenumber = it.mobile,
//                                    countrycode = it.mobile_txt,
//                                    name = it.name,
//                                    sourceScreen = ForgotPasswordFragment::class.java.simpleName
//                                )
//                            ).replace(true)
//                        } ?: run {
//                            showSomethingMessage()
//                        }
                    } ?: run {
                        showSomethingMessage()
                    }
                }

                Status.ERROR -> {
                    Log.i("TAG", "initObservers: "+resource.message)
                    hideLoader()
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> showLoader()
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthForgotPasswordFragmentBinding {
        return AuthForgotPasswordFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpEditText()
        setUpClickListener()
        setUpCountryCode()
        setUpButton()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_reset_password)
        includedTopContent.textViewWelcomeBack.text =
            getString(R.string.label_whats_your_mobile_number)
        includedTopContent.textViewYouHaveMissed.text =
            getString(R.string.label_fill_your_information_below)
    }

    private fun setUpEditText() = with(binding) {
        includedMobileNumber.textViewTitle.text = getString(R.string.title_mobile_number)
        includedMobileNumber.editText.hint = getString(R.string.hiint_987654321)
        includedMobileNumber.editText.imeOptions = EditorInfo.IME_ACTION_NEXT

        includedMobileNumber.editText.doAfterTextChanged {
            if (it?.length == 10) {
                includedMobileNumber.imageViewVerify.isVisible(true)
            } else {
                includedMobileNumber.imageViewVerify.isVisible(false)
            }
        }
    }

    private fun setUpCountryCode() = with(binding) {
        countryCodePicker = CountryCodePicker(context)
        countryCodePicker.setTypeFace(
            ResourcesCompat.getFont(requireContext(), R.font.lufga_regular)
        )
        countryCodePicker.setCountryForPhoneCode(91)
        countryCode = countryCodePicker.selectedCountryCodeWithPlus
        countryShortCode = countryCodePicker.selectedCountryNameCode
        includedMobileNumber.textViewCountryCode.text = countryCode
        countryCodePicker.setDialogBackground(R.drawable.bg_round_rect_white_radius_15)
        countryCodePicker.setOnCountryChangeListener {
            countryCode = countryCodePicker.selectedCountryCodeWithPlus
            countryShortCode = countryCodePicker.selectedCountryNameCode
            includedMobileNumber.textViewCountryCode.text = countryCode
        }
    }

    private fun enableVerifyButton() = with(binding) {
        if (includedMobileNumber.editText.trimmedText.length == 10
        ) {
            buttonNext.isClickable = true
            buttonNext.isEnabled = true
            buttonNext.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            includedMobileNumber.imageViewVerify.isVisible(true)
        } else {
            buttonNext.isClickable = false
            buttonNext.isEnabled = false
            buttonNext.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grey)
            includedMobileNumber.imageViewVerify.isVisible(false)
        }
    }

    private fun setUpButton() = with(binding) {
        includedMobileNumber.editText.doAfterTextChanged {
            enableVerifyButton()
        }
    }

    private fun setUpClickListener() = with(binding) {
        includedMobileNumber.viewCountryCode.setOnClickListener {
//            hideKeyBoard()
//            countryCodePicker.launchCountrySelectionDialog()
        }
        buttonNext.setOnClickListener {
            validate()
        }
    }

    private fun validate() = with(binding) {
        try {
            validator.submit(includedMobileNumber.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_mobile_number))
                .checkMinDigits(Constants.MIN_NUMBER)
                .errorMessage(getString(R.string.validation_please_enter_valid_mobile_number))
                .check()
            apiViewModel.sendOTPByMobileNumber(
                Request(
                    type = "driver",
                    mobile = binding.includedMobileNumber.editText.text.toString().trim(),
                )
            )

//            navigator.load(OTPVerificationFragment::class.java).setBundle(
//                OTPVerificationFragment.createBundle(
//                    phonenumber = binding.includedMobileNumber.editText.text.toString().trim(),
//                    countrycode = binding.includedMobileNumber.textViewCountryCode.text.toString().trim().plus(" ").plus(binding.includedMobileNumber.editText.text.toString().trim()),
//                    sourceScreen = ForgotPasswordFragment::class.java.simpleName,
//                    name = "Test",
//                )
//            ).replace(true)
        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}