package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.model.AccountPreferences
import com.helloyatri.databinding.AccountPreferenceFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPreferencesFragment : BaseFragment<AccountPreferenceFragmentBinding>() {

    private val accountPreferencesAdapter by lazy {
        AdapterAccountPreferences()
    }

    private val accountPreferencesList = ArrayList<AccountPreferences>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountPreferenceFragmentBinding {
        return AccountPreferenceFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpRecyclerView()
        setUpData()
        setUpClickListener()
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
        accountPreferencesList.clear()
        accountPreferencesList.add(
                AccountPreferences(accountPreferences = "Accept only Cash", isSwitchOn = true))
        accountPreferencesList.add(
                AccountPreferences(accountPreferences = "Auto-accept Trips", isSwitchOn = false))
        accountPreferencesList.add(
                AccountPreferences(accountPreferences = "Long Distance Trips", isSwitchOn = false))
        accountPreferencesList.add(
                AccountPreferences(accountPreferences = "Wheelchair accept", isSwitchOn = true))
        accountPreferencesList.add(
                AccountPreferences(accountPreferences = "Intercity Trip", isSwitchOn = true))
        accountPreferencesAdapter.setItems(accountPreferencesList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true)
                .setToolbarTitle(getString(R.string.title_preferences)).build()
    }
}