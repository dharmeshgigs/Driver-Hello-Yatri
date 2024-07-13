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

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): AuthDriverVerificationFragmentBinding {
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
        includedTopContent.textViewWelcomeBack.text = session.user?.name ?: ""
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
//            buttonYouVerified.isClickable = true
//            buttonYouVerified.isEnabled = true
//            buttonYouVerified.text = getString(R.string.btn_you_are_verified_login)
//            buttonYouVerified.backgroundTintList =
//                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        }

        buttonYouVerified.setOnClickListener {
            session.isDriverVerified = true
            navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
        }
    }

    private fun setUpData() {
        driverVerificationDataList.clear()
        if (session.isInitial) {
            session.verificationDetails?.CONTACTDETAILS?.let {
                it.CONTACTNUMBERS?.let {
                    if (it.isNotEmpty()) {
                        var contactNo: String = ""
                        it.forEachIndexed { i, s ->
                            if (i == 0) {
                                contactNo = s
                            } else {
                                contactNo = contactNo.plus("\n").plus(s)
                            }
                        }
                        driverVerificationDataList.add(
                            DriverVerification(
                                image = R.drawable.image_help_desk,
                                text = contactNo,
                                button = session.verificationDetails?.CONTACTDETAILS?.BTNLBL
                                    ?: getString(R.string.call_agency)
                            )
                        )
                    } else {
                        setUpDefaultContactData()
                    }
                } ?: run { setUpDefaultContactData() }
            } ?: run { setUpDefaultContactData() }
            session.verificationDetails?.LOCATION?.let {
                it.ADDRESS?.let {
                    driverVerificationDataList.add(
                        DriverVerification(
                            image = R.drawable.image_navigate_there,
                            text = it,
                            button = getString(R.string.navigate_there)
                        )
                    )
                } ?: run { setUpDefaultLocationData() }
            } ?: run { setUpDefaultLocationData() }
            binding.textViewDriverDescription.text = session.verificationDetails?.description
                ?: getString(R.string.dummy_lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard_dummy_text_ever_since)
            binding.buttonYouVerified.text = session.verificationDetails?.btn_lbl
                ?: if (session.isDriverVerified) getString(R.string.btn_you_are_verified_login) else getString(
                    R.string.btn_your_verification_pending
                )

        } else {
            setUpDefaultContactData()
            setUpDefaultLocationData()
        }
        driverVerificationAdapter.setItems(driverVerificationDataList, 1)
    }

    private fun setUpDefaultContactData() {
        driverVerificationDataList.add(
            DriverVerification(
                image = R.drawable.image_help_desk,
                text = "+18 98765 43210 \n+91 98765 43210",
                button = getString(R.string.call_agency)
            )
        )
    }

    private fun setUpDefaultLocationData() {
        driverVerificationDataList.add(
            DriverVerification(
                image = R.drawable.image_navigate_there,
                text = "Cinemax, Rectory Cottage, Court Road",
                button = getString(R.string.navigate_there)
            )
        )
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}