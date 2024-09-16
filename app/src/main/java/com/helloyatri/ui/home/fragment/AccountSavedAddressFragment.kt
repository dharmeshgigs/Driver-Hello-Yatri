package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.GetAllAddressModel
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.AccountSavedAddressFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterSavedAddress
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountSavedAddressFragment : BaseFragment<AccountSavedAddressFragmentBinding>() {
    private val adapterSavedAddress by lazy {
        AdapterSavedAddress()
    }

    private val apiViewModel: ApiViewModel by activityViewModels()
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
                            getAllAddressMutableList.add(SavedAddress(id=0))
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
                .setBundle(AccountAddNewAddressFragment.createBundle(isFromEdit = false)).replace(false)
        }

        adapterSavedAddress.setOnItemClickListener {
            if(it.id==0) {
                buttonAddNew.performClick()
                return@setOnItemClickListener
            }
            try {
                navigator.load(AccountAddNewAddressFragment::class.java)
                    .setBundle(
                        AccountAddNewAddressFragment.createBundle(
                            isFromEdit = true,
                            data = Gson().toJson(it),
                        )
                    ).replace(false )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpData() {
        adapterSavedAddress.setItems(getAllAddressMutableList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true)
            .setToolbarTitle(getString(R.string.title_saved_places)).build()
    }

}