package com.helloyatri.ui.home.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.EmergencyAssistance
import com.helloyatri.databinding.EmergencyAssistanceBottomSheetBinding
import com.helloyatri.ui.base.BaseBottomSheetDialogFragment
import com.helloyatri.ui.home.adapter.AdapterEmergencyAssistance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyAssistanceBottomSheet :
        BaseBottomSheetDialogFragment<EmergencyAssistanceBottomSheetBinding>() {

    private val adapterEmergencyAssistance by lazy {
        AdapterEmergencyAssistance()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): EmergencyAssistanceBottomSheetBinding {
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
            adapterEmergencyAssistance.setItems(arrayListOf(
                    EmergencyAssistance(title = "Call Hello Yatri Line",
                            icon = R.drawable.image_call_support),
                    EmergencyAssistance(title = "Get help from police",
                            icon = R.drawable.image_call_police),
                    EmergencyAssistance(title = "Report a crash", icon = R.drawable.image_crash)),
                    1)
        }
    }
}