package com.helloyatri.ui.auth.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.GetAllRequiredDocument
import com.helloyatri.data.model.GetTypeModel
import com.helloyatri.data.model.GetVehicleDetailsModel
import com.helloyatri.databinding.AuthDriverVehicleDetailsFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.common.fieldselection.bottomsheet.CommonFieldSelectionBottomSheet
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.trimmedText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverVehicleDetailsFragment : BaseFragment<AuthDriverVehicleDetailsFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    private val commonFieldSelectionBottomSheetForVehicleType by lazy {
        CommonFieldSelectionBottomSheet()
    }

    private val commonFieldSelectionBottomSheetForNumberOfSeats by lazy {
        CommonFieldSelectionBottomSheet()
    }

    private val commonFieldSelectionBottomSheetForVehicleFuelType by lazy {
        CommonFieldSelectionBottomSheet()
    }

    private val commonFieldSelectionBottomSheetForVehicleModelYear by lazy {
        CommonFieldSelectionBottomSheet()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverVehicleDetailsFragmentBinding {
        return AuthDriverVehicleDetailsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpEditText()
        setUpClickListener()
        setUpData()
        getVehicleDetailsAPI()
        getVehicleTypeAPI()
        initObservers()
    }

    private fun initObservers() {

        apiViewModel.getVehicleDetailsLiveData.observe(this){resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetVehicleDetailsModel::class.java)
                        response?.data?.let {

                        } ?: run {
                            showSomethingMessage()
                        }
                    } ?: run {
                        showSomethingMessage()
                    }
                }
                Status.ERROR ->{
                    hideLoader()
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }
                Status.LOADING -> hideLoader()
            }
        }

        apiViewModel.getVehicleTypeLiveData.observe(this){resource ->
            when(resource.status){
                Status.SUCCESS ->{
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetTypeModel::class.java)
                        response?.data?.let {

                        } ?: run {
                            showSomethingMessage()
                        }
                    } ?: run {
                        showSomethingMessage()
                    }                }
                Status.ERROR ->{
                    hideLoader()
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }
                Status.LOADING -> hideLoader()
            }
        }

    }

    private fun getVehicleTypeAPI() {
        apiViewModel.getVehicleType()
    }

    private fun getVehicleDetailsAPI() {
        apiViewModel.getVehicleDetails()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_complete)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_details)
        includedTopContent.textViewYouHaveMissed.text = getString(
                R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it)

        commonFieldSelectionBottomSheetForVehicleType.setOnOkButtonClickListener {
            includedVehicleType.editText.setText(it.options)
        }

        commonFieldSelectionBottomSheetForNumberOfSeats.setOnOkButtonClickListener {
            includedNumberOfSeats.editText.setText(it.options)
        }

        commonFieldSelectionBottomSheetForVehicleFuelType.setOnOkButtonClickListener {
            includedVehicleFuelType.editText.setText(it.options)
        }

        commonFieldSelectionBottomSheetForVehicleModelYear.setOnOkButtonClickListener {
            includedVehicleModelYear.editText.setText(it.options)
        }
    }

    private fun setUpData() = with(binding) {
        if (session.isAddVehicle) {
            includedVehicleName.editText.setText(String.format("%s", "Honda City"))
            includedVehicleType.editText.setText(String.format("%s", "Sedan"))
            includedNumberOfSeats.editText.setText(String.format("%s", "4"))
            includedVehicleNumber.editText.setText(String.format("%s", "GA04ED1111"))
            includedVehicleFuelType.editText.setText(String.format("%s", "Petrol"))
            includedVehicleModelYear.editText.setText(String.format("%s", "2019"))
            includedVehicleName.editText.isFocusable = false
            includedVehicleName.editText.isEnabled = false
            includedVehicleType.editText.isFocusable = false
            includedVehicleType.editText.isEnabled = false
            includedNumberOfSeats.editText.isFocusable = false
            includedNumberOfSeats.editText.isEnabled = false
            includedVehicleNumber.editText.isFocusable = false
            includedVehicleNumber.editText.isEnabled = false
            includedVehicleFuelType.editText.isFocusable = false
            includedVehicleFuelType.editText.isEnabled = false
            includedVehicleModelYear.editText.isFocusable = false
            includedVehicleModelYear.editText.isEnabled = false
        }
    }

    private fun setUpEditText() = with(binding) {
        includedVehicleName.textViewTitle.text = getString(R.string.title_vehicle_name)
        includedVehicleName.editText.hint = getString(R.string.hint_eg_honda_civic)
        includedVehicleType.textViewTitle.text = getString(R.string.title_vehicle_type)
        includedVehicleType.editText.hint = getString(R.string.hint_select_vehicle_type)
        includedNumberOfSeats.textViewTitle.text = getString(R.string.title_no_of_seats)
        includedNumberOfSeats.editText.hint = getString(R.string.hint_select_no_of_seats)
        includedVehicleNumber.textViewTitle.text = getString(R.string.title_vehicle_number)
        includedVehicleNumber.editText.hint = getString(R.string.hint_eg_ga03r3712)
        includedVehicleFuelType.textViewTitle.text = getString(R.string.title_vehicle_fuel_type)
        includedVehicleFuelType.editText.hint = getString(R.string.hint_select_fuel_type)
        includedVehicleModelYear.textViewTitle.text = getString(R.string.title_vehicle_model_year)
        includedVehicleModelYear.editText.hint = getString(R.string.hint_select_year)
        includedVehicleName.editText.imeOptions = EditorInfo.IME_ACTION_DONE
        includedVehicleNumber.editText.imeOptions = EditorInfo.IME_ACTION_DONE

        includedVehicleName.editText.doAfterTextChanged {
            checkEmptyEditText()
        }
        includedVehicleType.editText.doAfterTextChanged {
            checkEmptyEditText()
        }
        includedNumberOfSeats.editText.doAfterTextChanged {
            checkEmptyEditText()
        }
        includedVehicleNumber.editText.doAfterTextChanged {
            checkEmptyEditText()
        }
        includedVehicleFuelType.editText.doAfterTextChanged {
            checkEmptyEditText()
        }
        includedVehicleModelYear.editText.doAfterTextChanged {
            checkEmptyEditText()
        }
    }

    private fun checkEmptyEditText() = with(binding) {
        if (includedVehicleName.editText.trimmedText.isEmpty()
                    .not() && includedVehicleType.editText.trimmedText.isEmpty()
                    .not() && includedNumberOfSeats.editText.trimmedText.isEmpty()
                    .not() && includedVehicleNumber.editText.trimmedText.isEmpty()
                    .not() && includedVehicleFuelType.editText.trimmedText.isEmpty()
                    .not() && includedVehicleModelYear.editText.trimmedText.isEmpty().not()) {
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

        includedVehicleType.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForVehicleType.setOptionsList(
                    optionList = arrayListOf(CommonFieldSelection(options = "Sedan"),
                            CommonFieldSelection(options = "Hatchback"),
                            CommonFieldSelection(options = "Compact Sedan")))
                    .setTitle("Select Vehicle Type")
                    .setSelectedOption(includedVehicleType.editText.trimmedText)
                    .show(childFragmentManager, null)
        }

        includedNumberOfSeats.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForNumberOfSeats.setOptionsList(
                    optionList = arrayListOf(CommonFieldSelection(options = "1"),
                            CommonFieldSelection(options = "2"),
                            CommonFieldSelection(options = "3"),
                            CommonFieldSelection(options = "4"),
                            CommonFieldSelection(options = "5"))).setTitle("Select Seats")
                    .setSelectedOption(includedNumberOfSeats.editText.trimmedText)
                    .show(childFragmentManager, null)
        }

        includedVehicleFuelType.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForVehicleFuelType.setOptionsList(
                    optionList = arrayListOf(CommonFieldSelection(options = "Petrol"),
                            CommonFieldSelection(options = "Diesel"),
                            CommonFieldSelection(options = "CNG"))).setTitle("Select Fuel Type")
                    .setSelectedOption(includedVehicleFuelType.editText.trimmedText)
                    .show(childFragmentManager, null)
        }

        includedVehicleModelYear.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForVehicleModelYear.setOptionsList(
                    optionList = arrayListOf(CommonFieldSelection(options = "2018"),
                            CommonFieldSelection(options = "2019"),
                            CommonFieldSelection(options = "2020"))).setTitle("Select Model Year")
                    .setSelectedOption(includedVehicleModelYear.editText.trimmedText)
                    .show(childFragmentManager, null)
        }
    }

    private fun validate() = with(binding) {
        try {
            validator.submit(includedVehicleName.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_enter_vehicle_name))
                    .checkMinDigits(Constants.MIN_NAME)
                    .errorMessage(getString(R.string.validation_please_enter_valid_vehicle_name))
                    .check()

            validator.submit(includedVehicleType.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_select_vehicle_type)).check()

            validator.submit(includedNumberOfSeats.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_select_no_of_seats)).check()

            validator.submit(includedVehicleNumber.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_enter_vehicle_number))
                    .checkMinDigits(Constants.MIN_NAME)
                    .errorMessage(getString(R.string.validation_please_enter_valid_vehicle_number))
                    .check()

            validator.submit(includedVehicleFuelType.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_select_vehicle_fuel_type))
                    .check()

            validator.submit(includedVehicleModelYear.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_select_vehicle_model_year))
                    .check()

            session.isAddVehicle = true
            navigator.goBack()

        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }


}