package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.AccountSavedAddressFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterSavedAddress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSavedAddressFragment : BaseFragment<AccountSavedAddressFragmentBinding>() {

    private val adapterSavedAddress by lazy {
        AdapterSavedAddress()
    }

    private val savedAddressDataList = ArrayList<SavedAddress>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountSavedAddressFragmentBinding {
        return AccountSavedAddressFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
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
                    .setBundle(AccountAddNewAddressFragment.createBundle(isFromEdit = true))
                    .replace(true)
        }
    }

    private fun setUpData() {
        savedAddressDataList.clear()
        savedAddressDataList.add(SavedAddress(saveAddress = "101 National Dr. San Bruno, CA 94580"))
        savedAddressDataList.add(SavedAddress(saveAddress = "102 National Dr. San Bruno, CA 94580"))
        savedAddressDataList.add(SavedAddress(saveAddress = "103 National Dr. San Bruno, CA 94580"))
        savedAddressDataList.add(SavedAddress(saveAddress = "104 National Dr. San Bruno, CA 94580"))
        adapterSavedAddress.setItems(savedAddressDataList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true)
                .setToolbarTitle(getString(R.string.title_saved_places)).build()
    }
}