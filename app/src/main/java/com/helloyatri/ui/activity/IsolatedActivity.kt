package com.helloyatri.ui.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.databinding.IsolatedAcitivtyFullBinding
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.manager.ActivityStarter
import com.helloyatri.utils.extension.changeStatusBarColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IsolatedActivity : BaseActivity() {

    private lateinit var isolatedFullActivityBinding: IsolatedAcitivtyFullBinding

    override fun findFragmentPlaceHolder(): Int {
        return R.id.placeHolder
    }

    override fun createViewBinding(): View {
        isolatedFullActivityBinding = IsolatedAcitivtyFullBinding.inflate(layoutInflater)
        return isolatedFullActivityBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        changeStatusBarColor(ContextCompat.getColor(applicationContext, R.color.backgroundColor),
                true)
        if (savedInstanceState == null) {
            val page = intent.getSerializableExtra(
                    ActivityStarter.ACTIVITY_FIRST_PAGE) as Class<BaseFragment<*>>
            load(page).setBundle(intent.extras!!).replace(false)
        }
    }

    private fun setUpToolbar() = with(isolatedFullActivityBinding) {
        setToolbar(customToolbar)
    }
}