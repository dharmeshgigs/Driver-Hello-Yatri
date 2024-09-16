package com.helloyatri.ui.home.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.data.model.NotificationsData
import com.helloyatri.databinding.NotificationDetailBottomSheetBinding
import com.helloyatri.ui.base.BaseBottomSheetDialogFragment
import com.helloyatri.utils.extension.nullify
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationDetailBottomSheet(val notificationsData: NotificationsData,private val callBack: () -> Unit) :
    BaseBottomSheetDialogFragment<NotificationDetailBottomSheetBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): NotificationDetailBottomSheetBinding {
        return NotificationDetailBottomSheetBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initViews()
        setClickListener()
    }

    private fun initViews() = with(binding) {
        textViewTitle.text = notificationsData.title.nullify()
        textViewDescription.text = notificationsData.message.nullify()
    }

    private fun setClickListener() = with(binding) {
        buttonCancel.setOnClickListener {
            callBack.invoke()
            dismiss()
        }

        imageViewBackButton.setOnClickListener {
            callBack.invoke()
            dismiss()
        }
    }
}