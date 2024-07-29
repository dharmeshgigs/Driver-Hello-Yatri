package com.helloyatri.ui.auth.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.network.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.DataDocument
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.data.model.GetAllRequiredDocument
import com.helloyatri.databinding.AuthDriverRequiredDocumentsFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.auth.adapter.CommonAdapter
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants.BANK_DETAILS
import com.helloyatri.utils.Constants.CHASSIS_NUMBER_IMAGES
import com.helloyatri.utils.Constants.DRIVER_REQUIRED_DOCUMENT
import com.helloyatri.utils.Constants.DRIVING_LICENCE
import com.helloyatri.utils.Constants.FRONT_BACK_WITH_NUMBER_PLAGE
import com.helloyatri.utils.Constants.GOVERNMENT_ID
import com.helloyatri.utils.Constants.LEFT_RIGHT_SIDE_EXTERIOR
import com.helloyatri.utils.Constants.UPLOAD_BANK_DETAILS
import com.helloyatri.utils.Constants.UPLOAD_CHASIS_NUMBER_IMAGES
import com.helloyatri.utils.Constants.UPLOAD_DRIVING_LICENCE
import com.helloyatri.utils.Constants.UPLOAD_FRONTBACK_WITH_NUMBER_PLATE
import com.helloyatri.utils.Constants.UPLOAD_GOVERNMENT_ID
import com.helloyatri.utils.Constants.UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR
import com.helloyatri.utils.Constants.UPLOAD_REGISTRATION_CERTIFICATION
import com.helloyatri.utils.Constants.UPLOAD_VEHICLE_INSURANCE
import com.helloyatri.utils.Constants.UPLOAD_VEHICLE_PERMIT
import com.helloyatri.utils.Constants.UPLOAD_VEHICLE_PUC
import com.helloyatri.utils.Constants.UPLOAD_YOUR_PHOTO_WITH_VEHICLE
import com.helloyatri.utils.Constants.VEHICLE_DOCUMENT
import com.helloyatri.utils.Constants.VEHICLE_INSURANCE
import com.helloyatri.utils.Constants.VEHICLE_PERMIT
import com.helloyatri.utils.Constants.VEHICLE_PUC
import com.helloyatri.utils.Constants.VEHICLE_REGISTRATION_CERTIFICATE
import com.helloyatri.utils.Constants.YOUR_PHOTO_WITH_VEHICLE
import com.helloyatri.utils.extension.enableButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverRequiredDocumentsFragment : BaseFragment<AuthDriverRequiredDocumentsFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    private val driverRequiredDocumentsAdapter by lazy {
        CommonAdapter()
    }

    private val driverRequiredDocumentsList = ArrayList<DriverDocuments>()
    private val getAllVehicleDocumentMutableList: MutableList<DataDocument> = mutableListOf()


    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthDriverRequiredDocumentsFragmentBinding {
        return AuthDriverRequiredDocumentsFragmentBinding.inflate(layoutInflater)
    }

    companion object {
        fun createBundle(
            statusCode: String? = null
        ) = bundleOf(
            "statusCode" to statusCode
        )
    }

    override fun bindData() {
        initObservers()
        setUpText()
        setUpRecyclerView()
        getAllRequiredDocumentAPI()
        setUpData()
        setUpClickListener()
    }

    private fun getAllRequiredDocumentAPI() {
        if (arguments?.getString("statusCode") == DRIVER_REQUIRED_DOCUMENT) {
            apiViewModel.getAllRequiredDocument()
        } else if (arguments?.getString("statusCode") == VEHICLE_DOCUMENT) {
            apiViewModel.getVehicleDocument()
        } else {
            apiViewModel.getVehiclePhotos()
        }
    }

    private fun initObservers() {
        apiViewModel.getRequiredAllDocumentLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let { it ->
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            getAllVehicleDocumentMutableList.clear()
                            getAllVehicleDocumentMutableList.addAll(response.data)
                            var isEnable = true

                            response.data.forEach {
                                if (it.isRequired == 1 && (it.uploadedDriverDocument == null || it.uploadedDriverDocument?.uploadedArray == null
                                            || it.uploadedDriverDocument?.uploadedArray?.size == 0)
                                ) {
                                    isEnable = false
                                }
                            }
                            enableButton(isEnable)
                            setUpData()
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

                Status.LOADING -> showLoader()
            }
        }

        apiViewModel.getVehicleDocumentLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let { it ->
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            getAllVehicleDocumentMutableList.clear()
                            getAllVehicleDocumentMutableList.addAll(response.data)
                            var isEnable = true

                            response.data.forEach {
                                if (it.isRequired == 1 && (it.uploaded_vehicle_document == null || it.uploaded_vehicle_document?.uploadedArray == null
                                            || it.uploaded_vehicle_document?.uploadedArray?.size == 0)
                                ) {
                                    isEnable = false
                                }
                            }
                            enableButton(isEnable)
                            setUpData()
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

                Status.LOADING -> showLoader()
            }

        }

        apiViewModel.getVehiclePhotosLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let { it ->
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            getAllVehicleDocumentMutableList.clear()
                            getAllVehicleDocumentMutableList.addAll(response.data)
                            var isEnable = true

                            response.data.forEach {
                                if (it.isRequired == 1 && (it.uploaded_vehicle_document == null || it.uploaded_vehicle_document?.uploadedArray == null
                                            || it.uploaded_vehicle_document?.uploadedArray?.size == 0)
                                ) {
                                    isEnable = false
                                }
                            }
                            enableButton(isEnable)
                            setUpData()
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

                Status.LOADING -> showLoader()
            }
        }

    }

    private fun setUpText() = with(binding) {
        if (arguments?.getString("statusCode") == DRIVER_REQUIRED_DOCUMENT) {
            includedTopContent.textViewHello.text = getString(R.string.label_upload)
            includedTopContent.textViewWelcomeBack.text = getString(R.string.label_your_profile)
            textViewRequiredDocuments.text = getString(R.string.label_required_documents)
        } else if (arguments?.getString("statusCode") == VEHICLE_DOCUMENT) {
            includedTopContent.textViewHello.text = getString(R.string.label_complete)
            includedTopContent.textViewWelcomeBack.text =
                getString(R.string.label_vehicle_documents)
            textViewRequiredDocuments.text = getString(R.string.label_vehicle_documents)
        } else {
            includedTopContent.textViewHello.text = getString(R.string.label_complete)
            includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_photos)
            textViewRequiredDocuments.text = getString(R.string.label_vehicle_photos)
        }

        includedTopContent.textViewYouHaveMissed.text = getString(
            R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it
        )
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverRequiredDocuments.apply {
            adapter = driverRequiredDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpClickListener() {
        driverRequiredDocumentsAdapter.setOnItemClickPositionListener { _, position ->
            when (getAllVehicleDocumentMutableList[position].name) {
                DRIVING_LICENCE -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_DRIVING_LICENCE,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(false)
                }

                GOVERNMENT_ID -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_GOVERNMENT_ID,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)
                }

                BANK_DETAILS -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_BANK_DETAILS,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)
                }

                VEHICLE_PUC -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_VEHICLE_PUC,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)
                }

                VEHICLE_INSURANCE -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_VEHICLE_INSURANCE,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)
                }

                VEHICLE_REGISTRATION_CERTIFICATE -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_REGISTRATION_CERTIFICATION,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)
                }

                VEHICLE_PERMIT -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_VEHICLE_PERMIT,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)
                }

                FRONT_BACK_WITH_NUMBER_PLAGE-> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_FRONTBACK_WITH_NUMBER_PLATE,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)

                }

                LEFT_RIGHT_SIDE_EXTERIOR -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)

                }

                CHASSIS_NUMBER_IMAGES -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_CHASIS_NUMBER_IMAGES,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)

                }

                YOUR_PHOTO_WITH_VEHICLE -> {
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(
                            DriverPersonalProfilePictureFragment.createBundle(
                                statusCode = UPLOAD_YOUR_PHOTO_WITH_VEHICLE,
                                document_id = getAllVehicleDocumentMutableList[position].id.toString()
                            )
                        )
                        .replace(true)
                }

            }
