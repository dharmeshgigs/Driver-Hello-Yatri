package com.helloyatri.ui.home.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.ReportCrashImageDataModel
import com.helloyatri.data.model.ReportCrashResponse
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.TripReportCrashFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterReportCrashImage
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.enableTextView
import com.helloyatri.utils.extension.trimmedText
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
        setUpData()
    }

    private fun initViews() = with(binding) {
        textViewLocationAddress.text = apiViewModel.location.second
        includedFullName.textViewTitle.text = getString(R.string.label_description)
        includedFullName.editText.hint = getString(R.string.label_description)
        buttonSubmit.isClickable = false
        buttonSubmit.isEnabled = false
        buttonSubmit.enableTextView(false)
        includedFullName.editText.doAfterTextChanged {
            checkEmptyData()
        }
    }

    private fun setClickListeners() = with(binding) {
        buttonSubmit.setOnClickListener {
            val multiPartBodyBuilder = MultipartBody.Builder()
            multiPartBodyBuilder.setType(MultipartBody.FORM);
            multiPartBodyBuilder.addFormDataPart(
                "trip_id", ("" + apiViewModel.tripRequest.value?.tripDetails?.id)
            )
            multiPartBodyBuilder.addFormDataPart(
                "description", includedFullName.editText.text.toString().trim()
            )
            multiPartBodyBuilder.addFormDataPart(
                "location", apiViewModel.location.second
            )
            multiPartBodyBuilder.addFormDataPart(
                "latitude", apiViewModel.location.first.latitude.toString()
            )
            multiPartBodyBuilder.addFormDataPart(
                "longitude", apiViewModel.location.first.longitude.toString()
            )
            adapterReportCrashImage.items?.forEachIndexed { index, it ->
                if(index > 0) {
                    val file = File(it.image)
                    multiPartBodyBuilder.addFormDataPart(
                        Constants.Images, file.getName(), RequestBody.create(
                            "image/*".toMediaTypeOrNull(), file
                        )
                    )
                }
            }
            apiViewModel.tripReportCrash(multiPartBodyBuilder.build())
        }
    }

    private fun initObservers() {
        apiViewModel.tripReportCrashLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        it.data?.let {
                            val response = Gson().fromJson(it, ReportCrashResponse::class.java)
                            response?.let { it1 ->
                                showMessage(it1.message ?: "")
                            } ?: run {
                                showSomethingMessage()
                            }
                        } ?: run {
                            showSomethingMessage()
                        }
                        navigator.goBack()
                    }

                    Status.ERROR -> {
                        hideLoader()
                        showSomethingMessage()
                    }

                    Status.LOADING -> {
                        showLoader()
                    }
                }
            }
        }
    }

    private fun checkEmptyData() = with(binding) {
        if (includedFullName.editText.trimmedText.isNotEmpty()) {
            buttonSubmit.isClickable = true
            buttonSubmit.isEnabled = true
            buttonSubmit.enableTextView(true)
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
                imageList.removeAt(position)
                adapterReportCrashImage.setItems(imageList, 1)
            }
        }
    }

    private fun setUpData() {
        mediaSelectHelper.registerCallback(object : MediaSelector {
            override fun onImageUri(uri: Uri) {
                super.onImageUri(uri)
                imageList.add(ReportCrashImageDataModel("" + uri.path))
                adapterReportCrashImage.setItems(imageList, 1)
            }

            override fun onAnyFileSelected(outPutFileAny: OutPutFileAny) {
            }
        })

    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}