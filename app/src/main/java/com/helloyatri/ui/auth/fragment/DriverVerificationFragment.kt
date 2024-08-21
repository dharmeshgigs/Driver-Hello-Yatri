package com.helloyatri.ui.auth.fragment

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.helloyatri.network.Status
import com.helloyatri.R
import com.helloyatri.data.model.DriverVerification
import com.helloyatri.databinding.AuthDriverVerificationFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.activity.AuthActivity
import com.helloyatri.ui.auth.adapter.DriverVerificationAdapter
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.extension.enableButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverVerificationFragment : BaseFragment<AuthDriverVerificationFragmentBinding>() {

    private val driverVerificationAdapter by lazy {
        DriverVerificationAdapter()
    }

    private val driverVerificationDataList = ArrayList<DriverVerification>()
    var phoneNumber: String? = null
    private val apiViewModel by viewModels<ApiViewModel>()


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
        initObservers()

    }

    private fun initObservers() {
        apiViewModel.updateDriverVerificationStatusLiveData.observe(this){resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                }
                Status.ERROR -> {
                    hideLoader()
                }
                Status.LOADING -> showLoader()
            }
        }
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
        driverVerificationAdapter.setOnItemClickPositionListener { _, position ->
            if (position == 0) {
                val lines = driverVerificationDataList[position].text?.split("\n")
                val lastElement = lines?.lastOrNull()
                if (lastElement != null) {
                    phoneNumber = lastElement
                    makePhoneCall(phoneNumber!!)
                } else {
                    println("The list is empty.")
                }

            } else if (position == 1) {
                openGoogleMaps(
                    driverVerificationDataList[position].lat.toString(),
                    driverVerificationDataList[position].long.toString()
                )
            }
        }

        buttonYouVerified.setOnClickListener {
            session.isDriverVerified = true
            apiViewModel.updateDriverVerificationStatus()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            // Set the data to the phone number
            intent.data = Uri.parse("tel:$phoneNumber")
            // Start the activity with the intent
            startActivity(intent)
        } catch (e: Exception) {
            e.message?.let {
                showErrorMessage(e.message.toString())
            }
        }
    }

    private fun openGoogleMaps(latitude: String, longitude: String) {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        try {
            if (activity?.packageManager?.let { mapIntent.resolveActivity(it) } != null) {
                startActivity(mapIntent)
            } else {
                // Handle the case when Google Maps is not installed
                println("Google Maps is not installed.")
            }
        } catch (e: Exception) {
            e.message?.let {
                showErrorMessage(e.message.toString())
            }
        }
    }

    private fun setUpData() {
        driverVerificationDataList.clear()
        if (session.isInitial) {
            session.verificationDetails?.CONTACTDETAILS?.let { it ->
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
                driverVerificationDataList.add(
                    DriverVerification(
                        image = R.drawable.image_navigate_there,
                        text = it.ADDRESS,
                        button = getString(R.string.navigate_there),
                        lat = it.LATITUDE,
                        long = it.LONGITUDE
                    )
                )

            } ?: run { setUpDefaultLocationData() }

            binding.textViewDriverDescription.text = session.verificationDetails?.description
                ?: getString(R.string.dummy_lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard_dummy_text_ever_since)
            binding.buttonYouVerified.text = session.verificationDetails?.btn_lbl
                ?: if (session.isDriverVerified) getString(R.string.btn_you_are_verified_login) else getString(
                    R.string.btn_your_verification_pending
                )
            binding.buttonYouVerified.enableButton(session.isDriverVerified)
            binding.buttonYouVerified.isClickable = session.isDriverVerified
            binding.buttonYouVerified.isEnabled = session.isDriverVerified

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
                button = getString(R.string.navigate_there),
                lat = "19.076090",
                long = "72.877426"
            )
        )
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}