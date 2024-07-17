package com.helloyatri.ui.auth.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamingyards.sms.app.utils.Status
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
import com.helloyatri.ui.auth.adapter.DriverProfilePictureDetailsAdapter
import com.helloyatri.ui.auth.adapter.DriverProfilePictureImagesAdapter
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.utils.Constants.DRIVER_DOC
import com.helloyatri.utils.Constants.PERSONAL_PROFILE_SCREEN
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

//        apiViewModel.updateProfileImageLiveData.get(this,{
//            hideLoader()
//            when(it.code) {
//                APIFactory.ResponseCode.SUCCESS -> {
//
//                    navigator.goBack()
//                }
//
//                else -> {
//
//                    showMessage(it.message)
//                }
//            }
//        })

        getProfile()
        getRequiredDriverDocumentAPI()
        initObservers()

    }

    private fun getRequiredDriverDocumentAPI() {
        apiViewModel.getAllRequiredDocument()
    }

    private fun initObservers() {
        apiViewModel.updateProfileImageLiveData.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        Log.i("TAG", "initObservers: ")
                        hideLoader()
                        resource.data?.let {
                            val response =
                                Gson().fromJson(it.toString(), DriverResponse::class.java)
                            response?.data?.let {
                                showMessage(response.message ?: "")
                                navigator.goBack()
                            } ?: run {
                                Log.i("TAG", "initObservers: 111")
                                showSomethingMessage()
                            }
                        } ?: run {
                            showSomethingMessage()
                        }
                    }

                    Status.ERROR -> {
                        Log.i("TAG", "initObservers:11 " + resource.message)
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
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(
                                images = response.data?.profileImage.toString(),
                                local = false
                            )
                        )
                        updateCount()
                    }

                    Status.ERROR -> {
                        hideLoader()
                        Log.i("TAG", "initObservers: " + resource.message)
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
                        navigator.goBack()

                    } ?: run {
                        showSomethingMessage()
                    }
                }

                Status.ERROR -> {
                    hideLoader()
                    Log.i("TAG", "initObservers: " + resource.message)
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> hideLoader()
            }
        }

        apiViewModel.getRequiredAllDocumentLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            for (item in response.data!!) {
                                if (item.id.toString() == document_id) {
                                    item.uploadedDriverDocument?.uploadedArray?.let { it1 ->
                                        getuploadedDriverArrayList.addAll(
                                            it1
                                        )
                                    }
                                }
                            }
                            for (item in getuploadedDriverArrayList) {
                                driverProfilePictureImagesAdapter.addItem(
                                    DriverProfilePictureImages(
                                        images = item,
                                        local = false
                                    )
                                )
                            }
                            updateCount()
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

    private val driverProfilePictureDetailsList = ArrayList<DriverProfilePictureDetails>()
    var statusCode: String? = null
    var document_id: String? = null

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AuthDriverPersonalProfilePictureFragmentBinding {
        return AuthDriverPersonalProfilePictureFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
        setUpImages()
    }

    private fun getProfile() {
        if (statusCode == UPDATE_PROFILE_PICTURE) {
            apiViewModel.getDriverProfile()
        }
    }

    private fun setUpText() = with(binding) {
        when (statusCode) {
            UPDATE_PROFILE_PICTURE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_profile_picture)
                textViewProfilePicture.text = getString(R.string.label_profile_picture)
            }

            UPLOAD_DRIVING_LICENCE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_driving_license)
                textViewProfilePicture.text = getString(R.string.label_driving_license)
            }

            UPLOAD_GOVERNMENT_ID -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_government_id)
                textViewProfilePicture.text = getString(R.string.label_government_id)
            }

            UPLOAD_BANK_DETAILS -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text = getString(R.string.label_bank_details)
                textViewProfilePicture.text = getString(R.string.label_attach_bank_account_details)
            }

            UPLOAD_VEHICLE_PUC -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text = getString(R.string.label_vehicle_puc)
            }

            UPLOAD_VEHICLE_INSURANCE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text = getString(R.string.label_vehicle_insurance)
            }

            UPLOAD_REGISTRATION_CERTIFICATION -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text =
                    getString(R.string.label_vehicle_registration_certificate)
            }

            UPLOAD_VEHICLE_PERMIT -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_documents)
                textViewProfilePicture.text = getString(R.string.label_vehicle_permit)
            }

            UPLOAD_FRONTBACK_WITH_NUMBER_PLATE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_front_back_with_number_plate)
            }

            UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_left_right_side_exterior)
            }

            UPLOAD_CHASIS_NUMBER_IMAGES -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_chassis_number_images)
            }

            UPLOAD_YOUR_PHOTO_WITH_VEHICLE -> {
                includedTopContent.textViewHello.text = getString(R.string.label_upload)
                includedTopContent.textViewWelcomeBack.text =
                    getString(R.string.label_vehicle_photos)
                textViewProfilePicture.text = getString(R.string.label_your_photo_with_vehicle)
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
                if (statusCode == PERSONAL_PROFILE_SCREEN) {

                } else if (statusCode == UPLOAD_DRIVING_LICENCE) {
                    apiViewModel.removeSpecificDocument(Request(id = "11", document = it.images))
                }
            }
            updateCount()
        }

        buttonSave.setOnClickListener {
            showLoader()
            if (statusCode == PERSONAL_PROFILE_SCREEN) {
                if (driverProfilePictureImagesAdapter.items?.get(0)?.local == true) {
                    val requestBody: RequestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "profile_image",
                            Uri.parse(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)?.lastPathSegment
                                ?: "",
                            RequestBody.create(
                                "image/*".toMediaTypeOrNull(),
                                File(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)
                            )
                        )
                        .build()
                    apiViewModel.updateProfileImage(requestBody)
                } else {
                    hideLoader()
                    navigator.goBack()
                }
            } else if (statusCode == UPLOAD_DRIVING_LICENCE) {
                for (item in driverProfilePictureImagesAdapter.items!!) {
                    if (item.local) {
                        val requestBody: RequestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "documents[]",
                                Uri.parse(item.images)?.lastPathSegment
                                    ?: "",
                                RequestBody.create(
                                    "image/*".toMediaTypeOrNull(),
                                    File(item.images)
                                )
                            )
                            .addFormDataPart("document_id", document_id ?: "")
                            .addFormDataPart("type", DRIVER_DOC)
                            .build()
                        apiViewModel.updateDocument(requestBody)
                    } else {
                        hideLoader()
                    }
                }
            } else if (statusCode == UPLOAD_GOVERNMENT_ID) {

            } else if (statusCode == UPLOAD_BANK_DETAILS) {

            }
            // session.isProfilePictureAdded = true
            //navigator.goBack()
        }
    }

    private fun setUpImages() {
//        mediaSelectHelper.canSelectMultipleImages(false)
        mediaSelectHelper.registerCallback(object : MediaSelector {
            override fun onImageUri(uri: Uri) {
                super.onImageUri(uri)
                if (statusCode == PERSONAL_PROFILE_SCREEN) {
                    driverProfilePictureImagesAdapter.isBitMap = false
                    driverProfilePictureImagesAdapter.clearAllItem()
                    driverProfilePictureImagesAdapter.addItem(
                        DriverProfilePictureImages(images = uri.path, local = true)
                    )
                } else if (statusCode == UPLOAD_DRIVING_LICENCE) {
                    driverProfilePictureImagesAdapter.isBitMap = false
                    driverProfilePictureImagesAdapter.addItem(
                        DriverProfilePictureImages(images = uri.path, local = true)
                    )
                }

                updateCount()
            }

            override fun onAnyFileSelected(outPutFileAny: OutPutFileAny) {
                Log.i("TAG", "onAnyFileSelected: " + outPutFileAny.type)
                binding.recyclerViewImages.show()
                when (outPutFileAny.type) {
                    FileType.Image -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.addItem(
                            DriverProfilePictureImages(images = outPutFileAny.uri.path)
                        )
                        Log.i("TAG", "onAnyFileSelected:11 " + outPutFileAny.uri.path)
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

    private fun setUpData() {
        driverProfilePictureDetailsList.clear()
        driverProfilePictureDetailsList.add(
            DriverProfilePictureDetails(
                text = getString(R.string.label_please_upload_a_clear_selfie)
            )
        )
        driverProfilePictureDetailsList.add(
            DriverProfilePictureDetails(
                text = getString(R.string.label_the_selfie_should_have_the_applicants_face_alone)
            )
        )
        driverProfilePictureDetailsList.add(
            DriverProfilePictureDetails(text = getString(R.string.label_upload_pdf_jpeg_png))
        )
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