package com.helloyatri.ui.auth.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.request.DriverProfilePictureDetails
import com.helloyatri.data.request.DriverProfilePictureImages
import com.helloyatri.databinding.AuthDriverPersonalProfilePictureFragmentBinding
import com.helloyatri.network.APIFactory
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.auth.adapter.DriverProfilePictureDetailsAdapter
import com.helloyatri.ui.auth.adapter.DriverProfilePictureImagesAdapter
import com.helloyatri.ui.base.BaseFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideLoader()
        apiViewModel.updateProfileImageLiveData.get(this,{
            hideLoader()
            when(it.code) {
                APIFactory.ResponseCode.SUCCESS -> {

                    navigator.goBack()
                }

                else -> {

                    showMessage(it.message)
                }
            }
        })
    }
    private val driverProfilePictureDetailsList = ArrayList<DriverProfilePictureDetails>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverPersonalProfilePictureFragmentBinding {
        return AuthDriverPersonalProfilePictureFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
        setUpImages()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_upload)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_profile_picture)
        includedTopContent.textViewYouHaveMissed.text = getString(
                R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it)
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
            mediaSelectHelper.openAnyIntent()
        }

        driverProfilePictureImagesAdapter.setOnItemClickListener {
            driverProfilePictureImagesAdapter.removeItem(it)
            updateCount()
        }

        buttonSave.setOnClickListener {


            showLoader()
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "profile_image",
                    Uri.parse(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images)?.lastPathSegment?:"",
                    RequestBody.create("image/*".toMediaTypeOrNull(), File(driverProfilePictureImagesAdapter.items?.getOrNull(0)?.images))
                )
                .build()

            apiViewModel.updateProfileImage(requestBody)
           // session.isProfilePictureAdded = true
            //navigator.goBack()
        }
    }

    private fun setUpImages() {
        mediaSelectHelper.registerCallback(object : MediaSelector {
            override fun onAnyFileSelected(outPutFileAny: OutPutFileAny) {
                binding.recyclerViewImages.show()
                when (outPutFileAny.type) {
                    FileType.Image -> {
                        driverProfilePictureImagesAdapter.isBitMap = false
                        driverProfilePictureImagesAdapter.addItem(
                                DriverProfilePictureImages(images = outPutFileAny.uri.path))
                        updateCount()
                    }

                    FileType.Pdf -> {
                        requireActivity().contentResolver.openFileDescriptor(outPutFileAny.uri, "r")
                                ?.use { p ->
                                    val pdfRenderer = PdfRenderer(p).openPage(0)
                                    val bitmap = Bitmap.createBitmap(pdfRenderer.width,
                                            pdfRenderer.height, Bitmap.Config.ARGB_8888)

                                    val canvas = Canvas(bitmap)
                                    canvas.drawColor(Color.WHITE)

                                    pdfRenderer.render(bitmap, null, null,
                                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                    pdfRenderer.close()
                                    driverProfilePictureImagesAdapter.isBitMap = true
                                    driverProfilePictureImagesAdapter.addItem(
                                            DriverProfilePictureImages(
                                                    images = outPutFileAny.uri.path,
                                                    imageBitmap = bitmap))
                                }
                        updateCount()
                    }

                    else -> {
                        showMessage(
                                getString(R.string.validation_only_pdf_jpeg_png_files_are_allowed))
                    }
                }
            }
        })
    }

    private fun setUpData() {
        driverProfilePictureDetailsList.clear()
        driverProfilePictureDetailsList.add(DriverProfilePictureDetails(
                text = getString(R.string.label_please_upload_a_clear_selfie)))
        driverProfilePictureDetailsList.add(DriverProfilePictureDetails(
                text = getString(R.string.label_the_selfie_should_have_the_applicants_face_alone)))
        driverProfilePictureDetailsList.add(
                DriverProfilePictureDetails(text = getString(R.string.label_upload_pdf_jpeg_png)))
        driverProfilePictureDetailsAdapter.setItems(driverProfilePictureDetailsList, 1)

        driverProfilePictureImagesAdapter.addItem(DriverProfilePictureImages(
                images = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=1780&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"))
        updateCount()
    }

    private fun updateCount() = with(binding) {
        if (driverProfilePictureImagesAdapter.items?.isEmpty()?.not() == true) {
            recyclerViewImages.show()
            textViewUploadDocuments.text = getString(R.string.label_s_documents_uploaded,
                    driverProfilePictureImagesAdapter.itemCount.toString())
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