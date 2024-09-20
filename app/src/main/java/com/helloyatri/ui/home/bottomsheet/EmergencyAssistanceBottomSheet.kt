package com.helloyatri.ui.home.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.EmergencyAssistance
import com.helloyatri.databinding.EmergencyAssistanceBottomSheetBinding
import com.helloyatri.ui.base.BaseBottomSheetDialogFragment
import com.helloyatri.ui.home.adapter.AdapterEmergencyAssistance
import com.helloyatri.utils.AppUtils.openCallDialer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyAssistanceBottomSheet(private val callBack: () -> Unit) :
    BaseBottomSheetDialogFragment<EmergencyAssistanceBottomSheetBinding>() {

    private val adapterEmergencyAssistance by lazy {
        AdapterEmergencyAssistance()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): EmergencyAssistanceBottomSheetBinding {
        return EmergencyAssistanceBottomSheetBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setAdapter()
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        buttonCancel.setOnClickListener {
            dismiss()
        }

        imageViewBackButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setAdapter() = with(binding) {
        recyclerViewEmergencyAssistance.apply {
            adapter = adapterEmergencyAssistance
            adapterEmergencyAssistance.setItems(
                arrayListOf(
                    EmergencyAssistance(
                        title = getString(R.string.label_call_hello_yatri_line),
                        icon = R.drawable.image_call_support
                    ),
                    EmergencyAssistance(
                        title = getString(R.string.label_get_help_from_police),
                        icon = R.drawable.image_call_police
                    ),
                    EmergencyAssistance(title = getString(R.string.label_report_a_crash), icon = R.drawable.image_crash)
                ),
                1
            )
        }
        adapterEmergencyAssistance.setOnItemClickListener {
            if (it.title.equals(getString(R.string.label_call_hello_yatri_line))) {
                dismiss()
                activity?.openCallDialer("100")
            } else if (it.title.equals(getString(R.string.label_get_help_from_police))) {
                dismiss()
                activity?.openCallDialer("100")
            } else if (it.title.equals(getString(R.string.label_report_a_crash))) {
                dismiss()
                callBack.invoke()
            }
        }

    }
}