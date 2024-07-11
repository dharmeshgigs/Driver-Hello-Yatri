package com.helloyatri.ui.base.loader

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.helloyatri.databinding.LoadingDialogBinding

class LoadingDialog(context: Context, private val setOnCancelListener: () -> Unit) :
    Dialog(context) {

    private lateinit var binding: LoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingDialogBinding.inflate(LayoutInflater.from(context))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        binding.lottieLoader.setAnimation("splash_loader.json")
        setOnCancelListener {
            setOnCancelListener()
        }
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        //window?.setDimAmount(0f)
    }

    override fun onBackPressed() {
        //
    }
}