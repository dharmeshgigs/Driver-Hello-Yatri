package com.helloyatri.utils.fileselector

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.helloyatri.databinding.DialogSelectMediaForImageVideoBinding
import com.helloyatri.ui.base.BaseBottomSheetDialogFragment


class DialogSelectMediaForImageVideo(private val callBack: (view: View) -> Unit) :
        BaseBottomSheetDialogFragment<DialogSelectMediaForImageVideoBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): DialogSelectMediaForImageVideoBinding {
        return DialogSelectMediaForImageVideoBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        layoutTakePhoto.setOnClickListener {
            callBack.invoke(it)
            dismissAllowingStateLoss()
        }

        layoutSelectPhoto.setOnClickListener {
            callBack.invoke(it)
            dismissAllowingStateLoss()
        }

        layoutTakeVideo.setOnClickListener {
            callBack.invoke(it)
            dismissAllowingStateLoss()
        }

        layoutSelectVideo.setOnClickListener {
            callBack.invoke(it)
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.attributes?.gravity = Gravity.BOTTOM or Gravity.FILL_HORIZONTAL
        super.onStart()
    }
}