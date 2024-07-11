package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.response.DriverDocuments
import com.helloyatri.data.response.DriverVerification
import com.helloyatri.databinding.AccountDocumentsFragmentBinding
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.auth.adapter.DriverVerificationAdapter
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountDocumentsFragment : BaseFragment<AccountDocumentsFragmentBinding>() {

    private val driverDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val vehicleDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val driverVerificationAdapter by lazy {
        DriverVerificationAdapter()
    }

    private val driverDocumentsList = ArrayList<DriverDocuments>()
    private val vehicleDocumentsList = ArrayList<DriverDocuments>()
    private val driverVerificationDataList = ArrayList<DriverVerification>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountDocumentsFragmentBinding {
        return AccountDocumentsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpRecyclerView()
        setUpData()
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverDetails.apply {
            adapter = driverDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        recyclerViewVehicleDetails.apply {
            adapter = vehicleDocumentsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        recyclerViewDriverVerification.apply {
            adapter = driverVerificationAdapter
        }
    }

    private fun setUpData() {
        driverDocumentsList.clear()
        driverDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_personal_details),
                        isDataAdded = true))
        driverDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_profile_picture),
                        isDataAdded = true))
        driverDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_required_documents),
                        isDataAdded = true))

        vehicleDocumentsList.clear()
        vehicleDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_add_a_vehicle),
                        isDataAdded = true))
        vehicleDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_vehicle_documents),
                        isDataAdded = true))
        vehicleDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_vehicle_photos),
                        isDataAdded = true))

        driverVerificationDataList.clear()
        driverVerificationDataList.add(DriverVerification(image = R.drawable.image_help_desk,
                text = "+18 98765 43210 \n+91 98765 43210", button = "Call agency"))
        driverVerificationDataList.add(DriverVerification(image = R.drawable.image_navigate_there,
                text = "Cinemax, Rectory Cottage, Court Road", button = "Navigate there"))

        driverDocumentsAdapter.setItems(driverDocumentsList, 1)
        vehicleDocumentsAdapter.setItems(vehicleDocumentsList, 1)
        driverVerificationAdapter.setItems(driverVerificationDataList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarTitle("Documents").build()
    }
}