package com.helloyatri.ui.home.fragment

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.network.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.data.model.DriverStatusResponse
import com.helloyatri.data.model.DriverVerification
import com.helloyatri.databinding.AccountDocumentsFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.auth.adapter.DriverVerificationAdapter
import com.helloyatri.ui.auth.fragment.DriverPersonalDetailsFragment
import com.helloyatri.ui.auth.fragment.DriverPersonalProfilePictureFragment
import com.helloyatri.ui.auth.fragment.DriverRequiredDocumentsFragment
import com.helloyatri.ui.auth.fragment.DriverVehicleDetailsFragment
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants.DRIVER_REQUIRED_DOCUMENT
import com.helloyatri.utils.Constants.UPDATE_PROFILE_PICTURE
import com.helloyatri.utils.Constants.VEHICLE_DOCUMENT
import com.helloyatri.utils.Constants.VEHICLE_PHOTO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountDocumentsFragment : BaseFragment<AccountDocumentsFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    private val driverDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val vehicleDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val driverVerificationAdapter by lazy {
        DriverVerificationAdapter()
    }

    private val driverDocumentsList = ArrayList<DriverDocuments>()
    private val vehicleDocumentsList = ArrayList<DriverDocuments>()
    private val driverVerificationDataList = ArrayList<DriverVerification>()
    var phoneNumber: String? = null

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AccountDocumentsFragmentBinding {
        return AccountDocumentsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        apiViewModel.getDriverStatus()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
        initObservers()
    }

    private fun initObservers() {
        apiViewModel.getDriverStatus.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {

                        hideLoader()
                        it.data?.let { it ->
                            val response =
                                Gson().fromJson(it, DriverStatusResponse::class.java)
                            when(response.code) {
                                200 -> {
                                    // nothing goes here
                                }
                                else -> {
                                    response?.data?.let { driverStatus ->
                                        driverStatus.addVehicle?.let {
                                            session.isAddVehicle = it.status ?: false
                                        }
                                        driverStatus.profileImage?.let {
                                            session.isProfilePictureAdded = it.status ?: false
                                        }
                                        driverStatus.profileInfo?.let {
                                            session.isPersonalDetailsAdded = it.status ?: false
                                        }
                                        driverStatus.requiredDocuments?.let {
                                            session.isRequiredDocumentsAdded = it.status ?: false
                                        }
                                        driverStatus.vehicleDocuments?.let {
                                            session.isVehicleDocumentsAdded = it.status ?: false
                                        }
                                        driverStatus.vehicleImages?.let {
                                            session.isVehiclePhotosAdded = it.status ?: false
                                        }
                                        driverStatus.verificationPending?.let {
                                            session.isDriverVerified = it.status ?: false
                                        }
                                    } ?: run {
                                        showSomethingMessage()
                                    }
                                }
                            }
                        } ?: run {
                            showSomethingMessage()
                        }
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }
                }
            }
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

        driverDocumentsAdapter.setOnItemClickPositionListener { _, position ->
            when (position) {
                0 -> {
                    navigator.load(DriverPersonalDetailsFragment::class.java).replace(true)
                }

                1 -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = UPDATE_PROFILE_PICTURE))
                        .replace(true)
                }

                2 -> {
                    navigator.load(DriverRequiredDocumentsFragment::class.java)
                        .setBundle(DriverRequiredDocumentsFragment.createBundle(statusCode = DRIVER_REQUIRED_DOCUMENT))
                        .replace(true)
                }
            }
        }

        vehicleDocumentsAdapter.setOnItemClickPositionListener { _, position ->
            when (position) {
                0 -> {
                    navigator.load(DriverVehicleDetailsFragment::class.java).replace(true)
                }

                1 -> {
                    navigator.load(DriverRequiredDocumentsFragment::class.java)
                        .setBundle(DriverRequiredDocumentsFragment.createBundle(statusCode = VEHICLE_DOCUMENT))
                        .replace(true)
//                    navigator.load(DriverVehicleDocumentsFragment::class.java).replace(true)
                }

                2 -> {
                    navigator.load(DriverRequiredDocumentsFragment::class.java)
                        .setBundle(DriverRequiredDocumentsFragment.createBundle(statusCode = VEHICLE_PHOTO))
                        .replace(true)
                    //  navigator.load(DriverVehiclePhotosFragment::class.java).replace(true)
                }
            }
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        // Set the data to the phone number
        intent.data = Uri.parse("tel:$phoneNumber")
        // Start the activity with the intent
        startActivity(intent)
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

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverDetails.apply {
            adapter = driverDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        recyclerViewVehicleDetails.apply {
            adapter = vehicleDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        recyclerViewDriverVerification.apply {
            adapter = driverVerificationAdapter
        }
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

            //            binding.buttonYouVerified.text = session.verificationDetails?.btn_lbl
//                ?: if (session.isDriverVerified) getString(R.string.btn_you_are_verified_login) else getString(
//                    R.string.btn_your_verification_pending
//                )

        } else {
            setUpDefaultContactData()
            setUpDefaultLocationData()
        }
        driverVerificationAdapter.setItems(driverVerificationDataList, 1)


        driverDocumentsList.clear()
        driverDocumentsList.add(
            DriverDocuments(
                documentName = getString(R.string.label_personal_details),
                isDataAdded = session.isPersonalDetailsAdded
            )
        )
        driverDocumentsList.add(
            DriverDocuments(
                documentName = getString(R.string.label_profile_picture),
                isDataAdded = session.isProfilePictureAdded
            )
        )
        driverDocumentsList.add(
            DriverDocuments(
                documentName = getString(R.string.label_required_documents),
                isDataAdded = session.isRequiredDocumentsAdded
            )
        )

        vehicleDocumentsList.clear()
        vehicleDocumentsList.add(
            DriverDocuments(
                documentName = getString(R.string.label_add_a_vehicle),
                isDataAdded = session.isAddVehicle
            )
        )
        vehicleDocumentsList.add(
            DriverDocuments(
                documentName = getString(R.string.label_vehicle_documents),
                isDataAdded = session.isVehicleDocumentsAdded
            )
        )
        vehicleDocumentsList.add(
            DriverDocuments(
                documentName = getString(R.string.label_vehicle_photos),
                isDataAdded = session.isVehiclePhotosAdded
            )
        )
//        driverVerificationDataList.clear()
//        driverVerificationDataList.add(DriverVerification(image = R.drawable.image_help_desk,
//                text = "+18 98765 43210 \n+91 98765 43210", button = "Call agency"))
//        driverVerificationDataList.add(DriverVerification(image = R.drawable.image_navigate_there,
//                text = "Cinemax, Rectory Cottage, Court Road", button = "Navigate there"))

        driverDocumentsAdapter.setItems(driverDocumentsList, 1)
        vehicleDocumentsAdapter.setItems(vehicleDocumentsList, 1)
        //  driverVerificationAdapter.setItems(driverVerificationDataList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarTitle("Documents").build()
    }

}