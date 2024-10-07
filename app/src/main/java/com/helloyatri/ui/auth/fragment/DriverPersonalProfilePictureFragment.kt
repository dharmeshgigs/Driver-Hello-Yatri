package com.helloyatri.ui.auth.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.DriverProfilePictureImages
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.data.model.GetAllRequiredDocument
import com.helloyatri.data.model.UploadDocumentModel
import com.helloyatri.data.request.DriverProfilePictureDetails
import com.helloyatri.databinding.AuthDriverPersonalProfilePictureFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.auth.adapter.DriverProfilePictureDetailsAdapter
import com.helloyatri.ui.auth.adapter.DriverProfilePictureImagesAdapter
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants
import com.helloyatri.utils.Constants.DRIVER_DOC
import com.helloyatri.utils.Constants.UPDATE_PROFILE_PICTURE
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
import com.helloyatri.utils.Constants.VEHICLE_DOC
import com.helloyatri.utils.Constants.VEHICLE_IMAGE
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.fileselector.FileType
import com.helloyatri.utils.fileselector.MediaSelectHelper
import com.helloyatri.utils.fileselector.MediaSelector
import com.helloyatri.utils.fileselector.OutPutFileAny
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class DriverPersonalProfilePictureFragment :
    BaseFragment<AuthDriverPersonalProfilePictureFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()
    @Inject
    lateinit var mediaSelectHelper: MediaSelectHelper
    private val driverProfilePictureDetailsAdapter by lazy {
        DriverProfilePictureDetailsAdapter()
    }
    private val driverProfilePictureImagesAdapter by lazy {
        DriverProfilePictureImagesAdapter()
    }
    var getuploadedDriverArrayList: ArrayList<String> = arrayListOf()
    var isUploadedVehicleRegistration = false
    var isUploadedBankDetails = false
    var isUploadedVehiclePhoto = false
    private val driverProfilePictureDetailsList = ArrayList<DriverProfilePictureDetails>()
    var statusCode: String? = null
    private var document_id: String? = null
    var id: String? = null
    private var rejectionReason: String? = null
    private var reasonLable: Boolean? = false

    companion object {
        fun createBundle(
            statusCode: String? = null,
            document_id: String? = null
        ) = bundleOf(
            "statusCode" to statusCode,
            "document_id" to document_id
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideLoader()
        statusCode = arguments?.getString("statusCode")
        document_id = arguments?.getString("document_id")
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthDriverPersonalProfilePictureFragmentBinding {
        return AuthDriverPersonalProfilePictureFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpClickListener()
        setUpImages()
        getData()
        initObservers()
    }

    private fun initObservers() {

        apiViewModel.updateProfileImageLiveData.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                        resource.data?.let {
                            val response =
                                Gson().fromJson(it.toString(), DriverResponse::class.java)
                            response?.data?.let {
                                showMessage(response.message ?: "")
                                navigator.goBack()
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

        apiViewModel.getDriverProfileLiveData.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                        val response =
                            Gson().fromJson(it.data.toString(), DriverResponse::class.java)
                        response.data.let {
                            driverProfilePictureImagesAdapter.clearAllItem()
                            if (it?.profileImage !=null) {
                                driverProfilePictureImagesAdapter.addItem(
                                    DriverProfilePictureImages(
                                        images = it.profileImage.toString(),
                                        local = false
                                    )
                                )
                            }
                        }
                        driverProfilePictureImagesAdapter.notifyDataSetChanged()

                        updateCount()
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

        apiViewModel.uploadDocumentLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource.data?.let {
                        val response =
                            Gson().fromJson(
                                resource.data.toString(),
                                UploadDocumentModel::class.java
                            )
                        showMessage(response.message ?: "")
                        if (statusCode == UPLOAD_REGISTRATION_CERTIFICATION || statusCode == UPLOAD_VEHICLE_PERMIT) {
                            isUploadedVehicleRegistration = true
                            if (isUploadedVehicleRegistration) {
                                apiViewModel.getVehicleDocument()
                            }
                        } else if (statusCode == UPLOAD_CHASIS_NUMBER_IMAGES) {
                            isUploadedVehiclePhoto = true
                            if (isUploadedVehiclePhoto) {
                                apiViewModel.getVehiclePhotos()
                            }
                        } else if (statusCode == UPLOAD_BANK_DETAILS) {
                            isUploadedBankDetails = true
                            if (isUploadedBankDetails) {
                                getData()
                            }
                        }
                        else {
                            navigator.goBack()
                        }
                    } ?: run {
                        showSomethingMessage()
                    }
                }

                Status.ERROR -> {
                    hideLoader()
                    val error =
                        resource.message ?: getString(resource.resId!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> hideLoader()
            }
        }

        apiViewModel.getVehicleDocumentLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        if (isUploadedVehicleRegistration) {
                            for (item in response.data) {
                                if (item.id.toString() == document_id) {
                                    id = item.uploaded_vehicle_document?.id.toString()
                                    if (item.uploaded_vehicle_document?.uploadedArray?.size!! > 1) {
                                        apiViewModel.removeSpecificDocument(
                                            Request(
                                                id = id,
                                                document = item.uploaded_vehicle_document?.uploadedArray?.get(
                                                    item.uploaded_vehicle_document?.uploadedArray!!.lastIndex
                                                )
                                            )
                                        )
                                    } else {
                                        isUploadedVehicleRegistration = false
                                        navigator.goBack()
                                    }
                                }
                            }
                        } else {
                            response?.data?.let {
                                getuploadedDriverArrayList.clear()
                                for (item in response.data) {
                                    if (item.id.toString() == document_id) {
                                        id = item.uploaded_vehicle_document?.id.toString()
                                        item.uploaded_vehicle_document?.uploadedArray?.let { it1 ->
                                            getuploadedDriverArrayList.addAll(
                                                it1
                                            )
                                        }
                                        if (item.uploaded_vehicle_document?.status == 2) {
                                            reasonLable = true
                                            rejectionReason =
                                                item.uploaded_vehicle_document?.rejectionReason
                                                    ?: ""
                                        }
                                    }
                                }
                                driverProfilePictureImagesAdapter.clearAllItem()
                                for (item in getuploadedDriverArrayList) {
                                    driverProfilePictureImagesAdapter.addItem(
                                        DriverProfilePictureImages(
                                            images = item,
                                            local = false,
                                            id = id
                                        )
                                    )
                                }
                                updateCount()
                            }
//                            setUpData()
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
                        resource.message ?: getString(resource.resId!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> showLoader()
            }
        }

        apiViewModel.getVehiclePhotosLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        if (isUploadedVehiclePhoto) {
                            for (item in response.data) {
                                if (item.id.toString() == document_id) {
                                    id = item.uploaded_vehicle_document?.id.toString()
                                    if (item.uploaded_vehicle_document?.uploadedArray?.size!! > 1) {
                                        apiViewModel.removeSpecificDocument(
                                            Request(
                                                id = id,
                                                document = item.uploaded_vehicle_document?.uploadedArray?.get(
                                                    item.uploaded_vehicle_document?.uploadedArray!!.lastIndex
                                                )
                                            )
                                        )
                                    } else {
                                        isUploadedVehiclePhoto = false
                                        navigator.goBack()
                                    }
                                }
                            }
                        } else {
                            response?.data?.let {
                                getuploadedDriverArrayList.clear()
                                for (item in response.data!!) {
                                    if (item.id.toString() == document_id) {
                                        id = item.uploaded_vehicle_document?.id.toString()
                                        item.uploaded_vehicle_document?.uploadedArray?.let { it1 ->
                                            getuploadedDriverArrayList.addAll(
                                                it1
                                            )
                                        }
                                        if (item.uploaded_vehicle_document?.status == 2) {
                                            reasonLable = true
                                            rejectionReason =
                                                item.uploaded_vehicle_document?.rejectionReason
                                                    ?: ""
                                        }
                                    }
                                }
                                driverProfilePictureImagesAdapter.clearAllItem()
                                for (item in getuploadedDriverArrayList) {
                                    driverProfilePictureImagesAdapter.addItem(
                                        DriverProfilePictureImages(
                                            images = item,
                                            local = false,
                                            id = id
                                        )
                                    )
                                }
                                updateCount()
                            }
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
                        resource.message ?: getString(resource.resId!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> showLoader()
            }
        }

        apiViewModel.getRequiredAllDocumentLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        if (isUploadedBankDetails) {
                            for (item in response.data) {
                                if (item.id.toString() == document_id) {
                                    id = item.uploadedDriverDocument?.id.toString()
                                    if (item.uploadedDriverDocument?.uploadedArray?.size!! > 1) {
                                        apiViewModel.removeSpecificDocument(
                                            Request(
                                                id = id,
                                                document = item.uploadedDriverDocument?.uploadedArray?.get(
                                                    item.uploadedDriverDocument?.uploadedArray!!.lastIndex
                                                )
                                            )
                                        )
                                    } else {
                                        isUploadedBankDetails = false
                                        navigator.goBack()
                                    }
                                }
                            }
                        } else {
                            response?.data?.let {
                                getuploadedDriverArrayList.clear()
                                for (item in response.data) {
                                    if (item.id.toString() == document_id) {
                                        id = item.uploadedDriverDocument?.id.toString()
                                        item.uploadedDriverDocument?.uploadedArray?.let { it1 ->
                                            getuploadedDriverArrayList.addAll(
                                                it1
                                            )
                                        }
                                        if (item.uploadedDriverDocument?.status == 2) {
                                            reasonLable = true
                                            rejectionReason =
                                                item.uploadedDriverDocument?.rejectionReason ?: ""
                                        }
                                    }
                                }

                                driverProfilePictureImagesAdapter.clearAllItem()
                                for (item in getuploadedDriverArrayList) {
                                    driverProfilePictureImagesAdapter.addItem(
                                        DriverProfilePictureImages(
                                            images = item,
                                            local = false,
                                            id = id
                                        )
                                    )
                                }

                                updateCount()
//                            setUpData()
                            }
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
                        resource.message ?: getString(resource.resId!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> showLoader()
            }
        }

        apiViewModel.removeSpecificDocumentLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    when (statusCode) {
                        UPLOAD_DRIVING_LICENCE, UPLOAD_GOVERNMENT_ID -> {
                            getData()
                        }

                        UPLOAD_BANK_DETAILS ->{
                            navigator.goBack()
                        }

                        UPLOAD_REGISTRATION_CERTIFICATION -> {
                            navigator.goBack()
                        }

                        UPLOAD_VEHICLE_PERMIT -> {
                            navigator.goBack()
                        }

                        UPLOAD_VEHICLE_PUC, UPLOAD_VEHICLE_INSURANCE -> {
                            getData()
                        }

                        UPLOAD_CHASIS_NUMBER_IMAGES -> {
                            navigator.goBack()
                        }

                        UPLOAD_FRONTBACK_WITH_NUMBER_PLATE, UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR,UPLOAD_YOUR_PHOTO_WITH_VEHICLE -> {
                            getData()
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoader()
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> hideLoader()
            }

        }

        apiViewModel.deleteUserImageLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    getData()
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

    private fun getData() {
        when (statusCode) {
            UPDATE_PROFILE_PICTURE -> {
                apiViewModel.getDriverProfile()
            }

            UPLOAD_DRIVING_LICENCE, UPLOAD_GOVERNMENT_ID, UPLOAD_BANK_DETAILS -> {
                apiViewModel.getAllRequiredDocument()
            }

            UPLOAD_VEHICLE_PUC, UPLOAD_VEHICLE_INSURANCE, UPLOAD_REGISTRATION_CERTIFICATION, UPLOAD_VEHICLE_PERMIT -> {
                apiViewModel.getVehicleDocument()
            }

            UPLOAD_FRONTBACK_WITH_NUMBER_PLATE, UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR, UPLOAD_CHASIS_NUMBER_IMAGES, UPLOAD_YOUR_PHOTO_WITH_VEHICLE -> {
                apiViewModel.getVehiclePhotos()
            }
        }
    }

    private fun setUpText() = with(binding) {
        when (statusCode) {
            UPDATE_PROFILE_PICTURE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_profile_picture)
                textViewProfilePicture.text = getString(R.string.label_profile_picture)
                setUpData(resources.getStringArray(R.array.profile_image))
            }

            UPLOAD_DRIVING_LICENCE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_driving_license)
                textViewProfilePicture.text = getString(R.string.label_driving_license)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
                textViewNote.visibility = View.VISIBLE
            }

            UPLOAD_GOVERNMENT_ID -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_government_id)
                textViewProfilePicture.text = getString(R.string.label_government_id)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
                textViewNote.text =
                    getString(R.string.label_note_please_upload_both_sides_of_government_id)
                textViewNote.visibility = View.VISIBLE
            }

            UPLOAD_BANK_DETAILS -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text = getString(R.string.label_bank_details)
                textViewProfilePicture.text = getString(R.string.label_attach_bank_account_details)
                setUpData(resources.getStringArray(R.array.all_bank_common_lablel))
            }

            UPLOAD_VEHICLE_PUC -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text = getString(R.string.label_vehicle_puc)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
            }

            UPLOAD_VEHICLE_INSURANCE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text = getString(R.string.label_vehicle_insurance)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
            }

            UPLOAD_REGISTRATION_CERTIFICATION -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text =
                    getString(R.string.label_vehicle_registration_certificate)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
            }

            UPLOAD_VEHICLE_PERMIT -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text = getString(R.string.label_vehicle_permit)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
            }

            UPLOAD_FRONTBACK_WITH_NUMBER_PLATE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_front_back_with_number_plate)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
                textViewNote.text =
                    getString(R.string.label_note_please_upload_both_sides_front_and_back)
                textViewNote.visibility = View.VISIBLE
            }

            UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_left_right_side_exterior)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
                textViewNote.text =
                    getString(R.string.label_note_please_upload_both_sides_exterior_left_right_side)
                textViewNote.visibility = View.VISIBLE
            }

            UPLOAD_CHASIS_NUMBER_IMAGES -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_chassis_number_images)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
            }

            UPLOAD_YOUR_PHOTO_WITH_VEHICLE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_your_photo_with_vehicle)
                setUpData(resources.getStringArray(R.array.all_common_lablel))
                textViewNote.text =
                    getString(R.string.label_note_please_upload_your_photo_by_standing_in_front_of_your_vehicle)
                textViewNote.visibility = View.VISIBLE
            }
        }
        includedTopContent.textViewYouHaveMissed.text = getString(
            R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it
        )
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDetails.apply {
            adapter = driverProfilePictureDetailsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        recyclerViewImages.apply {
            adapter = driverProfilePictureImagesAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    private fun setUpClickListener() = with(binding) {
        constraintUploadDocuments.setOnClickListener {
            mediaSelectHelper.selectOptionsForImagePicker(false)
        }

        driverProfilePictureImagesAdapter.setOnItemClickListener {
            if (it.local) {
                driverProfilePictureImagesAdapter.removeItem(it)
            } else {
                when (statusCode) {
                    UPDATE_PROFILE_PICTURE -> {
                        apiViewModel.deleteUserImage()
                    }

                    UPLOAD_DRIVING_LICENCE, UPLOAD_GOVERNMENT_ID, UPLOAD_BANK_DETAILS -> {
                        apiViewModel.removeSpecificDocument(
                            Request(
                                id = it.id,
                                document = it.images
                            )
                        )
                    }

                    UPLOAD_VEHICLE_PUC, UPLOAD_VEHICLE_INSURANCE, UPLOAD_REGISTRATION_CERTIFICATION, UPLOAD_VEHICLE_PERMIT -> {
                        apiViewModel.removeSpecificDocument(
                            Request(
                                id = it.id,
                                document = it.images
                            )
                        )
                    }

                    UPLOAD_FRONTBACK_WITH_NUMBER_PLATE, UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR, UPLOAD_CHASIS_NUMBER_IMAGES, UPLOAD_YOUR_PHOTO_WITH_VEHICLE -> {
                        apiViewModel.removeSpecificDocument(
                            Request(
                                id = it.id,
                                document = it.images
                            )
                        )
                    }
                }
            }
            updateCount()
        }

        buttonSave.setOnClickListener {
            showLoader()
            when (statusCode) {
                UPDATE_PROFILE_PICTURE -> {
                    driverProfilePictureImagesAdapter.items?.filter {
                        it.local
                    }?.let {
                        if (it.isNotEmpty()) {
                            val multiPartBodyBuilder = MultipartBody.Builder()
                            multiPartBodyBuilder.setType(MultipartBody.FORM)
                            multiPartBodyBuilder.addFormDataPart(
                                Constants.PROFILE_IMAGE,
                                Uri.parse(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)?.lastPathSegment,
                                RequestBody.create(
                                    "image/*".toMediaTypeOrNull(), File(
                                        driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images
                                    )
                                )
                            )
                            apiViewModel.updateProfileImage(multiPartBodyBuilder.build())
                        } else {
                            hideLoader()
                            navigator.goBack()
                        }
                    }
        //                if (driverProfilePictureImagesAdapter.items?.get(0)?.local == true) {
        //                    val requestBody: RequestBody = MultipartBody.Builder()
        //                        .setType(MultipartBody.FORM)
        //                        .addFormDataPart(
        //                            "profile_image",
        //                            Uri.parse(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)?.lastPathSegment
        //                                ?: "",
        //                            RequestBody.create(
        //                                "image/*".toMediaTypeOrNull(),
        //                                File(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)
        //                            )
        //                        )
        //                        .build()
        //                    apiViewModel.updateProfileImage(requestBody)
        //                } else {
        //                    hideLoader()
        //                    navigator.goBack()
        //                }
                }
                UPLOAD_DRIVING_LICENCE, UPLOAD_GOVERNMENT_ID -> {
                    driverProfilePictureImagesAdapter.items?.filter {
                        it.local
                    }?.let {
                        if (it.isNotEmpty()) {
                            val multiPartBodyBuilder = MultipartBody.Builder()
                            multiPartBodyBuilder.setType(MultipartBody.FORM);
                            multiPartBodyBuilder.addFormDataPart("document_id", document_id ?: "")
                            multiPartBodyBuilder.addFormDataPart("type", DRIVER_DOC)
                            it.forEach {
                                val file = it.images?.let { it1 -> File(it1) }
                                multiPartBodyBuilder.addFormDataPart(
                                    Constants.Documents, file?.getName(), RequestBody.create(
                                        "image/*".toMediaTypeOrNull(), file!!
                                    )
                                )
                            }
                            apiViewModel.updateDocument(multiPartBodyBuilder.build())
                        } else {
                            hideLoader()
                            navigator.goBack()
                        }
                    } ?: run {
                        hideLoader()
                        navigator.goBack()
                    }

                }
                UPLOAD_BANK_DETAILS -> {
                    driverProfilePictureImagesAdapter.items?.filter {
                        it.local
                    }?.let {
                        if (it.isNotEmpty()) {
                            val multiPartBodyBuilder = MultipartBody.Builder()
                            multiPartBodyBuilder.setType(MultipartBody.FORM);
                            multiPartBodyBuilder.addFormDataPart("document_id", document_id ?: "")
                            multiPartBodyBuilder.addFormDataPart("type", DRIVER_DOC)
                            multiPartBodyBuilder.addFormDataPart(
                                Constants.Documents,
                                Uri.parse(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)?.lastPathSegment,
                                RequestBody.create(
                                    "image/*".toMediaTypeOrNull(), File(
                                        driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images
                                    )
                                )
                            )
                            apiViewModel.updateDocument(multiPartBodyBuilder.build())
                        } else {
                            hideLoader()
                            navigator.goBack()
                        }
                    } ?: run {
                        hideLoader()
                        navigator.goBack()
                    }
                }
                UPLOAD_REGISTRATION_CERTIFICATION, UPLOAD_VEHICLE_PERMIT -> {
                    driverProfilePictureImagesAdapter.items?.filter {
                        it.local
                    }?.let {
                        if (it.isNotEmpty()) {
                            val multiPartBodyBuilder = MultipartBody.Builder()
                            multiPartBodyBuilder.setType(MultipartBody.FORM);
                            multiPartBodyBuilder.addFormDataPart("document_id", document_id ?: "")
                            multiPartBodyBuilder.addFormDataPart("type", VEHICLE_DOC)
                            multiPartBodyBuilder.addFormDataPart(
                                Constants.Documents,
                                Uri.parse(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)?.lastPathSegment,
                                RequestBody.create(
                                    "image/*".toMediaTypeOrNull(), File(
                                        driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images
                                    )
                                )
                            )
                            apiViewModel.updateDocument(multiPartBodyBuilder.build())
                        } else {
                            hideLoader()
                            navigator.goBack()
                        }
                    } ?: run {
                        hideLoader()
                        navigator.goBack()
                    }

                }
                UPLOAD_VEHICLE_PUC, UPLOAD_VEHICLE_INSURANCE -> {
                    driverProfilePictureImagesAdapter.items?.filter {
                        it.local
                    }?.let { it ->
                        if (it.isNotEmpty()) {
                            val multiPartBodyBuilder = MultipartBody.Builder()
                            multiPartBodyBuilder.setType(MultipartBody.FORM);
                            multiPartBodyBuilder.addFormDataPart("document_id", document_id ?: "")
                            multiPartBodyBuilder.addFormDataPart("type", VEHICLE_DOC)
                            it.forEach {
                                val file = it.images?.let { it1 -> File(it1) }
                                multiPartBodyBuilder.addFormDataPart(
                                    Constants.Documents, file?.getName(), RequestBody.create(
                                        "image/*".toMediaTypeOrNull(), file!!
                                    )
                                )
                            }
                            apiViewModel.updateDocument(multiPartBodyBuilder.build())
                        } else {
                            hideLoader()
                            navigator.goBack()
                        }
                    } ?: run {
                        hideLoader()
                        navigator.goBack()
                    }

                }
                UPLOAD_CHASIS_NUMBER_IMAGES -> {
                    driverProfilePictureImagesAdapter.items?.filter {
                        it.local
                    }?.let {
                        if (it.isNotEmpty()) {
                            val multiPartBodyBuilder = MultipartBody.Builder()
                            multiPartBodyBuilder.setType(MultipartBody.FORM)
                            multiPartBodyBuilder.addFormDataPart("document_id", document_id ?: "")
                            multiPartBodyBuilder.addFormDataPart("type", VEHICLE_IMAGE)
                            multiPartBodyBuilder.addFormDataPart(
                                Constants.Documents,
                                Uri.parse(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)?.lastPathSegment,
                                RequestBody.create(
                                    "image/*".toMediaTypeOrNull(), File(
                                        driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images
                                    )
                                )
                            )
                            apiViewModel.updateDocument(multiPartBodyBuilder.build())
                        } else {
                            hideLoader()
                            navigator.goBack()
                        }
                    } ?: run {
                        hideLoader()
                        navigator.goBack()
                    }
                }
                UPLOAD_FRONTBACK_WITH_NUMBER_PLATE, UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR, UPLOAD_YOUR_PHOTO_WITH_VEHICLE -> {
                    driverProfilePictureImagesAdapter.items?.filter {
                        it.local
                    }?.let {
                        if (it.isNotEmpty()) {
                            val multiPartBodyBuilder = MultipartBody.Builder()
                            multiPartBodyBuilder.setType(MultipartBody.FORM);
                            multiPartBodyBuilder.addFormDataPart("document_id", document_id ?: "")
                            multiPartBodyBuilder.addFormDataPart("type", VEHICLE_IMAGE)
                            it.forEach {
                                val file = it.images?.let { it1 -> File(it1) }
                                multiPartBodyBuilder.addFormDataPart(
                                    Constants.Documents, file?.getName(), RequestBody.create(
                                        "image/*".toMediaTypeOrNull(), file!!
                                    )
                                );
                            }
                            apiViewModel.updateDocument(multiPartBodyBuilder.build())
                        } else {
                            hideLoader()
                            navigator.goBack()
                        }
                    } ?: run {
                        hideLoader()
                        navigator.goBack()
                    }

                }
            }
            //session.isProfilePictureAdded = true
            //navigator.goBack()
        }
    }

    private fun setUpImages() {
//        mediaSelectHelper.canSelectMultipleImages(false)
        mediaSelectHelper.registerCallback(object : MediaSelector {
            override fun onImageUri(uri: Uri) {
                super.onImageUri(uri)
                when (statusCode) {
                    UPDATE_PROFILE_PICTURE -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.clearAllItem()
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = uri.path, local = true)
                        )
                    }

                    UPLOAD_DRIVING_LICENCE, UPLOAD_GOVERNMENT_ID -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = uri.path, local = true)
                        )
                    }

                    UPLOAD_BANK_DETAILS ->{
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.clearAllItem()
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = uri.path, local = true)
                        )
                    }

                    UPLOAD_REGISTRATION_CERTIFICATION, UPLOAD_VEHICLE_PERMIT -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.clearAllItem()
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = uri.path, local = true)
                        )
                    }

                    UPLOAD_VEHICLE_PUC, UPLOAD_VEHICLE_INSURANCE -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = uri.path, local = true)
                        )
                    }

                    UPLOAD_CHASIS_NUMBER_IMAGES, UPLOAD_YOUR_PHOTO_WITH_VEHICLE -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.clearAllItem()
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = uri.path, local = true)
                        )
                    }

                    UPLOAD_FRONTBACK_WITH_NUMBER_PLATE, UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = uri.path, local = true)
                        )
                    }


