package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.helloyatri.network.Status
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.databinding.AccountAddNewPlaceFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountAddNewAddressFragment : BaseFragment<AccountAddNewPlaceFragmentBinding>() {
    var address: String? = null
    var name: String? = null
    var lat: String? = null
    var long: String? = null

    companion object {
        const val IS_FROM_EDIT = "isFromEdit"

        fun createBundle(
            isFromEdit: Boolean = false,
            lat: String? = null,
            long: String? = null,
            name: String? = null,
            location: String? = null
        ) = bundleOf(
            IS_FROM_EDIT to isFromEdit,
            "lat" to lat,
            "long" to long,
            "name" to name,
            "location" to location
        )
    }

    //private val apiViewModel by viewModels<ApiViewModel>()
    private val apiViewModel: ApiViewModel by activityViewModels()


    private val getIsFromEdit by lazy {
        arguments?.getBoolean(IS_FROM_EDIT)
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AccountAddNewPlaceFragmentBinding {
        return AccountAddNewPlaceFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        name = arguments?.getString("name")
        address = arguments?.getString("location")
        lat = arguments?.getString("lat")
        long = arguments?.getString("long")

        setUpText()
        setUpEditText()
        setUpClickListener()
        initObservers()
    }

    private fun initObservers() {
        apiViewModel.sharedData.observe(this) { updatedData ->
            // Update the UI with the new data
            address = updatedData
            setData()
        }

        apiViewModel.longitudeData.observe(this){
            long = it
        }

        apiViewModel.lattitudeData.observe(this){
            lat = it
        }

        apiViewModel.updateAddressLiveData.observe(this) { resourse ->
            when (resourse.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    navigator.goBack()
                }

                Status.ERROR -> {
                    hideLoader()
                }
                Status.LOADING -> showLoader()
            }
        }

    }

    private fun setData() = with(binding) {

            includedNameThisPlace.editText.setText(
                String.format(
                    "%s",
                    name ?: ""
                )
            )
            includedAddress.editText.setText(
                String.format("%s", address ?: "")
            )

    }

    private fun setUpText() = with(binding) {
        if (getIsFromEdit == true) {
            buttonAdd.text = getString(R.string.btn_update)
            includedNameThisPlace.editText.setText(
                String.format(
                    "%s",
                    name ?: ""
                )
            )
            includedAddress.editText.setText(
                String.format("%s", address ?: "")
            )
        } else {
            buttonAdd.text = getString(R.string.label_add)
        }

    }

    private fun setUpEditText() = with(binding) {
        includedNameThisPlace.textViewTitle.text = getString(R.string.title_name_this_place)
        includedNameThisPlace.editText.hint = getString(R.string.hint_ex_near_home)
        includedAddress.textViewTitle.text = getString(R.string.title_address)
        includedAddress.editText.hint = getString(R.string.hint_ex_101_national_san_bruno_ca_94580)
        includedNameThisPlace.editText.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    private fun setUpClickListener() = with(binding) {
        textViewSetLocationMap.setOnClickListener {
            //showMessage("Under Development")
            navigator.load(ConfirmStartRidePointFragment::class.java)
                .setBundle(
                    ConfirmStartRidePointFragment.createBundle(
                        lat = arguments?.getString("lat"),
                        long = arguments?.getString("long"),
                        name = arguments?.getString("name"),
                        location = arguments?.getString("location")
                    )
                )
                .replace(true)
        }

        buttonAdd.setOnClickListener {
            validate()
        }
    }

    private fun validate() = with(binding) {
        try {
            validator.submit(includedNameThisPlace.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_name_this_place))
                .check()

            validator.submit(includedAddress.editText).checkEmpty()
                .errorMessage(getString(R.string.validation_please_enter_address)).check()

            apiViewModel.updateAddress(
                Request(
                    name = includedNameThisPlace.editText.text.toString().trim(),
                    longitude = long,
                    latitude = lat,
                    location = includedAddress.editText.text.toString().trim(),
                    type = "1"
                )
            )

        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarTitle(
            if (getIsFromEdit == true) getString(
                R.string.title_update_saved_place
            ) else getString(R.string.title_add_new_place)
        )
            .build()
    }

    override fun onResume() {
        super.onResume()
    }
}