//            when (position) {
//                0 -> {
//                    navigator.load(DriverRequiredDocumentLicenceFragment::class.java).replace(true)
//                }
//
//                1 -> {
//                    navigator.load(DriverRequiredDocumentGovernmentIDFragment::class.java)
//                            .replace(true)
//                }
//
//                2 -> {
//                    navigator.load(DriverRequiredDocumentBankDetailsFragment::class.java)
//                            .replace(true)
//                }
//            }
        }

        binding.buttonSave.setOnClickListener {
          //  session.isRequiredDocumentsAdded = true
            navigator.goBack()
        }
    }

    private fun setUpData() {
//        driverRequiredDocumentsList.clear()
//        driverRequiredDocumentsList.add(
//                DriverDocuments(documentName = getString(R.string.label_driving_license),
//                        isDataAdded = session.isDrivingLicenseAdded))
//        driverRequiredDocumentsList.add(
//                DriverDocuments(documentName = getString(R.string.label_government_id),
//                        isDataAdded = session.isGovernmentIDAdded))
//        driverRequiredDocumentsList.add(
//                DriverDocuments(documentName = getString(R.string.label_bank_account_details),
//                        isDataAdded = session.isBankAccountDetailsAdded))
//        driverRequiredDocumentsAdapter.setItems(driverRequiredDocumentsList, 1)
        driverRequiredDocumentsAdapter.setItems(getAllVehicleDocumentMutableList.toList(), 1)
//        if (session.isDrivingLicenseAdded && session.isGovernmentIDAdded && session.isBankAccountDetailsAdded) {
//            enableButton()
//        } else {
//            disableButton()
//        }
    }

    private fun disableButton() {
        binding.buttonSave.isClickable = false
        binding.buttonSave.isEnabled = false
        binding.buttonSave.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.grey)
    }

    private fun enableButton(isenable: Boolean) = with(binding) {
        buttonSave.apply {
            isClickable = isenable
            isEnabled = isenable
        }.enableButton(isenable)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onResume() {
        super.onResume()
        setUpData()
    }

}