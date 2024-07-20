package com.helloyatri.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.gamingyards.sms.app.utils.Resource
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.CityModel
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.databinding.AuthDriverPersonalDetailsFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.common.fieldselection.bottomsheet.CommonFieldSelectionBottomSheet
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.trimmedText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverPersonalDetailsFragment : BaseFragment<AuthDriverPersonalDetailsFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()
    private lateinit var countryCodePicker: CountryCodePicker
    private var countryCode: String? = null
    private var countryShortCode: String? = null
    val getCities by lazy { MutableLiveData<Resource<JsonObject>>() }
    val getAllCities by lazy { ArrayList<CommonFieldSelection>() }

    private val commonFieldSelectionBottomSheetForGender by lazy {
        CommonFieldSelectionBottomSheet()
    }

    private val commonFieldSelectionBottomSheetForCity by lazy {
        CommonFieldSelectionBottomSheet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    private fun initObservers() {

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

        apiViewModel.getCitiesLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let { it ->
                        val response =
                            Gson().fromJson(it.toString(), CityModel::class.java)
                        response?.data?.let {
                            getAllCities.clear()
                            getAllCities.addAll(it)
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

        apiViewModel.getDriverProfileLiveData.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                        val response =
                            Gson().fromJson(it.data.toString(), DriverResponse::class.java)
                        binding.includedFullName.editText.setText(
                            String.format(
                                "%s",
                                response.data?.name
                            )
                        )
                        binding.includedUserId.editText.setText(
                            String.format(
                                "%s",
                                response.data?.userId
                            )
                        )
                        binding.includedMobileNumber.editText.setText(
                            String.format(
                                "%s",
                                response.data?.mobile
                            )
                        )
                        binding.includedGender.editText.setText(
                            String.format(
                                "%s",
                                response.data?.gender ?: getString(R.string.hint_select_gender)
                            )
                        )
                        response.data?.driveInCity?.let {
                            binding.includedCityYouDriveIn.editText.setText(
                                response.data?.driveInCity
                            )
                        } ?: run {
                            binding.includedCityYouDriveIn.editText.setHint(
                                getString(R.string.hint_select_city)
                            )
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

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthDriverPersonalDetailsFragmentBinding {
        return AuthDriverPersonalDetailsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpEditText()
        setUpClickListener()
        setUpCountryCode()
        setUpData()

        showLoader()
        apiViewModel.getDriverProfile()
        apiViewModel.getCities()
    }

    private fun setUpCountryCode() = with(binding) {
        countryCodePicker = CountryCodePicker(context)
        countryCodePicker.setTypeFace(
            ResourcesCompat.getFont(requireContext(), R.font.lufga_regular)
        )
        countryCodePicker.setAutoDetectedCountry(true)
        countryCodePicker.setCountryForPhoneCode(91)
        countryCode = countryCodePicker.selectedCountryCodeWithPlus
        countryShortCode = countryCodePicker.selectedCountryNameCode
        includedMobileNumber.textViewCountryCode.text = countryCode
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_complete)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_your_profile)
        includedTopContent.textViewYouHaveMissed.text = getString(
            R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it
        )
//        includedFullName.editText.setText(String.format("%s", session.user?.name))
//        includedUserId.editText.setText(String.format("%s", session.user?.userId))
//        includedMobileNumber.editText.setText(String.format("%s", session.user?.mobile))
        includedMobileNumber.textViewCountryCode.text = String.format("%s", "+91")

        commonFieldSelectionBottomSheetForGender.setOnOkButtonClickListener {
            includedGender.editText.setText(it.options)
        }
        commonFieldSelectionBottomSheetForCity.setOnOkButtonClickListener {
            includedCityYouDriveIn.editText.setText(it.options)
        }
    }

    private fun setUpData() = with(binding) {
        /* if (session.isPersonalDetailsAdded) {
             includedGender.editText.setText(String.format("%s", "Male"))
             includedCityYouDriveIn.editText.setText(String.format("%s", "Ahmedabad"))
             includedGender.editText.isFocusable = false
             includedGender.editText.isEnabled = false
             includedCityYouDriveIn.editText.isFocusable = false
             includedCityYouDriveIn.editText.isEnabled = false
         }*/
    }

    private fun setUpEditText() = with(binding) {
        includedFullName.textViewTitle.text = getString(R.string.hint_full_name)
        includedFullName.editText.hint = getString(R.string.dummy_username_value)
        includedUserId.textViewTitle.text = getString(R.string.title_user_id)
        includedUserId.editText.hint = getString(R.string.hint_ex_rahul_patel)
        includedMobileNumber.textViewTitle.text = getString(R.string.title_mobile_number)
        includedMobileNumber.editText.hint = getString(R.string.hiint_987654321)
        includedGender.textViewTitle.text = getString(R.string.title_gender)
        includedGender.editText.hint = getString(R.string.hint_select_gender)
        includedCityYouDriveIn.textViewTitle.text = getString(R.string.title_city_you_drive_in)
        includedCityYouDriveIn.editText.hint = getString(R.string.hint_select_city)
        includedFullName.editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        includedUserId.editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        includedMobileNumber.editText.imeOptions = EditorInfo.IME_ACTION_DONE
//        includedFullName.editText.isFocusable = false
        includedUserId.editText.isFocusable = false
        includedMobileNumber.editText.isFocusable = false
        includedMobileNumber.imageViewVerify.isVisible(true)
        includedGender.editText.doAfterTextChanged {
            checkEmptyEditText()
        }

        includedCityYouDriveIn.editText.doAfterTextChanged {
            checkEmptyEditText()
        }
    }

    private fun checkEmptyEditText() = with(binding) {
        if (includedGender.editText.trimmedText.isEmpty()
                .not() && includedCityYouDriveIn.editText.trimmedText.isEmpty().not()
        ) {
            buttonSave.isClickable = true
            buttonSave.isEnabled = true
            buttonSave.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        }
    }

    private fun setUpClickListener() = with(binding) {
        buttonSave.setOnClickListener {
            validate()
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
                optionList = getAllCities
            ).setTitle("Select City")
                .setSelectedOption(includedCityYouDriveIn.editText.trimmedText)
                .show(childFragmentManager, null)
        }
        //optionList = apiViewModel.getCitiesLiveData.value?.resBody?.data?: arrayListOf<CommonFieldSelection>()

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

            validator.submit(includedGender.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_select_gender)).check()

            validator.submit(includedCityYouDriveIn.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_select_city)).check()

            /*session.isPersonalDetailsAdded = true
            navigator.goBack()
*/
            showLoader()
            apiViewModel.updateProfile(
                Request(
                    name = binding.includedFullName.editText.text.toString().trim(),
                    userId = binding.includedUserId.editText.text.toString().trim(),
                    mobile = binding.includedMobileNumber.editText.text.toString().trim(),
                    gender = binding.includedGender.editText.text.toString().trim(),
                    driverInCity = binding.includedCityYouDriveIn.editText.text.toString().trim()
                )
            )
        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}