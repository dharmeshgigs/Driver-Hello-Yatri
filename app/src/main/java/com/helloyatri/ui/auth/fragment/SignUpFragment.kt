package com.helloyatri.ui.auth.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.response.LoginResponse
import com.helloyatri.databinding.AuthSignupFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.APIFactory
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<AuthSignupFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()

    private lateinit var countryCodePicker: CountryCodePicker
    private var countryCode: String? = null
    private var countryShortCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        apiViewModel.driverRegisterLiveData.get(this,{
//            hideLoader()
//
//            when(it.code) {
//                APIFactory.ResponseCode.SUCCESS -> {
//                    navigator.load(OTPVerificationFragment::class.java).setBundle(
//                        OTPVerificationFragment.createBundle(
//                            sourceScreen = SignUpFragment::class.java.simpleName
//                            , phonenumber = binding.includedMobileNumber.editText.text.toString().trim(),
//                            countrycode = binding.includedMobileNumber.textViewCountryCode.text.toString().trim(),
//                            name = binding.includedFullName.editText.text.toString().trim(),
//                            userId = binding.includedUserId.editText.text.toString().trim(),
//                            pwd = binding.includedPassword.editText.text.toString().trim())
//                        ,).replace(true)
//                }
//
//                else -> {
//                    showMessage(it.message)
//                }
//            }
//        //    navigator.load(DriverVerificationFragment::class.java).replace(false)
//        })

        initObservers()

    }

    private fun initObservers() {
        apiViewModel.driverRegisterLiveData.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        it.data?.let { it1 ->
                            val response =
                                Gson().fromJson(it1.toString(), LoginResponse::class.java)
                            response?.data?.let {
                                navigator.load(OTPVerificationFragment::class.java).setBundle(
                                    OTPVerificationFragment.createBundle(
                                        phonenumber = it.mobile,
                                        countrycode = it.mobile_txt
                                            ?: binding.includedMobileNumber.textViewCountryCode.text.toString()
                                                .trim(),
                                        sourceScreen = SignUpFragment::class.java.simpleName,
                                        name = it.name
                                            ?: binding.includedFullName.editText.text.toString()
                                                .trim(),
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

                    Status.LOADING -> showLoader()
                }
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthSignupFragmentBinding {
        return AuthSignupFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpEditText()
        setUpClickListener()
        setTextDecorator()
        setUpCountryCode()
        setUpButton()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_welcome)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_create_account)
        includedTopContent.textViewYouHaveMissed.text =
            getString(R.string.label_fill_your_information_below)
        textViewDontHaveAccount.text = getString(R.string.label_already_have_an_account_sign_in)
    }

    private fun setUpEditText() = with(binding) {
        includedFullName.textViewTitle.text = getString(R.string.hint_full_name)
        includedFullName.editText.hint = getString(R.string.hint_ex_rahul)
        includedUserId.textViewTitle.text = getString(R.string.title_user_id)
        includedUserId.editText.hint = getString(R.string.hint_ex_rahul_patel)
        includedMobileNumber.textViewTitle.text = getString(R.string.title_mobile_number)
        includedMobileNumber.editText.hint = getString(R.string.hiint_987654321)
        includedPassword.textViewTitle.text = getString(R.string.title_password)
        includedPassword.editText.hint = getString(R.string.hint_password)
        includedFullName.editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        includedUserId.editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        includedMobileNumber.editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        includedPassword.editText.imeOptions = EditorInfo.IME_ACTION_DONE

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
        countryCodePicker.setAutoDetectedCountry(true)
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
        if (includedFullName.editText.trimmedText.isEmpty()
                .not() && includedUserId.editText.trimmedText.isEmpty()
                .not() && includedMobileNumber.editText.trimmedText.isEmpty()
                .not() && includedPassword.editText.trimmedText.isEmpty().not()
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

    private fun setUpButton() = with(binding) {
        includedFullName.editText.doAfterTextChanged {
            enableVerifyButton()
        }
        includedUserId.editText.doAfterTextChanged {
            enableVerifyButton()
        }
        includedMobileNumber.editText.doAfterTextChanged {
            enableVerifyButton()
        }
        includedPassword.editText.doAfterTextChanged {
            enableVerifyButton()
        }
        radioButtonTermsCondition.setOnCheckedChangeListener { buttonView, isChecked ->
            enableVerifyButton()
        }

        includedMobileNumber.editText.doAfterTextChanged {
            if (it?.length == 10) {
                includedMobileNumber.imageViewVerify.isVisible(true)
            } else {
                includedMobileNumber.imageViewVerify.isVisible(false)
            }
        }
    }

    private fun setUpClickListener() = with(binding) {
        includedMobileNumber.viewCountryCode.setOnClickListener {
            hideKeyBoard()
            countryCodePicker.launchCountrySelectionDialog()
        }

        imageViewPassword.setOnClickListener {
            showHidePassword(imageViewPassword)
        }

        buttonNext.setOnClickListener {
            validate()
        }
    }

    private fun setTextDecorator() = with(binding) {
        TextDecorator.decorate(textViewDontHaveAccount, textViewDontHaveAccount.trimmedText)
            .makeTextClickable(false, getString(R.string.sign_in)) { _, _ ->
                navigator.goBack()
            }.setTextColor(R.color.colorPrimary, getString(R.string.sign_in))
            .setBackgroundColor(R.color.backgroundColor, getString(R.string.sign_in))
            .setTypeface(R.font.lufga_medium, getString(R.string.sign_in)).build()
    }

    private fun validate() = with(binding) {
        try {
            validator.submit(includedFullName.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_full_name))
                .checkMinDigits(Constants.MIN_NAME)
                .errorMessage(getString(R.string.validation_please_enter_valid_full_name))
                .check()

            validator.submit(includedUserId.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_user_id))
                .checkMinDigits(Constants.MIN_NAME)
                .errorMessage(getString(R.string.validation_please_enter_valid_user_id)).check()

            validator.submit(includedMobileNumber.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_mobile_number))
                .checkMinDigits(Constants.MIN_NUMBER)
                .errorMessage(getString(R.string.validation_please_enter_valid_mobile_number))
                .check()

            validator.submit(includedPassword.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_password))
                .checkMinDigits(Constants.MIN_PASSWORD)
                .errorMessage(getString(R.string.validation_please_enter_minimum_8_characters))
                .matchPatter(Constants.PASSWORD_REX).errorMessage(
                    getString(
                        R.string.validation_password_should_be_contained_1_uppercase_1_lowercase_1_digit_and_1_special_character
                    )
                )
                .check()

            if (!radioButtonTermsCondition.isChecked) {
                showMessage(getString(R.string.validation_please_agree_with_terms_and_condition))
                return@with
            }


            apiViewModel.driverRegister(
                Request(
                    name = binding.includedFullName.editText.text.toString().trim(),
                    userId = binding.includedUserId.editText.text.toString().trim(),
                    mobile = binding.includedMobileNumber.editText.text.toString().trim(),
                    password = binding.includedPassword.editText.text.toString().trim()
                )
            )
            /*
            navigator.load(OTPVerificationFragment::class.java).setBundle(
                    OTPVerificationFragment.createBundle(
                            sourceScreen = SignUpFragment::class.java.simpleName
                        , phonenumber = binding.includedMobileNumber.editText.text.toString().trim(),
                        countrycode = binding.includedMobileNumber.textViewCountryCode.text.toString().trim(),
                        name = binding.includedFullName.editText.text.toString().trim(),
                        userId = binding.includedUserId.editText.text.toString().trim(),
                        pwd = binding.includedPassword.editText.text.toString().trim())
            ,).replace(true)*/

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