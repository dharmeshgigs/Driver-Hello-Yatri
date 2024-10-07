package com.helloyatri.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.CommonResponse
import com.helloyatri.data.model.DataVehicle
import com.helloyatri.data.model.GetTypeModel
import com.helloyatri.data.model.GetVehicleDetailsModel
import com.helloyatri.databinding.AuthDriverVehicleDetailsFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.common.fieldselection.bottomsheet.CommonFieldSelectionBottomSheet
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.nullify
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

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthDriverVehicleDetailsFragmentBinding {
        return AuthDriverVehicleDetailsFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getVehicleTypeAPI()

        initObservers()
        yearList.clear()
        for (year in 2024 downTo 1969) {
            yearList.addAll(listOf(CommonFieldSelection(options = year.toString())))
        }
    }

    override fun bindData() {
        setUpText()
        setUpEditText()
        setUpClickListener()
    }

    private val getAllVehicleTypeList: ArrayList<CommonFieldSelection> = ArrayList()
    private val yearList: ArrayList<CommonFieldSelection> = ArrayList()
    private val capacityList: ArrayList<CommonFieldSelection> = ArrayList()
    private var capacity: Int? = 0
    private var data: DataVehicle? = DataVehicle()
    var vehicleType: String? = null
    var vehicleId: String? = null

    private fun initObservers() {
        apiViewModel.getVehicleTypeLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let { it ->
                        val response =
                            Gson().fromJson(it.toString(), GetTypeModel::class.java)
                        response?.data?.let {
                            getAllVehicleTypeList.clear()
                            getAllVehicleTypeList.addAll(it)
                            getVehicleDetailsAPI()
                        } ?: run {
                            getVehicleDetailsAPI()
                            // showSomethingMessage()
                        }
                    } ?: run {
                        getVehicleDetailsAPI()
                        // showSomethingMessage()
                    }
                }

                Status.ERROR -> {
                    hideLoader()
                    getVehicleDetailsAPI()
//                    val error =
//                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
//                    showErrorMessage(error)
                }

                Status.LOADING -> hideLoader()
            }
        }

        apiViewModel.getVehicleDetailsLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let { it ->
                        val response =
                            Gson().fromJson(it.toString(), GetVehicleDetailsModel::class.java)
                        response?.data?.let {
                            data = it
                            setData(data!!)
                        } ?: run {
                            showSomethingMessage()
                        }
                    } ?: run {
                        showSomethingMessage()
                    }
                }

                Status.ERROR -> {
                    hideLoader()
//                    val error =
//                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
//                    showErrorMessage(error)
                }

                Status.LOADING -> hideLoader()
            }
        }

        apiViewModel.updateVehicleDetailsLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), CommonResponse::class.java)
                        showMessage(response.message.nullify())
                        session.isAddVehicle = true
                        navigator.goBack()
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
            R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it
        )

        commonFieldSelectionBottomSheetForVehicleType.setOnOkButtonClickListener {
            capacity = it.capacity ?: 0
            vehicleId = it.id.toString()
            includedVehicleType.editText.setText(it.options)
            capacityList(capacity!!)
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
//        includedNumberOfSeats.editText.doAfterTextChanged {
//            checkEmptyEditText()
//        }
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
                .not() && includedVehicleNumber.editText.trimmedText.isEmpty()
                .not() && includedVehicleFuelType.editText.trimmedText.isEmpty()
                .not() && includedVehicleModelYear.editText.trimmedText.isEmpty().not()
        ) {
            buttonSave.isClickable = true
            buttonSave.isEnabled = true
            buttonSave.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        }
    }

    private fun capacityList(capacity: Int) {
        capacityList.clear()

        for (limit in capacity!! downTo (1)) {
            capacityList.addAll(listOf(CommonFieldSelection(options = limit.toString())))
        }
        binding.includedNumberOfSeats.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForNumberOfSeats.setOptionsList(
                optionList = capacityList
            ).setTitle("Select Seats")
                .setSelectedOption(binding.includedNumberOfSeats.editText.trimmedText)
                .show(childFragmentManager, null)
        }
    }

    private fun setUpClickListener() = with(binding) {
        buttonSave.setOnClickListener {
            validate()
        }

        includedVehicleType.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForVehicleType.setOptionsList(
                optionList = getAllVehicleTypeList
            )
                .setTitle("Select Vehicle Type")
                .setSelectedOption(includedVehicleType.editText.trimmedText)
                .show(childFragmentManager, null)
        }


        includedVehicleFuelType.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForVehicleFuelType.setOptionsList(
                optionList = arrayListOf(
                    CommonFieldSelection(options = "Petrol"),
                    CommonFieldSelection(options = "Diesel"),
                    CommonFieldSelection(options = "CNG")
                )
            ).setTitle("Select Fuel Type")
                .setSelectedOption(includedVehicleFuelType.editText.trimmedText)
                .show(childFragmentManager, null)
        }

        includedVehicleModelYear.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForVehicleModelYear.setOptionsList(
                optionList = yearList
            ).setTitle("Select Model Year")
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
            hideKeyBoard()
            apiViewModel.updateVehicleDetails(
                Request(
                    name = data?.name ?: includedVehicleName.editText.text.toString().trim(),
                    vehicleType = vehicleId,
                    noOfSheets = data?.noOfSheets ?: "1",
                    vehicleNumber = data?.vehicleNumber
                        ?: includedVehicleNumber.editText.text.toString().trim(),
                    fuelType = data?.fuelType ?: includedVehicleFuelType.editText.text.toString()
                        .trim(),
                    modelYear = data?.modelYear ?: includedVehicleModelYear.editText.text.toString()
                        .trim()
                )
            )

        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    private fun setData(data: DataVehicle) = with(binding) {
        includedVehicleName.editText.setText(data?.name ?: "")
        for (item in getAllVehicleTypeList) {
            if (item.id == data.vehicleType) {
                vehicleType = item.options
                vehicleId = item.id.toString()
            }
        }
        includedVehicleType.editText.setText(vehicleType ?: "")
        includedNumberOfSeats.editText.setText(data?.noOfSheets.toString() ?: "")
        includedVehicleNumber.editText.setText(data?.vehicleNumber ?: "")
        includedVehicleFuelType.editText.setText(data?.fuelType ?: "")
        includedVehicleModelYear.editText.setText(data?.modelYear ?: "")
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }


}