package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.CommonResponse
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.AccountAddNewPlaceFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.common.fieldselection.bottomsheet.CommonFieldSelectionBottomSheet
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import com.helloyatri.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountAddNewAddressFragment : BaseFragment<AccountAddNewPlaceFragmentBinding>() {
    private var address: String? = null
    var name: String? = null
    private var lat: String? = null
    var long: String? = null
    private var commonFieldSelection: CommonFieldSelection? = null
    private var IS_FROM_EDIT = false
    private val apiViewModel: ApiViewModel by activityViewModels()
    private var savedAddress: SavedAddress? = null
    private val commonFieldSelectionBottomSheetForCategory by lazy {
        CommonFieldSelectionBottomSheet()
    }

    companion object {

        fun createBundle(
            isFromEdit: Boolean = false,
            data: String? = null,
        ) = bundleOf(
            "isFromEdit" to isFromEdit, "data" to data
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IS_FROM_EDIT = arguments?.getBoolean("isFromEdit") ?: false
        savedAddress = Gson().fromJson(arguments?.getString("data") ?: "", SavedAddress::class.java)
        lat = savedAddress?.latitude ?: ""
        long = savedAddress?.longitude ?: ""
        address = savedAddress?.location ?: ""
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): AccountAddNewPlaceFragmentBinding {
        val view = AccountAddNewPlaceFragmentBinding.inflate(layoutInflater)
        return view
    }

    override fun bindData() {
        setUpView()
        fileUpForm()
        setUpClickListeners()
        initObservers()
    }

    private fun fileUpForm() = with(binding) {
        if (IS_FROM_EDIT) {
            buttonAdd.text = getString(R.string.btn_update)
            includedNameThisPlace.editText.setText(savedAddress?.name ?: "")
            includedAddress.editText.setText(savedAddress?.location ?: "")
        } else {
            buttonAdd.text = getString(R.string.label_add)
            includedNameThisPlace.editText.setText("")
            includedAddress.editText.setText("")
        }
        commonFieldSelection?.let {
            commonFieldSelectionBottomSheetForCategory.setSelect(commonFieldSelection)
            includedCategory.editText.setText(it.options)
        }
    }

    private fun setUpView() = with(binding) {
        includedCategory.textViewTitle.text = getString(R.string.title_category)
        includedCategory.editText.hint = getString(R.string.select_category)
        includedNameThisPlace.textViewTitle.text = getString(R.string.title_name_this_place)
        includedNameThisPlace.editText.hint = getString(R.string.hint_ex_near_home)
        includedAddress.textViewTitle.text = getString(R.string.title_address)
        includedAddress.editText.hint = getString(R.string.hint_ex_101_national_san_bruno_ca_94580)
        includedNameThisPlace.editText.imeOptions = EditorInfo.IME_ACTION_DONE
        includedAddress.editText.isEnabled = false
        includedAddress.editText.isClickable = false
        includedNameThisPlace.editText.doAfterTextChanged {
            name = it.toString()
        }
        includedAddress.editText.doAfterTextChanged {
            address = it.toString()
        }
        val optionsList = arrayListOf(
            CommonFieldSelection(options = getString(R.string.option_home), id = 1),
            CommonFieldSelection(options = getString(R.string.option_work), id = 2),
            CommonFieldSelection(options = getString(R.string.option_school), id = 3),
            CommonFieldSelection(options = getString(R.string.option_other), id = 4),
        )
        commonFieldSelectionBottomSheetForCategory.setOptionsList(
            optionsList
        )
        optionsList.find { it.id == (savedAddress?.type ?: 0) }?.let {
            commonFieldSelection = it
        }
    }

    private fun setUpClickListeners() = with(binding) {
        includedCategory.editText.setOnClickListener {
            commonFieldSelectionBottomSheetForCategory.show(childFragmentManager, null)
        }
        textViewSetLocationMap.setOnClickListener {
            val savedAddress = SavedAddress(latitude = lat, longitude = long, location = address)
            navigator.load(AddressLocationFragment::class.java).setBundle(
                AddressLocationFragment.createBundle(
                    data = Gson().toJson(savedAddress)
                )
            ).add(true)
        }
        buttonAdd.setOnClickListener {
            if (isValidate()) {
                addOrUpdateAddressOnServer()
            }
        }
        commonFieldSelectionBottomSheetForCategory.setOnOkButtonClickListener {
            commonFieldSelection = it
            includedCategory.editText.setText(it.options)
        }
    }

    private fun addOrUpdateAddressOnServer() = with(binding) {
        savedAddress?.id?.let {
            apiViewModel.updateAddress(
                Request(
                    name = includedNameThisPlace.editText.text.toString().trim(),
                    longitude = long,
                    latitude = lat,
                    location = includedAddress.editText.text.toString().trim(),
                    type = commonFieldSelection?.id ?: Constants.PLACE_CATEGORY,
                    id = it
                )
            )
        } ?: run {
            apiViewModel.updateAddress(
                Request(
                    name = includedNameThisPlace.editText.text.toString().trim(),
                    longitude = long,
                    latitude = lat,
                    location = includedAddress.editText.text.toString().trim(),
                    type = commonFieldSelection?.id ?: Constants.PLACE_CATEGORY
                )
            )
        }
    }

    private fun initObservers() {
        apiViewModel._sharedData.observe(this) { updatedData ->
            updatedData?.let {
                // Update the UI with the new data
                address = updatedData.first
                lat = updatedData.second
                long = updatedData.third
                binding.apply {
                    includedNameThisPlace.editText.setText(
                        name ?: ""
                    )
                    includedAddress.editText.setText(
                        address ?: ""
                    )
                }
                apiViewModel._sharedData.value = null
            }

        }
        apiViewModel.updateAddressLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        resource.data?.let {
                            Gson().fromJson(it.toString(), CommonResponse::class.java)?.let {
                                it.message?.let {
                                    showMessage(it)
                                }
                            }
                        }
                        apiViewModel.updateAddressLiveData.value = null

                        // Solution for observing again and again
//                        Handler(Looper.getMainLooper()).postDelayed({
//
////                            navigator.goBack()
////                            apiViewModel.updateAddressLiveData.value = null
//                        }, 1500)
                    }

                    Status.ERROR -> {
                        hideLoader()
                    }

                    Status.LOADING -> showLoader()
                }
            }
        }
    }

    private fun isValidate(): Boolean {
        try {
            binding.apply {
                commonFieldSelection?.let {} ?: run {
                    validator.submit(includedCategory.editText).checkEmpty()
                        .errorMessage(getString(R.string.validation_please_select_category)).check()
                }
                validator.submit(includedCategory.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_select_category)).check()

                validator.submit(includedNameThisPlace.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_enter_name_this_place))
                    .check()

                validator.submit(includedAddress.editText).checkEmpty()
                    .errorMessage(getString(R.string.validation_please_enter_address)).check()

            }
            return true
        } catch (e: ApplicationException) {
            showMessage(e.message)
            return false
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarTitle(
            if (IS_FROM_EDIT) getString(
                R.string.title_update_saved_place
            ) else getString(R.string.title_add_new_place)
        ).build()
    }
}