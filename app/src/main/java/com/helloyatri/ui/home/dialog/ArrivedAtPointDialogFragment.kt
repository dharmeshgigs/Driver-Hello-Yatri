package com.helloyatri.ui.home.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.databinding.ArrivedAtPointDialogFragmentBinding
import com.helloyatri.ui.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArrivedAtPointDialogFragment(private val callBack: (action: String) -> Unit) :
        BaseDialogFragment<ArrivedAtPointDialogFragmentBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): ArrivedAtPointDialogFragmentBinding {
        return ArrivedAtPointDialogFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        isCancelable = false
        setClickListener()
    }

    companion object {
        const val YES = "YES"
        const val NO = "NO"
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