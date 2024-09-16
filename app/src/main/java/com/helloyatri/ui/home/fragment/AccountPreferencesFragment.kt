package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.Preference
import com.helloyatri.data.model.PreferencesResponse
import com.helloyatri.databinding.AccountPreferenceFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPreferencesFragment : BaseFragment<AccountPreferenceFragmentBinding>() {

    private val apiViewModel by activityViewModels<ApiViewModel>()

    private val accountPreferencesAdapter by lazy {
        AdapterAccountPreferences(onClickListener = {
            it.key?.let {
                apiViewModel.updateDriverPreferences(
                    Request(
                        key = it
                    )
                )
            }
        })
    }

    private val accountPreferencesList = ArrayList<Preference>()

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AccountPreferenceFragmentBinding {
        return AccountPreferenceFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpRecyclerView()
        setUpClickListener()
        initObservers()
        accountPreferencesList.clear()
        apiViewModel.getDriverPreferences()
    }

    private fun initObservers() {
        apiViewModel.getDriverPreferencesLiveData.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                        it.data?.let { it ->
                            val response =
                                Gson().fromJson(it, PreferencesResponse::class.java)
                            response?.data?.let {
                                accountPreferencesList.addAll(it)
                                setUpData()
                            }
                        }
                    }

                    Status.ERROR -> {
                        hideLoader()
                    }
                }
            }
        }

        apiViewModel.updateDriverPreferencesLiveData.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoader()
                    Status.SUCCESS -> {
                        hideLoader()
                    }

                    Status.ERROR -> {
                        hideLoader()
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewPreference.apply {
            adapter = accountPreferencesAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpClickListener() = with(binding) {
        buttonUpdate.setOnClickListener {
            navigator.goBack()
        }
    }

    private fun setUpData() {
//        accountPreferencesList.clear()
//        accountPreferencesList.add(
//                AccountPreferences(accountPreferences = "Accept only Cash", isSwitchOn = true))
//        accountPreferencesList.add(
//                AccountPreferences(accountPreferences = "Auto-accept Trips", isSwitchOn = false))
//        accountPreferencesList.add(
//                AccountPreferences(accountPreferences = "Long Distance Trips", isSwitchOn = false))
//        accountPreferencesList.add(
//                AccountPreferences(accountPreferences = "Wheelchair accept", isSwitchOn = true))
//        accountPreferencesList.add(
//                AccountPreferences(accountPreferences = "Intercity Trip", isSwitchOn = true))
        accountPreferencesAdapter.setItems(accountPreferencesList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true)
            .setToolbarTitle(getString(R.string.title_preferences)).build()
    }
}