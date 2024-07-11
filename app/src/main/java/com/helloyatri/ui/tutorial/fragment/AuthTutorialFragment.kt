package com.helloyatri.ui.tutorial.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.databinding.AuthTutorialFragmentBinding
import com.helloyatri.ui.auth.fragment.LoginFragment
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.dialog.CalenderDialog
import com.helloyatri.utils.extension.changeStatusBarColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthTutorialFragment : BaseFragment<AuthTutorialFragmentBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthTutorialFragmentBinding {
        return AuthTutorialFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().changeStatusBarColor(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary), false)
    }

    override fun bindData() {
        setUpClickListener()
    }

    private fun setUpClickListener() = with(binding) {
        buttonGetStarted.setOnClickListener {
            session.isInitial = true
            navigator.load(LoginFragment::class.java).replace(false)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}