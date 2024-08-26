package com.helloyatri.ui.home.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.CancelRideReasons
import com.helloyatri.databinding.CancelRideBottomSheetBinding
import com.helloyatri.ui.base.BaseBottomSheetDialogFragment
import com.helloyatri.ui.home.adapter.AdapterCancelRideReasons
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelRideBottomSheet(
    private val cancelRideCallBack: (reason: String) -> Unit,
    private val cencellationDataList: ArrayList<String>
) :
        BaseBottomSheetDialogFragment<CancelRideBottomSheetBinding>() {


    private val adapterCancelRideReasons by lazy {
        AdapterCancelRideReasons()
    }
    private val cancelRideList: MutableList<CancelRideReasons> = mutableListOf()

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

        var cancelReason = ""
        val reason = adapterCancelRideReasons.items!!.find { it.isSelected }
        reason?.let {
            if(it.title.equals("Other")) {
                if(reason.reportReasonOther.isNullOrEmpty()) {
                    showMessage(getString(R.string.message_please_enter_reason))
                    return
                }
                cancelReason= it.reportReasonOther ?: ""
            } else {
                cancelReason= it.title ?: ""
            }
        } ?: run {
            showMessage(getString(R.string.message_please_select_at_least_one_reason))
            return
        }
        dismiss()
        cancelRideCallBack.invoke(cancelReason)
    }

    private fun setAdapter() = with(binding) {
        recyclerViewCancelReasons.apply {
            adapter = adapterCancelRideReasons
           // adapterCancelRideReasons.setItems(requireActivity().getCancelRideReasons(), 1)
            cancelRideList.clear()
            cencellationDataList.forEach {
                cancelRideList.add(CancelRideReasons(title = it))
            }
            adapterCancelRideReasons.setItems(cancelRideList, 1)
        }
    }
}