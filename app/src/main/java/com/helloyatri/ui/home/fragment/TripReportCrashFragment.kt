package com.helloyatri.ui.home.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.AccountPaymentData
import com.helloyatri.data.model.DriverProfilePictureImages
import com.helloyatri.data.model.ReportCrashImageDataModel
import com.helloyatri.data.model.TabTypeForPayment
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.AccountPaymentReqAcceptFragmentBinding
import com.helloyatri.databinding.TripReportCrashFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.auth.fragment.DriverRequiredDocumentsFragment
import com.helloyatri.ui.auth.fragment.DriverVehicleDetailsFragment
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountPayment
import com.helloyatri.ui.home.adapter.AdapterReportCrashImage
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
import com.helloyatri.utils.Constants.VEHICLE_DOCUMENT
import com.helloyatri.utils.Constants.VEHICLE_PHOTO
import com.helloyatri.utils.extension.enableTextView
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.trimmedText
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
class TripReportCrashFragment : BaseFragment<TripReportCrashFragmentBinding>() {

    @Inject
    lateinit var mediaSelectHelper: MediaSelectHelper

    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var tripRiderModel: TripRiderModel? = null
    private val adapterReportCrashImage by lazy {
        AdapterReportCrashImage()
    }

    private val imageList = arrayListOf<ReportCrashImageDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripRiderModel = apiViewModel.tripRequest.value
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): TripReportCrashFragmentBinding {
        return TripReportCrashFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initViews()
        initObservers()
        setUpRecyclerView()
        setClickListeners()
    }

    private fun initViews() = with(binding) {
        textViewLocationAddress.text = apiViewModel.location.second
        includedFullName.textViewTitle.text = getString(R.string.label_description)
        includedFullName.editText.hint = getString(R.string.label_description)
        buttonSubmit.isClickable = false
        buttonSubmit.isEnabled = false
        buttonSubmit.enableTextView(false)


        includedFullName.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                checkEmptyData()
            }

        })
    }

    private fun setClickListeners() = with(binding) {


        buttonSubmit.setOnClickListener {

            Log.e(
                " >>> >>> >>> ", "trip_id   >>  ${apiViewModel.tripRequest.value?.tripDetails?.id}"
            )
            Log.e(
                " >>> >>> >>> ",
                "description   >>  ${includedFullName.editText.text.toString().trim()}"
            )
            Log.e(" >>> >>> >>> ", "location   >>  ${apiViewModel.location.second}")
            Log.e(" >>> >>> >>> ", "latitude   >>  ${apiViewModel.location.first.latitude}")
            Log.e(" >>> >>> >>> ", "longitude   >>  ${apiViewModel.location.first.longitude}")

            showLoader()

            val multiPartBodyBuilder = MultipartBody.Builder()
            multiPartBodyBuilder.setType(MultipartBody.FORM);
            multiPartBodyBuilder.addFormDataPart(
                "trip_id", ("" + apiViewModel.tripRequest.value?.tripDetails?.id) ?: ""
            )
            multiPartBodyBuilder.addFormDataPart(
                "description", includedFullName.editText.text.toString().trim() ?: ""
            )
            multiPartBodyBuilder.addFormDataPart(
                "location", apiViewModel.location.second ?: ""
            )
            multiPartBodyBuilder.addFormDataPart(
                "latitude", ("" + apiViewModel.location.first.latitude) ?: ""
            )
            multiPartBodyBuilder.addFormDataPart(
                "longitude", ("" + apiViewModel.location.first.longitude) ?: ""
            )

            adapterReportCrashImage.items?.filter {
                it.image.isNotEmpty()
            }?.let {

                it.forEach {
                    val file = it.image?.let { it1 -> File(it1) }
                    multiPartBodyBuilder.addFormDataPart(
                        Constants.Images, file?.getName(), RequestBody.create(
                            "image/*".toMediaTypeOrNull(), file!!
                        )
                    )
                }
                apiViewModel.tripReportCrash(multiPartBodyBuilder.build())
//                hideLoader()
//                navigator.goBack()
            } ?: run {
//                hideLoader()
//                navigator.goBack()
            }
        }
    }

    private fun initObservers() {
        apiViewModel.tripReportCrashLiveData.observe(this) { resource ->
            Log.e(">> >>> >> >>", "tripReportCrashLiveData >> ${Gson().toJson(resource)} ")
            hideLoader()
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        navigator.goBack()
                    }

                    Status.ERROR -> {
                    }

                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun checkEmptyData() = with(binding) {
        if (includedFullName.editText.trimmedText.isNotEmpty() && adapterReportCrashImage.items?.size!! > 1) {
            buttonSubmit.isClickable = true
            buttonSubmit.isEnabled = true
            buttonSubmit.enableTextView(true)
//            buttonSubmit.backgroundTintList =
//                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        } else {
            buttonSubmit.isClickable = false
            buttonSubmit.isEnabled = false
            buttonSubmit.enableTextView(false)
        }
    }


    private fun setUpRecyclerView() = with(binding) {

        imageList.add(ReportCrashImageDataModel(""))
        adapterReportCrashImage.setItems(imageList, 1)

        recyclerViewImages.apply {
            adapter = adapterReportCrashImage
            layoutManager = GridLayoutManager(this.context, 3)
        }

        adapterReportCrashImage.setOnItemClickPositionListener { _, position ->

            if (position == 0) {
                mediaSelectHelper.selectOptionsForImagePicker(false)
            } else {

            }
        }
        setUpData()
    }

    private fun setUpData() {
        mediaSelectHelper.registerCallback(object : MediaSelector {
            override fun onImageUri(uri: Uri) {
                super.onImageUri(uri)
                imageList.add(ReportCrashImageDataModel("" + uri))
                adapterReportCrashImage.setItems(imageList, 1)
                checkEmptyData()
            }

            override fun onAnyFileSelected(outPutFileAny: OutPutFileAny) {

            }
        })

    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}