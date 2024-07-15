package com.helloyatri.ui.auth.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.DriverStatusResponse
import com.helloyatri.databinding.AuthResetPasswordFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.activity.DriverDocumentsActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.changeStatusBarColor
import com.helloyatri.utils.extension.trimmedText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<AuthResetPasswordFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()
    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthResetPasswordFragmentBinding {
        return AuthResetPasswordFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(requireContext(), R.color.backgroundColor), true
        )
        initObservers()
    }

    private fun getDriverStatus() {
        apiViewModel.getDriverStatus()
    }

    private fun initObservers() {

        apiViewModel.resetPasswordLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    getDriverStatus()
                }

                Status.ERROR -> {
                    hideLoader()
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> showLoader()
            }
        }

        apiViewModel.getDriverStatus.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                        showLoader()
                    }

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

                                if (session.isAllDocumentUploaded()) {
                                    if (session.isDriverVerified && session.user?.status == 1) {
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
                        showErrorMessage(error)
                    }
                }
            }
        }

    }

    override fun bindData() {
        setUpText()
        setUpEditText()
        setUpButton()
        setTextDecorator()
        setUpClickListener()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_reset_password)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_create_new_password)
        includedTopContent.textViewYouHaveMissed.text =
            getString(R.string.label_complete_the_reset_password_process)
    }

    private fun setUpEditText() = with(binding) {
        includedNewPassword.textViewTitle.text = getString(R.string.new_password)
        includedConfirmPassword.textViewTitle.text = getString(R.string.confirm_password)
    }

    private fun setUpButton() = with(binding) {
        buttonNext.isClickable = false
        buttonNext.isEnabled = false
        includedNewPassword.editText.doAfterTextChanged {
            enableNextButton()
        }
        includedConfirmPassword.editText.doAfterTextChanged {
            enableNextButton()
        }
    }

    private fun enableNextButton() = with(binding) {
        if (includedNewPassword.editText.trimmedText.length == includedConfirmPassword.editText.trimmedText.length &&
            includedConfirmPassword.editText.trimmedText.length >= 8
        ) {
            buttonNext.isClickable = true
            buttonNext.isEnabled = true
            buttonNext.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        } else {
            buttonNext.isClickable = false
            buttonNext.isEnabled = false
            buttonNext.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grey)
        }
    }

    private fun setTextDecorator() = with(binding) {
//        TextDecorator.decorate(textViewDontHaveAccount, textViewDontHaveAccount.trimmedText)
//            .makeTextClickable(false, getString(R.string.label_sign_up)) { _, _ ->
//                navigator.load(SignUpFragment::class.java).replace(true)
//            }.setTextColor(R.color.colorPrimary, getString(R.string.label_sign_up))
//            .setBackgroundColor(R.color.backgroundColor, getString(R.string.label_sign_up))
//            .setTypeface(R.font.lufga_medium, getString(R.string.label_sign_up)).build()
    }

    private fun setUpClickListener() = with(binding) {
        imageViewNewPassword.setOnClickListener {
            showHidePassword(imageViewNewPassword)
        }
        imageViewConfirmPassword.setOnClickListener {
            showHideConfirmPassword(imageViewConfirmPassword)
        }
//
        buttonNext.setOnClickListener {
            validate()
        }
//
//        textViewForgotPassword.setOnClickListener {
//            navigator.load(ForgotPasswordFragment::class.java).replace(true)
//        }
    }

    private fun validate() = with(binding) {
        try {
            hideKeyBoard()

            validator.submit(includedNewPassword.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_password))
                .checkMinDigits(Constants.MIN_PASSWORD)
                .errorMessage(getString(R.string.validation_please_enter_minimum_8_characters))
                .matchPatter(Constants.PASSWORD_REX).errorMessage(
                    getString(
                        R.string.validation_password_should_be_contained_1_uppercase_1_lowercase_1_digit_and_1_special_character
                    )
                )
                .check()
            validator.submit(includedConfirmPassword.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_password))
                .checkMinDigits(Constants.MIN_PASSWORD)
                .errorMessage(getString(R.string.validation_please_enter_minimum_8_characters))
                .matchPatter(Constants.PASSWORD_REX).errorMessage(
                    getString(
                        R.string.validation_password_should_be_contained_1_uppercase_1_lowercase_1_digit_and_1_special_character
                    )
                )
                .check()
            if (includedNewPassword.editText.text.toString()
                    .trim() == includedConfirmPassword.editText.text.toString().trim()
            ) {
                apiViewModel.resetPassword(
                    Request(
                        password = includedNewPassword.editText.text.toString().trim()
                    )
                )
            } else {
                showMessage(getString(R.string.enter_password))
            }
//            apiViewModel.driverLogin(
//                Request(
//                    userId = includedUserId.editText.text.toString().trim(),
//                    password = includedPassword.editText.text.toString().trim(),
//                )
//            )
        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    private fun showHidePassword(view: View) {
        if (view.id == R.id.imageViewNewPassword) {
            if (binding.includedNewPassword.editText.transformationMethod.equals(
                    PasswordTransformationMethod.getInstance()
                )
            ) {
                binding.includedNewPassword.editText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.includedNewPassword.editText.setSelection(
                    binding.includedNewPassword.editText.text?.length ?: 0
                )
                (view as AppCompatImageView).setImageResource(R.drawable.ic_password_show)
            } else {
                (view as AppCompatImageView).setImageResource(R.drawable.ic_password_hide)
                binding.includedNewPassword.editText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.includedNewPassword.editText.setSelection(
                    binding.includedNewPassword.editText.text?.length ?: 0
                )

            }
        }
    }

    private fun showHideConfirmPassword(view: View) {
        if (view.id == R.id.imageViewConfirmPassword) {
            if (binding.includedConfirmPassword.editText.transformationMethod.equals(
                    PasswordTransformationMethod.getInstance()
                )
            ) {
                binding.includedConfirmPassword.editText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.includedConfirmPassword.editText.setSelection(
                    binding.includedConfirmPassword.editText.text?.length ?: 0
                )
                (view as AppCompatImageView).setImageResource(R.drawable.ic_password_show)
            } else {
                (view as AppCompatImageView).setImageResource(R.drawable.ic_password_hide)
                binding.includedConfirmPassword.editText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.includedConfirmPassword.editText.setSelection(
                    binding.includedConfirmPassword.editText.text?.length ?: 0
                )

            }
        }
    }


    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}