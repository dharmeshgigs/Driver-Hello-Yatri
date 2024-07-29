package com.helloyatri.ui.home.fragment

import android.net.Uri
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.helloyatri.network.Status
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.databinding.AccountEditProfileFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.auth.fragment.OTPVerificationFragment
import com.helloyatri.ui.auth.fragment.ResetPasswordFragment
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.common.fieldselection.bottomsheet.CommonFieldSelectionBottomSheet
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.fileselector.MediaSelectHelper
import com.helloyatri.utils.fileselector.MediaSelector
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountEditProfileFragment : BaseFragment<AccountEditProfileFragmentBinding>() {

    @Inject
    lateinit var mediaSelectHelper: MediaSelectHelper

    private lateinit var countryCodePicker: CountryCodePicker
    private var countryCode: String? = null
    private var countryShortCode: String? = null
    private var selectedImage: String? = null
    private var isCountryPickerEnable: Boolean = false
    private val apiViewModel by viewModels<ApiViewModel>()
    private var data: Driver? = null


    private val commonFieldSelectionBottomSheetForGender by lazy {
        CommonFieldSelectionBottomSheet()
    }

    private val commonFieldSelectionBottomSheetForCity by lazy {
        CommonFieldSelectionBottomSheet()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AccountEditProfileFragmentBinding {
        return AccountEditProfileFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpEditText()
        apiViewModel.getDriverProfile()
//        setUpData()
        initObservers()
        setUpClickListener()
        setImagePicker()
        setUpCountryCode()
    }

    private fun initObservers() {
        apiViewModel.getDriverProfileLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    val response =
                        Gson().fromJson(resource.data.toString(), DriverResponse::class.java)
                    data = response.data
                    setUserData(response.data)
                    Log.i("TAG", "initObservers: " + response.data.toString())
                }

                Status.ERROR -> {
                    hideLoader()
                }

                Status.LOADING -> {
                    hideLoader()
                }
            }
        }

        apiViewModel.updateProfileLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), DriverResponse::class.java)
                        response?.data?.let {
                            showMessage(response.message ?: "")
                            navigator.goBack()
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

    private fun setUserData(data: Driver?) = with(binding) {
        imageViewUserProfile.loadImageFromServerWithPlaceHolder(
            data?.profileImage ?: ""
        )
        includedFullName.editText.setText(data?.name ?: "")
        includedUserId.editText.setText(data?.userId ?: "")
        includedMobileNumber.textViewCountryCode.text = String.format("%s", "+91")
        includedMobileNumber.editText.setText(data?.mobile ?: "")
        includedPassword.editText.setText(String.format("%s", "rahul@123"))

        data?.gender?.let {
            binding.includedGender.editText.setText(
                data.gender
            )
        } ?: run {
            binding.includedGender.editText.setHint(getString(R.string.hint_select_gender))
        }
        data?.driveInCity?.let {
            binding.includedCityYouDriveIn.editText.setText(
                data?.driveInCity
            )
        } ?: run {
            binding.includedCityYouDriveIn.editText.setHint(
                getString(R.string.hint_select_city)
            )
        }
        commonFieldSelectionBottomSheetForGender.setOnOkButtonClickListener {
            includedGender.editText.setText(it.options)
        }
        commonFieldSelectionBottomSheetForCity.setOnOkButtonClickListener {
            includedCityYouDriveIn.editText.setText(it.options)
        }
    }

    private fun setUpCountryCode() = with(binding) {
        countryCodePicker = CountryCodePicker(context)
        countryCodePicker.setTypeFace(
            ResourcesCompat.getFont(requireContext(), R.font.lufga_regular)
        )
        countryCodePicker.setDialogBackground(R.drawable.bg_round_rect_white_radius_15)
        countryCodePicker.setOnCountryChangeListener {
            countryCode = countryCodePicker.selectedCountryCodeWithPlus
            countryShortCode = countryCodePicker.selectedCountryNameCode
            includedMobileNumber.textViewCountryCode.text = countryCode
        }
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
        includedGender.textViewTitle.text = getString(R.string.title_gender)
        includedGender.editText.hint = getString(R.string.hint_select_gender)
        includedCityYouDriveIn.textViewTitle.text = getString(R.string.title_city_you_drive_in)
        includedCityYouDriveIn.editText.hint = getString(R.string.hint_select_city)
        includedUserId.editText.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.hintColor)
        )
        includedMobileNumber.editText.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.hintColor)
        )
        includedMobileNumber.textViewCountryCode.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.hintColor)
        )
        includedPassword.editText.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.hintColor)
        )
        includedUserId.editText.isFocusableInTouchMode = false
        includedUserId.editText.isClickable = false
        includedMobileNumber.editText.isFocusableInTouchMode = false
        includedMobileNumber.editText.isClickable = false
        includedMobileNumber.textViewCountryCode.isFocusable = false
        includedMobileNumber.textViewCountryCode.isClickable = false
        includedPassword.editText.isFocusableInTouchMode = false
        includedPassword.editText.isClickable = false
        includedFullName.editText.imeOptions = EditorInfo.IME_ACTION_DONE
        includedUserId.editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        includedMobileNumber.editText.imeOptions = EditorInfo.IME_ACTION_DONE
        includedMobileNumber.editText.doAfterTextChanged {
            if (it?.length == 10) {
                includedMobileNumber.imageViewVerify.isVisible(true)
            } else {
                includedMobileNumber.imageViewVerify.isVisible(false)
            }
        }
    }

    private fun setUpData() = with(binding) {
        imageViewUserProfile.loadImageFromServerWithPlaceHolder(
            "https://plus.unsplash.com/premium_photo-1669047668540-9e1712e29f1f?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        )
        includedFullName.editText.setText(String.format("%s", "Rahul Patel"))
        includedUserId.editText.setText(String.format("%s", "rahul_patel"))
        includedMobileNumber.textViewCountryCode.text = String.format("%s", "+91")
        includedMobileNumber.editText.setText(String.format("%s", "9876541232"))
        includedPassword.editText.setText(String.format("%s", "rahul@123"))
        includedGender.editText.setText(String.format("%s", "Male"))
        includedCityYouDriveIn.editText.setText(String.format("%s", "Ahmedabad"))
        commonFieldSelectionBottomSheetForGender.setOnOkButtonClickListener {
            includedGender.editText.setText(it.options)
        }
        commonFieldSelectionBottomSheetForCity.setOnOkButtonClickListener {
            includedCityYouDriveIn.editText.setText(it.options)
        }
    }

    private fun setUpClickListener() = with(binding) {
        imageViewEdit.setOnClickListener {
            mediaSelectHelper.selectOptionsForImagePicker(true)
        }

        includedMobileNumber.viewCountryCode.setOnClickListener {
            if (isCountryPickerEnable) {
                hideKeyBoard()
                countryCodePicker.launchCountrySelectionDialog()
            }
        }

        buttonUpdate.setOnClickListener {
            validate()
        }

        textViewChangeNumber.setOnClickListener {
            isCountryPickerEnable = true
            includedMobileNumber.editText.isFocusableInTouchMode = true
            includedMobileNumber.editText.isClickable = true
            includedMobileNumber.textViewCountryCode.isFocusableInTouchMode = true
            includedMobileNumber.textViewCountryCode.isClickable = true
            includedMobileNumber.editText.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
            includedMobileNumber.textViewCountryCode.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
        }

        textViewChangePassword.setOnClickListener {
//            includedPassword.editText.isFocusableInTouchMode = true
//            includedPassword.editText.isClickable = true
//            includedPassword.editText.setTextColor(
//                    ContextCompat.getColor(requireContext(), R.color.black))
            navigator.load(ResetPasswordFragment::class.java).setBundle(
                OTPVerificationFragment.createBundle(
                    sourceScreen = AccountEditProfileFragment::class.java.simpleName,
                )
            ).replace(false)
        }

        imageViewPassword.setOnClickListener {
            showHidePassword(imageViewPassword)
        }

        includedGender.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForGender.setOptionsList(
                optionList = arrayListOf(
                    CommonFieldSelection(options = "Male"),
                    CommonFieldSelection(options = "Female"),
                    CommonFieldSelection(options = "Other")
                )
            ).setTitle("Select Gender")
                .setSelectedOption(includedGender.editText.trimmedText)
                .show(childFragmentManager, null)
        }

        includedCityYouDriveIn.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForCity.setOptionsList(
                optionList = arrayListOf(
                    CommonFieldSelection(options = "Ahmedabad"),
                    CommonFieldSelection(options = "Vadodara"),
                    CommonFieldSelection(options = "Mumbai"),
                    CommonFieldSelection(options = "Goa")
                )
            ).setTitle("Select City")
                .setSelectedOption(includedCityYouDriveIn.editText.trimmedText)
                .show(childFragmentManager, null)
        }
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
                .errorMessage(getString(R.string.validation_please_enter_password)).check()

            validator.submit(includedGender.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_select_gender)).check()

            validator.submit(includedCityYouDriveIn.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_select_city)).check()

//          navigator.goBack()

            showLoader()
            apiViewModel.updateProfile(
                Request(
                    name = (binding.includedFullName.editText.text ?: data?.name).toString(),
                    gender = (binding.includedGender.editText.text ?: data?.gender).toString(),
                    driverInCity = (binding.includedCityYouDriveIn.editText.text
                        ?: data?.driveInCity).toString()
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

    private fun setImagePicker() {
        mediaSelectHelper.canSelectMultipleImages(false)
        mediaSelectHelper.registerCallback(object : MediaSelector {
            override fun onImageUri(uri: Uri) {
                selectedImage = uri.path
                binding.imageViewUserProfile.loadImageFromServerWithPlaceHolder(selectedImage)
            }

            override fun onVideoUri(uri: Uri) {
                //
            }
        })
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarTitle(getString(R.string.title_profile))
            .build()
    }

}