package com.helloyatri.ui.auth.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.DataDocument
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.data.model.GetAllRequiredDocument
import com.helloyatri.databinding.AuthDriverVehicleDocumentsFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.auth.adapter.CommonAdapter
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverVehicleDocumentsFragment : BaseFragment<AuthDriverVehicleDocumentsFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    private val driverRequiredDocumentsAdapter by lazy {
        CommonAdapter()
    }

    private val driverVehicleDocumentsList = ArrayList<DriverDocuments>()
    private val getVehicleDocumentMutableList: MutableList<DataDocument> = mutableListOf()


    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverVehicleDocumentsFragmentBinding {
        return AuthDriverVehicleDocumentsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
        initObservers()
        getAllVehicleDocument()
    }

    private fun getAllVehicleDocument() {
        apiViewModel.getVehicleDocument()
    }

    private fun initObservers() {
        apiViewModel.getVehicleDocumentLiveData.observe(this){ resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllRequiredDocument::class.java)
                        response?.data?.let {
                            getVehicleDocumentMutableList.clear()
                            getVehicleDocumentMutableList.addAll(response.data)
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
        includedTopContent.textViewHello.text = getString(R.string.label_complete)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_documents)
        includedTopContent.textViewYouHaveMissed.text = getString(
                R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it)
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverVehicleDocuments.apply {
            adapter = driverRequiredDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpClickListener() {
        driverRequiredDocumentsAdapter.setOnItemClickPositionListener { _, position ->
            when (position) {
                0 -> {
                    navigator.load(DriverVehiclePUCFragment::class.java).replace(true)
                }

                1 -> {
                    navigator.load(DriverVehicleInsuranceFragment::class.java).replace(true)
                }

                2 -> {
                    navigator.load(DriverVehicleRegistrationCertificateFragment::class.java)
                            .replace(true)
                }

                3 -> {
                    navigator.load(DriverVehiclePermitFragment::class.java).replace(true)
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            session.isVehicleDocumentsAdded = true
            navigator.goBack()
        }
    }

    private fun setUpData() {
//        driverVehicleDocumentsList.clear()
//        driverVehicleDocumentsList.add(
//                DriverDocuments(documentName = getString(R.string.label_vehicle_puc),
//                        isDataAdded = session.isVehiclePUCAdded))
//        driverVehicleDocumentsList.add(
//                DriverDocuments(documentName = getString(R.string.label_vehicle_insurance),
//                        isDataAdded = session.isVehicleInsuranceAdded))
//        driverVehicleDocumentsList.add(DriverDocuments(
//                documentName = getString(R.string.label_vehicle_registration_certificate),
//                isDataAdded = session.isVehicleRegistrationAdded))
//        driverVehicleDocumentsList.add(
//                DriverDocuments(documentName = getString(R.string.label_vehicle_permit),
//                        isDataAdded = session.isVehiclePermitAdded))
        driverRequiredDocumentsAdapter.setItems(getVehicleDocumentMutableList.toList(), 1)

        if (session.isVehiclePUCAdded && session.isVehicleInsuranceAdded && session.isVehicleRegistrationAdded && session.isVehiclePermitAdded) {
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