package com.helloyatri.ui.auth.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.databinding.AuthDriverVehiclePhotosFragmentBinding
import com.helloyatri.ui.auth.adapter.DriverDocumentsAdapter
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverVehiclePhotosFragment : BaseFragment<AuthDriverVehiclePhotosFragmentBinding>() {

    private val driverVehiclePhotosAdapter by lazy {
        DriverDocumentsAdapter()
    }

    private val driverVehiclePhotosList = ArrayList<DriverDocuments>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AuthDriverVehiclePhotosFragmentBinding {
        return AuthDriverVehiclePhotosFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpText()
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
    }

    private fun setUpText() = with(binding) {
        includedTopContent.textViewHello.text = getString(R.string.label_complete)
        includedTopContent.textViewWelcomeBack.text = getString(R.string.label_vehicle_photos)
        includedTopContent.textViewYouHaveMissed.text = getString(
                R.string.label_don_t_worry_only_you_can_see_your_personal_data_no_one_else_will_be_able_to_see_it)
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewDriverVehiclePhotos.apply {
            adapter = driverVehiclePhotosAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpClickListener() {
        driverVehiclePhotosAdapter.setOnItemClickPositionListener { _, position ->
            when (position) {
                0 -> {
                    navigator.load(DriverVehiclePhotoFrontBackFragment::class.java).replace(true)
                }

                1 -> {
                    navigator.load(DriverVehiclePhotoLeftRightFragment::class.java).replace(true)
                }

                2 -> {
                    navigator.load(DriverVehiclePhotoChassisFragment::class.java).replace(true)
                }

                3 -> {
                    navigator.load(DriverVehiclePhotoWithYourFragment::class.java).replace(true)
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            session.isVehiclePhotosAdded = true
            navigator.goBack()
        }
    }

    private fun setUpData() {
        driverVehiclePhotosList.clear()
        driverVehiclePhotosList.add(DriverDocuments(
                documentName = getString(R.string.label_front_back_with_number_plate),
                isDataAdded = session.isVehicleFrontBackPhotoAdded))
        driverVehiclePhotosList.add(
                DriverDocuments(documentName = getString(R.string.label_left_right_side_exterior),
                        isDataAdded = session.isVehicleLeftRightPhotoAdded))
        driverVehiclePhotosList.add(
                DriverDocuments(documentName = getString(R.string.label_chassis_number_images),
                        isDataAdded = session.isVehicleChassisAdded))
        driverVehiclePhotosList.add(
                DriverDocuments(documentName = getString(R.string.label_your_photo_with_vehicle),
                        isDataAdded = session.isVehicleWithYourPhotoAdded))
        driverVehiclePhotosAdapter.setItems(driverVehiclePhotosList, 1)

        if (session.isVehicleFrontBackPhotoAdded && session.isVehicleLeftRightPhotoAdded && session.isVehicleChassisAdded && session.isVehicleWithYourPhotoAdded) {
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