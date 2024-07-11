package com.helloyatri.ui.auth.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.data.response.DriverVerification
import com.helloyatri.databinding.AuthDriverVerificationFragmentBinding
import com.helloyatri.ui.activity.AuthActivity
import com.helloyatri.ui.auth.adapter.DriverVerificationAdapter
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverVerificationFragment : BaseFragment<AuthDriverVerificationFragmentBinding>() {

    private val driverVerificationAdapter by lazy {
        DriverVerificationAdapter()
    }

    private val driverVerificationDataList = ArrayList<DriverVerification>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverVerificationFragmentBinding {
        return AuthDriverVerificationFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_welcome)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.dummy_rahul_patel)
        includedTopContent.textViewYouHaveMissed.text =
                getString(R.string.label_you_have_successfully_created_an_account)
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverVerification.apply {
            adapter = driverVerificationAdapter
        }
    }

    private fun setUpClickListener() = with(binding) {
        driverVerificationAdapter.setOnItemClickPositionListener { _, _ ->
            buttonYouVerified.isClickable = true
            buttonYouVerified.isEnabled = true
            buttonYouVerified.text = getString(R.string.btn_you_are_verified_login)
            buttonYouVerified.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        }

        buttonYouVerified.setOnClickListener {
            session.isDriverVerified = true
            navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
        }
    }

    private fun setUpData() {
        driverVerificationDataList.clear()
        driverVerificationDataList.add(DriverVerification(image = R.drawable.image_help_desk,
                text = "+18 98765 43210 \n+91 98765 43210", button = "Call agency"))
        driverVerificationDataList.add(DriverVerification(image = R.drawable.image_navigate_there,
                text = "Cinemax, Rectory Cottage, Court Road", button = "Navigate there"))
        driverVerificationAdapter.setItems(driverVerificationDataList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}