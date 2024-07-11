package com.helloyatri.ui.home.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.databinding.LogoutDialogBinding
import com.helloyatri.ui.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogoutDialogFragment : BaseDialogFragment<LogoutDialogBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): LogoutDialogBinding {
        return LogoutDialogBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        isCancelable = false
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        textViewAccept.setOnClickListener {
            logout()
        }

        textViewDecline.setOnClickListener {
            dismiss()
        }
    }
}