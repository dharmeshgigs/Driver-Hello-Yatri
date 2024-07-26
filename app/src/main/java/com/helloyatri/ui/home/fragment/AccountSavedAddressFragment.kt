package com.helloyatri.ui.home.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.DataDocument
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.data.model.GetAllAddressModel
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.AccountSavedAddressFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterSavedAddress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSavedAddressFragment : BaseFragment<AccountSavedAddressFragmentBinding>(){

    private val adapterSavedAddress by lazy {
        AdapterSavedAddress()
    }

    private val savedAddressDataList = ArrayList<SavedAddress>()
    private val apiViewModel by viewModels<ApiViewModel>()
    private val getAllAddressMutableList: MutableList<SavedAddress> = mutableListOf()


    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AccountSavedAddressFragmentBinding {
        return AccountSavedAddressFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        apiViewModel.getAllAddress()
        setUpRecyclerView()
        initObservers()
//        setUpData()
        setUpClickListener()
    }

    private fun initObservers() {
        apiViewModel.getAllAddressLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource?.data?.let {
                        val response =
                            Gson().fromJson(it.toString(), GetAllAddressModel::class.java)
                        response?.data?.let {
                            getAllAddressMutableList.clear()
                            getAllAddressMutableList.addAll(response.data)
                            adapterSavedAddress.setItems(getAllAddressMutableList, 1)
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

                Status.LOADING -> hideLoader()
            }
        }
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewSavedAddress.apply {
            adapter = adapterSavedAddress
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpClickListener() = with(binding) {
        buttonAddNew.setOnClickListener {
            navigator.load(AccountAddNewAddressFragment::class.java)
                .setBundle(AccountAddNewAddressFragment.createBundle(isFromEdit = false))
                .replace(true)
        }

        adapterSavedAddress.setOnItemClickListener {
            navigator.load(AccountAddNewAddressFragment::class.java)
                .setBundle(
                    AccountAddNewAddressFragment.createBundle(
                        isFromEdit = true,
                        lat = it.latitude,
                        long = it.longitude,
                        name = it.name,
                        location = it.location
                    )
                )
                .replace(true)
        }
    }

    private fun setUpData() {
        //savedAddressDataList.clear()
//        savedAddressDataList.add(SavedAddress(saveAddress = "101 National Dr. San Bruno, CA 94580"))
//        savedAddressDataList.add(SavedAddress(saveAddress = "102 National Dr. San Bruno, CA 94580"))
//        savedAddressDataList.add(SavedAddress(saveAddress = "103 National Dr. San Bruno, CA 94580"))
//        savedAddressDataList.add(SavedAddress(saveAddress = "104 National Dr. San Bruno, CA 94580"))
        adapterSavedAddress.setItems(getAllAddressMutableList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true)
            .setToolbarTitle(getString(R.string.title_saved_places)).build()
    }

}