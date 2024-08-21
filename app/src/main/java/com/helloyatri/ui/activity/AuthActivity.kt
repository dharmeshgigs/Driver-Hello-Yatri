package com.helloyatri.ui.activity

import android.os.Bundle
import android.view.View
import com.helloyatri.R
import com.helloyatri.databinding.AuthAcitivtyBinding
import com.helloyatri.ui.auth.fragment.LoginFragment
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.base.MapBaseFragment
import com.helloyatri.ui.manager.FragmentActionPerformer
import com.helloyatri.ui.tutorial.fragment.AuthTutorialFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private lateinit var authAcitivtyBinding: AuthAcitivtyBinding

    override fun findFragmentPlaceHolder(): Int {
        return R.id.placeHolder
    }

    override fun createViewBinding(): View {
        authAcitivtyBinding = AuthAcitivtyBinding.inflate(layoutInflater)
        return authAcitivtyBinding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        if (appSession.isInitial) {
            load(LoginFragment::class.java).replace(false)
        } else {
            load(AuthTutorialFragment::class.java).replace(false)
        }
    }

    private fun setUpToolbar() = with(authAcitivtyBinding) {
        setToolbar(customToolbar)
        showToolbar(false)
    }

}