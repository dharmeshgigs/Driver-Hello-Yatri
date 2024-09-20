package com.helloyatri.ui.auth.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.LoginResponse
import com.helloyatri.databinding.AuthLoginFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.changeStatusBarColor
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<AuthLoginFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)


    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthLoginFragmentBinding {
        return AuthLoginFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(requireContext(), R.color.backgroundColor), true
        )
        initObservers()

    }

    private fun initObservers() {
        apiViewModel.driverLoginLiveData.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                        it.data?.let {
                            val response =
                                Gson().fromJson(it.toString(), LoginResponse::class.java)
                            response?.data?.let { it ->
                                navigator.load(OTPVerificationFragment::class.java).setBundle(
                                    OTPVerificationFragment.createBundle(
                                        phonenumber = it.mobile,
                                        countrycode = it.mobile_txt,
                                        name = it.name,
                                        sourceScreen = LoginFragment::class.java.simpleName
                                    )
                                ).replace(true)
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
        session.user = null
        session.userSession = ""
        setUpText()
        setUpEditText()
        setUpButton()
        setTextDecorator()
        setUpClickListener()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_hello)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_welcome_back)
        includedTopContent.textViewYouHaveMissed.text = getString(R.string.label_you_ve_been_missed)
    }

    private fun setUpEditText() = with(binding) {
        includedUserId.textViewTitle.text = getString(R.string.title_user_id)
        includedUserId.editText.hint = getString(R.string.hint_ex_rahul)
        includedPassword.textViewTitle.text = getString(R.string.title_password)
        includedPassword.editText.hint = getString(R.string.hint_password)
        includedUserId.editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        includedPassword.editText.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    private fun setUpButton() = with(binding) {
        buttonNext.isClickable = false
        buttonNext.isEnabled = false
        includedPassword.editText.doAfterTextChanged {
            enableNextButton()
        }
        includedUserId.editText.doAfterTextChanged {
            enableNextButton()
        }
    }

    private fun enableNextButton() = with(binding) {
        if (includedPassword.editText.trimmedText.length >= 8) {
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
        TextDecorator.decorate(textViewDontHaveAccount, textViewDontHaveAccount.trimmedText)
            .makeTextClickable(false, getString(R.string.label_sign_up)) { _, _ ->
                navigator.load(SignUpFragment::class.java).replace(true)
            }.setTextColor(R.color.colorPrimary, getString(R.string.label_sign_up))
            .setBackgroundColor(R.color.backgroundColor, getString(R.string.label_sign_up))
            .setTypeface(R.font.lufga_medium, getString(R.string.label_sign_up)).build()
    }

    private fun setUpClickListener() = with(binding) {
        imageViewPassword.setOnClickListener {
            showHidePassword(imageViewPassword)
        }

        buttonNext.setOnClickListener {
            validate()
        }

        textViewForgotPassword.setOnClickListener {
            navigator.load(ForgotPasswordFragment::class.java).replace(true)
        }
    }

    private fun validate() = with(binding) {
        try {
            validator.submit(includedUserId.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_user_id))
                .checkMinDigits(Constants.MIN_NAME)
                .errorMessage(getString(R.string.validation_please_enter_valid_user_id)).check()

            validator.submit(includedPassword.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_password)).check()

            hideKeyBoard()
            apiViewModel.driverLogin(
                Request(
                    userId = includedUserId.editText.text.toString().trim(),
                    password = includedPassword.editText.text.toString().trim(),
                )
            )
        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    private fun showHidePassword(view: View) {
        if (view.id == R.id.imageViewPassword) {
            if (binding.includedPassword.editText.transformationMethod.equals(
                    PasswordTransformationMethod.getInstance()
                )
            ) {
                binding.includedPassword.editText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.includedPassword.editText.setSelection(
                    binding.includedPassword.editText.text?.length ?: 0
                )
                (view as AppCompatImageView).setImageResource(R.drawable.ic_password_show)
            } else {
                (view as AppCompatImageView).setImageResource(R.drawable.ic_password_hide)
                binding.includedPassword.editText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.includedPassword.editText.setSelection(
                    binding.includedPassword.editText.text?.length ?: 0
                )

            }
        }
    }


    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}