//                    UPLOAD_BANK_DETAILS -> {
//                        driverProfilePictureImagesAdapter.isBitMap = false
//
//                     //   driverProfilePictureImagesAdapter.clearAllItem()
//                        driverProfilePictureImagesAdapter.addItem(
//                            DriverProfilePictureImages(images = uri.path, local = true)
//                        )
//                    }
                }

                updateCount()
            }

            override fun onAnyFileSelected(outPutFileAny: OutPutFileAny) {
                binding.recyclerViewImages.show()
                when (outPutFileAny.type) {
                    FileType.Image -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = outPutFileAny.uri.path)
                        )
                        updateCount()
                    }

                    FileType.Pdf -> {
                        requireActivity().contentResolver.openFileDescriptor(outPutFileAny.uri, "r")
                            ?.use { p ->
                                val pdfRenderer = PdfRenderer(p).openPage(0)
                                val bitmap = Bitmap.createBitmap(
                                    pdfRenderer.width,
                                    pdfRenderer.height, Bitmap.Config.ARGB_8888
                                )

                                val canvas = Canvas(bitmap)
                                canvas.drawColor(Color.WHITE)

                                pdfRenderer.render(
                                    bitmap, null, null,
                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                )
                                pdfRenderer.close()
                                driverProfilePictureImagesAdapter.isBitMap = true
                                driverProfilePictureImagesAdapter.addItem(
                                    DriverProfilePictureImages(
                                        images = outPutFileAny.uri.path,
                                        imageBitmap = bitmap
                                    )
                                )
                            }
                        updateCount()
                    }

                    else -> {
                        showMessage(
                            getString(R.string.validation_only_pdf_jpeg_png_files_are_allowed)
                        )
                    }
                }
            }
        })
    }

    private fun setUpData(data: Array<String>) {
        driverProfilePictureDetailsList.clear()
        data.forEach {
            driverProfilePictureDetailsList.add(
                DriverProfilePictureDetails(
                    text = it
                )
            )
        }
        driverProfilePictureDetailsAdapter.setItems(driverProfilePictureDetailsList, 1)
    }

    private fun updateCount() = with(binding) {
        if (driverProfilePictureImagesAdapter.items?.isEmpty()?.not() == true) {
            recyclerViewImages.show()
            textViewUploadDocuments.text = getString(
                R.string.label_s_documents_uploaded,
                driverProfilePictureImagesAdapter.itemCount.toString()
            )
            buttonSave.isClickable = true
            buttonSave.isEnabled = true
            buttonSave.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            if (reasonLable == true) {
                binding.textViewLabelReason.text = rejectionReason ?: ""
                binding.textViewLabelReason.visibility = View.VISIBLE
            } else {
                binding.textViewLabelReason.visibility = View.GONE
            }
        } else {
            textViewUploadDocuments.text = getString(R.string.label_upload_documents)
            recyclerViewImages.hide()
            buttonSave.isClickable = false
            buttonSave.isEnabled = false
            buttonSave.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grey)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}