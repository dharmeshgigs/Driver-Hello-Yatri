package com.helloyatri.ui.home.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.databinding.CancelRideBottomSheetBinding
import com.helloyatri.ui.base.BaseBottomSheetDialogFragment
import com.helloyatri.ui.home.adapter.AdapterCancelRideReasons
import com.helloyatri.utils.getCancelRideReasons
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelRideBottomSheet(private val cancelRideCallBack: () -> Unit) :
        BaseBottomSheetDialogFragment<CancelRideBottomSheetBinding>() {

    private val adapterCancelRideReasons by lazy {
        AdapterCancelRideReasons()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): CancelRideBottomSheetBinding {
        return CancelRideBottomSheetBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setAdapter()
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        buttonSubmit.setOnClickListener {
            checkValidation()
        }

        imageViewBackButton.setOnClickListener {
            dismiss()
        }
    }

    private fun checkValidation() {
        if (adapterCancelRideReasons.items?.all { data -> !data.isSelected } == true) {
            showMessage(getString(R.string.message_please_select_at_least_one_reason))
            return
        } else if (adapterCancelRideReasons.items!!.any { data -> data.isSelected && data.title == "Other" && data.reportReasonOther == null }) {
            showMessage(getString(R.string.message_please_enter_reason))
            return
        }
        cancelRideCallBack.invoke()
    }

    private fun setAdapter() = with(binding) {
        recyclerViewCancelReasons.apply {
            adapter = adapterCancelRideReasons
            adapterCancelRideReasons.setItems(requireActivity().getCancelRideReasons(), 1)
        }
    }
}