package com.helloyatri.ui.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.data.model.DriverStatusResponse
import com.helloyatri.databinding.AuthDriverDocumentsFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.activity.DriverDocumentsActivity
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.utils.Constants
import com.helloyatri.utils.Constants.DRIVER_REQUIRED_DOCUMENT
import com.helloyatri.utils.Constants.UPDATE_PROFILE_PICTURE
import com.helloyatri.utils.Constants.VEHICLE_DOCUMENT
import com.helloyatri.utils.Constants.VEHICLE_PHOTO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDocumentsFragment : BaseFragment<AuthDriverDocumentsFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()

    private val driverDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val vehicleDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val driverDocumentsList = ArrayList<DriverDocuments>()
    private val vehicleDocumentsList = ArrayList<DriverDocuments>()

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthDriverDocumentsFragmentBinding {
        return AuthDriverDocumentsFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG", "onCreate: ")

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

//        apiViewModel.getDriverStatus.get(this, {
//            hideLoader()
//            when(it.code) {
//                APIFactory.ResponseCode.SUCCESS -> {
//                    driverDocumentsList.clear()
//                    driverDocumentsList.add(
//                        DriverDocuments(documentName = getString(R.string.label_personal_details),
//                            isDataAdded = it.data?.profileInfo?.status?:false))
//                    driverDocumentsList.add(
//                        DriverDocuments(documentName = getString(R.string.label_profile_picture),
//                            isDataAdded = it.data?.profileImage?.status?:false))
//                    driverDocumentsList.add(
//                        DriverDocuments(documentName = getString(R.string.label_required_documents),
//                            isDataAdded = it.data?.requiredDocuments?.status?:false))
//
//                    vehicleDocumentsList.clear()
//                    vehicleDocumentsList.add(
//                        DriverDocuments(documentName = getString(R.string.label_add_a_vehicle),
//                            isDataAdded = it.data?.addVehicle?.status?:false))
//                    vehicleDocumentsList.add(
//                        DriverDocuments(documentName = getString(R.string.label_vehicle_documents),
//                            isDataAdded = it.data?.vehicleDocuments?.status?:false))
//                    vehicleDocumentsList.add(
//                        DriverDocuments(documentName = getString(R.string.label_vehicle_photos),
//                            isDataAdded = it.data?.vehicleImages?.status?:false))
//
//                    driverDocumentsAdapter.setItems(driverDocumentsList, 1)
//                    vehicleDocumentsAdapter.setItems(vehicleDocumentsList, 1)
//
//                    if ((it.data?.profileInfo?.status == true) && (it.data?.profileImage?.status == true) && (it.data?.requiredDocuments?.status == true) && (it.data?.addVehicle?.status == true) && (it.data?.vehicleDocuments?.status == true) && (it.data?.vehicleImages?.status == true)) {
//                        binding.buttonSubmit.isClickable = true
//                        binding.buttonSubmit.isEnabled = true
//                        binding.buttonSubmit.backgroundTintList =
//                            ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
//                    } else {
//                        binding.buttonSubmit.isClickable = false
//                        binding.buttonSubmit.isEnabled = false
//                        binding.buttonSubmit.backgroundTintList =
//                            ContextCompat.getColorStateList(requireContext(), R.color.grey)
//                    }
//                }
//
//                else -> {
//
//                }
//            }
//        })
        initObservers()
    }

    override fun bindData() {
        apiViewModel.getDriverStatus()
        setUpText()
        setUpRecyclerView()
        setUpClickListener()
        initObservers()
        Log.i("TAG", "bindData: ")

    }

    private fun initObservers() {
        apiViewModel.getDriverStatus.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        Log.i("TAG", "initObservers: "+it.message)
                        hideLoader()
                        it.data?.let { it ->
                            val response =
                                Gson().fromJson(it, DriverStatusResponse::class.java)
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
                                setUpMenu()
                            } ?: run {
                                showSomethingMessage()
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

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_hello)
        includedTopContent.textViewWelcomeBack.text = String.format(
            getString(R.string.dummy_welcome_rahul),
            session.user?.name?.split(" ")?.get(0) ?: ""
        )
        includedTopContent.textViewYouHaveMissed.text =
            getString(R.string.label_please_complete_all_steps)
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

        buttonSubmit.setOnClickListener {
//            session.isDriverVerified = true
//            session.isLoggedIn = true
//            navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
            navigator.load(DriverVerificationFragment::class.java)
                .replace(false)
        }
    }

    private fun setUpClickListener() {
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

    private fun setUpData() {

    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onResume() {
        super.onResume()
        setUpMenu()
        apiViewModel.getDriverStatus()
    }

    private fun setUpMenu() {
        if (driverDocumentsList.isNotEmpty()) {
            driverDocumentsAdapter.setItems(driverDocumentsList, 1)
        }
        if (vehicleDocumentsList.isNotEmpty()) {
            vehicleDocumentsAdapter.setItems(vehicleDocumentsList, 1)
        }
        if (session.isAllDocumentUploaded()) {
            binding.buttonSubmit.isClickable = true
            binding.buttonSubmit.isEnabled = true
            binding.buttonSubmit.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        } else {
            binding.buttonSubmit.isClickable = false
            binding.buttonSubmit.isEnabled = false
            binding.buttonSubmit.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grey)
        }
    }
}