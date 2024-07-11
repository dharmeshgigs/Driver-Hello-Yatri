package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.response.SavedAddress
import com.helloyatri.databinding.FragmentStartRideFromBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterNearbyLocations
import com.helloyatri.ui.home.adapter.AdapterSearchRideByLocation
import com.helloyatri.ui.home.adapter.StartRideSavedAddressAdapter
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.getLocationList
import com.helloyatri.utils.getNearByLocationList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartRideFromFragment : BaseFragment<FragmentStartRideFromBinding>() {

    private val adapterNearbyLocations by lazy {
        AdapterNearbyLocations()
    }

    private val adapterSearchRideByLocation by lazy {
        AdapterSearchRideByLocation()
    }

    private val adapterSavedAddress by lazy {
        StartRideSavedAddressAdapter()
    }

    private val savedAddressDataList = ArrayList<SavedAddress>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): FragmentStartRideFromBinding {
        return FragmentStartRideFromBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setAdapter()
        setUpSavedLocations()
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        editText.doAfterTextChanged {
            if (!it.isNullOrEmpty()) {
                recyclerViewSearchByLocation.show()
                adapterSearchRideByLocation.filter.filter(it.toString())
            } else {
                recyclerViewSearchByLocation.hide()
            }
        }

        textViewLabelManage.setOnClickListener {
            navigator.load(AccountSavedAddressFragment::class.java).replace(true)
        }

        adapterSavedAddress.setOnItemClickListener {
            navigator.load(AccountAddNewAddressFragment::class.java).replace(true)
        }

        /*includeAddNew.root.setOnClickListener {
            navigator.load(AccountAddNewAddressFragment::class.java).replace(true)
        }*/
    }

    private fun setUpSavedLocations() = with(binding) {
        /*includeAddNew.imageViewIcon.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_new)
        includeSave1.imageViewIcon.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_save)
        includeSave2.imageViewIcon.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_save)

        includeAddNew.textViewTitle.text = getString(R.string.label_add_new)
        includeSave1.textViewTitle.text = getString(R.string.label_save_1)
        includeSave2.textViewTitle.text = getString(R.string.label_save_2)*/
    }

    private fun setAdapter() = with(binding) {
        recyclerViewNearByLocation.apply {
            adapter = adapterNearbyLocations
            adapterNearbyLocations.setItems(requireActivity().getNearByLocationList(), 1)
            setHasFixedSize(true)
        }

        recyclerViewSearchByLocation.apply {
            adapter = adapterSearchRideByLocation
            adapterSearchRideByLocation.setItem(requireActivity().getLocationList(), 1)
        }

        recyclerViewSavedAddress.apply {
            adapter = adapterSavedAddress
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            setUpData()
        }
    }

    private fun setUpData() {
        savedAddressDataList.clear()
        savedAddressDataList.add(SavedAddress(isAddressAdded = false))
        savedAddressDataList.add(SavedAddress(saveAddress = "101 National Dr. San Bruno, CA 94580",
                isAddressAdded = true))
        savedAddressDataList.add(SavedAddress(saveAddress = "102 National Dr. San Bruno, CA 94580",
                isAddressAdded = true))
        savedAddressDataList.add(SavedAddress(saveAddress = "103 National Dr. San Bruno, CA 94580",
                isAddressAdded = true))
        adapterSavedAddress.setItems(savedAddressDataList, 1)
    }


    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true)
        showBackButton(true)
        setToolbarTitle(getString(R.string.title_start_ride_from)).build()
    }
}