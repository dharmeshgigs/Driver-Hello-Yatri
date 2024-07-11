package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import com.helloyatri.R
import com.helloyatri.databinding.AccountAddNewPlaceFragmentBinding
import com.helloyatri.exception.ApplicationException
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountAddNewAddressFragment : BaseFragment<AccountAddNewPlaceFragmentBinding>() {

    companion object {
        const val IS_FROM_EDIT = "isFromEdit"

        fun createBundle(isFromEdit: Boolean = false) = bundleOf(IS_FROM_EDIT to isFromEdit)
    }

    private val getIsFromEdit by lazy {
        arguments?.getBoolean(IS_FROM_EDIT)
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountAddNewPlaceFragmentBinding {
        return AccountAddNewPlaceFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpEditText()
        setUpClickListener()
    }

    private fun setUpText() = with(binding) {
        if (getIsFromEdit == true) {
            buttonAdd.text = getString(R.string.btn_update)
            includedNameThisPlace.editText.setText(String.format("%s", "Home Nearby"))
            includedAddress.editText.setText(
                    String.format("%s", "101 National Dr. San Bruno, CA 94580"))
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
            showMessage("Under Development")
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

            navigator.goBack()

        } catch (e: ApplicationException) {
            showMessage(e.message)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarTitle(if (getIsFromEdit == true) getString(
                R.string.title_update_saved_place) else getString(R.string.title_add_new_place))
                .build()
    }
}