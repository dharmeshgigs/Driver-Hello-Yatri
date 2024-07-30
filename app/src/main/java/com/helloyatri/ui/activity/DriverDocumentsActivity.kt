package com.helloyatri.ui.activity

import android.os.Bundle
import android.view.View
import com.helloyatri.R
import com.helloyatri.databinding.DriverDocumentsAcitivtyBinding
import com.helloyatri.ui.auth.fragment.DriverDocumentsFragment
import com.helloyatri.ui.auth.fragment.DriverVerificationFragment
import com.helloyatri.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDocumentsActivity : BaseActivity() {

    private lateinit var driverDocumentsActivityBinding: DriverDocumentsAcitivtyBinding

    override fun findFragmentPlaceHolder(): Int {
        return R.id.placeHolder
    }

    override fun createViewBinding(): View {
        driverDocumentsActivityBinding = DriverDocumentsAcitivtyBinding.inflate(layoutInflater)
        return driverDocumentsActivityBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        if (appSession.isInitial && appSession.isAllDocumentUploaded()) {
            load(DriverVerificationFragment::class.java).clearHistory(null).replace(true)
        } else {
            load(DriverDocumentsFragment::class.java).clearHistory(null).replace(true)
        }
    }

    private fun setUpToolbar() = with(driverDocumentsActivityBinding) {
        setToolbar(customToolbar)
        showToolbar(false)
    }
}