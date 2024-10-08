package com.helloyatri.ui.auth.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.response.DriverDocuments
import com.helloyatri.databinding.AuthDriverVehicleDocumentsFragmentBinding
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverVehicleDocumentsFragment : BaseFragment<AuthDriverVehicleDocumentsFragmentBinding>() {

    private val driverVehicleDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val driverVehicleDocumentsList = ArrayList<DriverDocuments>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverVehicleDocumentsFragmentBinding {
        return AuthDriverVehicleDocumentsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_complete)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_documents)
        includedTopContent.textViewYouHaveMissed.text = getString(
                R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it)
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverVehicleDocuments.apply {
            adapter = driverVehicleDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpClickListener() {
        driverVehicleDocumentsAdapter.setOnItemClickPositionListener { _, position ->
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
        driverVehicleDocumentsList.clear()
        driverVehicleDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_vehicle_puc),
                        isDataAdded = session.isVehiclePUCAdded))
        driverVehicleDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_vehicle_insurance),
                        isDataAdded = session.isVehicleInsuranceAdded))
        driverVehicleDocumentsList.add(DriverDocuments(
                documentName = getString(R.string.label_vehicle_registration_certificate),
                isDataAdded = session.isVehicleRegistrationAdded))
        driverVehicleDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_vehicle_permit),
                        isDataAdded = session.isVehiclePermitAdded))
        driverVehicleDocumentsAdapter.setItems(driverVehicleDocumentsList, 1)

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