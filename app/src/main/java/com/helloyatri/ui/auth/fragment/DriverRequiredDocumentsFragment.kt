package com.helloyatri.ui.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamingyards.sms.app.utils.Resource
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.R
import com.helloyatri.data.model.DataDocument
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.data.model.GetAllRequiredDocument
import com.helloyatri.databinding.AuthDriverRequiredDocumentsFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.auth.adapter.CommonAdapter
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverRequiredDocumentsFragment : BaseFragment<AuthDriverRequiredDocumentsFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    private val driverRequiredDocumentsAdapter by lazy {
        CommonAdapter()
    }

    private val driverRequiredDocumentsList = ArrayList<DriverDocuments>()
    private val getAllVehicleDocumentMutableList: MutableList<DataDocument> = mutableListOf()


    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverRequiredDocumentsFragmentBinding {
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
        if (arguments?.getString("statusCode") == "11") {
            apiViewModel.getAllRequiredDocument()
        }else if (arguments?.getString("statusCode") == "12"){
            apiViewModel.getVehicleDocument()
        }else{
            apiViewModel.getVehiclePhotos()
        }
    }

    private fun initObservers() {
        apiViewModel.getRequiredAllDocumentLiveData.observe(this){ resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            getAllVehicleDocumentMutableList.clear()
                            getAllVehicleDocumentMutableList.addAll(response.data)
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

        apiViewModel.getVehicleDocumentLiveData.observe(this){ resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            getAllVehicleDocumentMutableList.clear()
                            getAllVehicleDocumentMutableList.addAll(response.data)
                            Log.i("TAG", "initObservers: "+response.data.toString())
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

        apiViewModel.getVehiclePhotosLiveData.observe(this){ resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            getAllVehicleDocumentMutableList.clear()
                            getAllVehicleDocumentMutableList.addAll(response.data)
                            Log.i("TAG", "initObservers: "+response.data.toString())
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
        if (arguments?.getString("statusCode") == "11"){
            includedTopContent.textViewHello.text = getString(R.string.label_upload)
            includedTopContent.textViewWelcomeBack.text = getString(R.string.label_your_profile)
            textViewRequiredDocuments.text = getString(R.string.label_required_documents)
        }else if (arguments?.getString("statusCode") == "12"){
            includedTopContent.textViewHello.text = getString(R.string.label_complete)
            includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_documents)
            textViewRequiredDocuments.text = getString(R.string.label_vehicle_documents)
        }else{
            includedTopContent.textViewHello.text = getString(R.string.label_complete)
            includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_photos)
            textViewRequiredDocuments.text = getString(R.string.label_vehicle_photos)
        }

        includedTopContent.textViewYouHaveMissed.text = getString(
                R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it)
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverRequiredDocuments.apply {
            adapter = driverRequiredDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpClickListener() {
        driverRequiredDocumentsAdapter.setOnItemClickPositionListener { _, position ->
            when(getAllVehicleDocumentMutableList[position].name){
                "Driving Licence" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1002"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Driving Licence")
                }
                "Goverment Id" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1003"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Goverment Id")

                }
                "Bank Details" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1004"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Bank Details")

                }
                "Vehicle PUC" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1005"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Vehicle PUC")

                }
                "Vehicle Insurance" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1006"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Vehicle Insurance")

                }
                "Vehicle Registration Certificate" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1007"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Vehicle Registration Certificate")

                }
                "Vehicle Permit" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1008"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Vehicle Permit")

                }
                "Front-back with Number plage" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1009"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Front-back with Number plage")

                }
                "Left-Right Side Exterior" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1010"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Left-Right Side Exterior")

                }
                "Chassis Number Images" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1011"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Chassis Number Images")

                }
                "Your photo with vehicle" ->{
                    navigator.load(DriverPersonalProfilePictureFragment::class.java)
                        .setBundle(DriverPersonalProfilePictureFragment.createBundle(statusCode = "1012"))
                        .replace(true)
                    Log.i("TAG", "setUpClickListener: "+ "Your photo with vehicle")

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
            session.isRequiredDocumentsAdded = true
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
        if (session.isDrivingLicenseAdded && session.isGovernmentIDAdded && session.isBankAccountDetailsAdded) {
            binding.buttonSave.isClickable = true
            binding.buttonSave.isEnabled = true
            binding.buttonSave.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        } else {
            binding.buttonSave.isClickable = false
            binding.buttonSave.isEnabled = false
            binding.buttonSave.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.grey)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onResume() {
        super.onResume()

        setUpData()
    }
}