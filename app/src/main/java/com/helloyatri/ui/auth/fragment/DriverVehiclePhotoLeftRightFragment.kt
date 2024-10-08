package com.helloyatri.ui.auth.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.request.DriverProfilePictureDetails
import com.helloyatri.data.request.DriverProfilePictureImages
import com.helloyatri.databinding.AuthDriverVehiclePhotoLeftRightFragmentBinding
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
import javax.inject.Inject

@AndroidEntryPoint
class DriverVehiclePhotoLeftRightFragment :
        BaseFragment<AuthDriverVehiclePhotoLeftRightFragmentBinding>() {

    @Inject
    lateinit var mediaSelectHelper: MediaSelectHelper

    private val driverVehiclePhotoLeftRightDetailsAdapter by lazy {
        DriverProfilePictureDetailsAdapter()
    }

    private val driverVehiclePhotoLeftRightImagesAdapter by lazy {
        DriverProfilePictureImagesAdapter()
    }

    private val driverVehiclePhotoLeftRightDetailsList = ArrayList<DriverProfilePictureDetails>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverVehiclePhotoLeftRightFragmentBinding {
        return AuthDriverVehiclePhotoLeftRightFragmentBinding.inflate(layoutInflater)
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
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_photos)
        includedTopContent.textViewYouHaveMissed.text = getString(
                R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it)
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDetails.apply {
            adapter = driverVehiclePhotoLeftRightDetailsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        recyclerViewImages.apply {
            adapter = driverVehiclePhotoLeftRightImagesAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    private fun setUpClickListener() = with(binding) {
        constraintUploadDocuments.setOnClickListener {
            mediaSelectHelper.openAnyIntent()
        }

        driverVehiclePhotoLeftRightImagesAdapter.setOnItemClickListener {
            driverVehiclePhotoLeftRightImagesAdapter.removeItem(it)
            updateCount()
        }

        buttonSave.setOnClickListener {
            session.isVehicleLeftRightPhotoAdded = true
            navigator.goBack()
        }
    }

    private fun setUpImages() {
        mediaSelectHelper.registerCallback(object : MediaSelector {
            override fun onAnyFileSelected(outPutFileAny: OutPutFileAny) {
                binding.recyclerViewImages.show()
                when (outPutFileAny.type) {
                    FileType.Image -> {
                        driverVehiclePhotoLeftRightImagesAdapter.isBitMap = false
                        driverVehiclePhotoLeftRightImagesAdapter.addItem(
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
                                    driverVehiclePhotoLeftRightImagesAdapter.isBitMap = true
                                    driverVehiclePhotoLeftRightImagesAdapter.addItem(
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
        driverVehiclePhotoLeftRightDetailsList.clear()
        driverVehiclePhotoLeftRightDetailsList.add(DriverProfilePictureDetails(text = getString(
                R.string.label_photocopies_and_printouts_of_documents_will_not_be_accepted)))
        driverVehiclePhotoLeftRightDetailsList.add(DriverProfilePictureDetails(
                text = getString(R.string.label_the_photo_and_all_details_must_be_clearly_visible)))
        driverVehiclePhotoLeftRightDetailsList.add(DriverProfilePictureDetails(text = getString(
                R.string.label_only_documents_that_are_less_than_10_mb_in_size_and_in_jpg_jpeg_png_or_pdf_format_will_be_accepted)))
        driverVehiclePhotoLeftRightDetailsAdapter.setItems(driverVehiclePhotoLeftRightDetailsList,
                1)

        driverVehiclePhotoLeftRightImagesAdapter.addItem(DriverProfilePictureImages(
                images = "https://images.unsplash.com/photo-1459603677915-a62079ffd002?q=80&w=1834&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"))
        updateCount()
    }

    private fun updateCount() = with(binding) {
        if (driverVehiclePhotoLeftRightImagesAdapter.items?.isEmpty()?.not() == true) {
            recyclerViewImages.show()
            textViewUploadDocuments.text = getString(R.string.label_s_documents_uploaded,
                    driverVehiclePhotoLeftRightImagesAdapter.itemCount.toString())
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