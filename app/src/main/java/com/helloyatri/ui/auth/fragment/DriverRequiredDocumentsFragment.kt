package com.helloyatri.ui.auth.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.databinding.AuthDriverRequiredDocumentsFragmentBinding
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverRequiredDocumentsFragment : BaseFragment<AuthDriverRequiredDocumentsFragmentBinding>() {

    private val driverRequiredDocumentsAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val driverRequiredDocumentsList = ArrayList<DriverDocuments>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverRequiredDocumentsFragmentBinding {
        return AuthDriverRequiredDocumentsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_upload)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_your_profile)
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
            when (position) {
                0 -> {
                    navigator.load(DriverRequiredDocumentLicenceFragment::class.java).replace(true)
                }

                1 -> {
                    navigator.load(DriverRequiredDocumentGovernmentIDFragment::class.java)
                            .replace(true)
                }

                2 -> {
                    navigator.load(DriverRequiredDocumentBankDetailsFragment::class.java)
                            .replace(true)
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            session.isRequiredDocumentsAdded = true
            navigator.goBack()
        }
    }

    private fun setUpData() {
        driverRequiredDocumentsList.clear()
        driverRequiredDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_driving_license),
                        isDataAdded = session.isDrivingLicenseAdded))
        driverRequiredDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_government_id),
                        isDataAdded = session.isGovernmentIDAdded))
        driverRequiredDocumentsList.add(
                DriverDocuments(documentName = getString(R.string.label_bank_account_details),
                        isDataAdded = session.isBankAccountDetailsAdded))
        driverRequiredDocumentsAdapter.setItems(driverRequiredDocumentsList, 1)
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