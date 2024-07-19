package com.helloyatri.ui.auth.fragment

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import com.helloyatri.R
import com.helloyatri.data.model.DriverVerification
import com.helloyatri.databinding.AuthDriverVerificationFragmentBinding
import com.helloyatri.ui.activity.AuthActivity
import com.helloyatri.ui.auth.adapter.DriverVerificationAdapter
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants.REQUEST_CALL_PERMISSION
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverVerificationFragment : BaseFragment<AuthDriverVerificationFragmentBinding>() {

    private val driverVerificationAdapter by lazy {
        DriverVerificationAdapter()
    }

    private val driverVerificationDataList = ArrayList<DriverVerification>()
    var phoneNumber: String? = null

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

    private fun makePhoneCall(phoneNumber: String) {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CALL_PHONE
                )
            } != PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CALL_PERMISSION
                )
            }
        } else {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                phoneNumber?.let { makePhoneCall(it) }
            }
        }
    }

    private fun openGoogleMaps(latitude: String, longitude: String) {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (activity?.packageManager?.let { mapIntent.resolveActivity(it) } != null) {
            startActivity(mapIntent)
        } else {
            // Handle the case when Google Maps is not installed
            println("Google Maps is not installed.")
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

        } else {
            Log.i("TAG", "setUpData: 11")
            setUpDefaultContactData()
            setUpDefaultLocationData()
        }
        driverVerificationAdapter.setItems(driverVerificationDataList, 1)
    }

    private fun setUpDefaultContactData() {
        Log.i("TAG", "setUpDefaultContactData: ")
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