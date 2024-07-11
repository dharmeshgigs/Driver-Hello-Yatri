package com.helloyatri.ui.home.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.databinding.CommonYesNoDialogFragmentBinding
import com.helloyatri.ui.base.BaseDialogFragment
import com.helloyatri.utils.extension.setDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommonYesNoDialogFragment(private val callBack: (action: String) -> Unit) :
        BaseDialogFragment<CommonYesNoDialogFragmentBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): CommonYesNoDialogFragmentBinding {
        return CommonYesNoDialogFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        isCancelable = false
        setUpUi()
        setClickListener()
    }

    private fun setUpUi() = with(binding) {
        textViewTitle.text = arguments?.getString(TITLE)
        imageViewCenter.setDrawable(arguments?.getInt(ICON))
        textViewNo.text = arguments?.getString(CANCEL_TEXT)
        textViewYes.text = arguments?.getString(OK_TEXT)
    }

    companion object {
        const val YES = "YES"
        const val NO = "NO"
        const val TITLE = "TITLE"
        const val ICON = "ICON"
        const val CANCEL_TEXT = "CANCEL_TEXT"
        const val OK_TEXT = "OK_TEXT"
    }

    private fun setClickListener() = with(binding) {
        textViewYes.setOnClickListener {
            callBack.invoke(YES)
            dismiss()
        }

        textViewNo.setOnClickListener {
            callBack.invoke(NO)
            dismiss()
        }
    }
}