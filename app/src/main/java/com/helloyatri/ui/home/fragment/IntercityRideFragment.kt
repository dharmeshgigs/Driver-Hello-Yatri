package com.helloyatri.ui.home.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.databinding.IntercityRideRequestFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.IntercityRideMainAdapter
import com.helloyatri.ui.home.dialog.CalenderDialog
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.getScheduleRideList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntercityRideFragment : BaseFragment<IntercityRideRequestFragmentBinding>() {

    private val intercityRideMainAdapter by lazy {
        IntercityRideMainAdapter()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): IntercityRideRequestFragmentBinding {
        return IntercityRideRequestFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        binding.textViewMarkAllRead.hide()
        setAdapter()
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        intercityRideMainAdapter.setOnAcceptOrDeclineClickListener { item, subItem, action ->
            Log.e("+++MainItem", Gson().toJson(item))
            Log.e("+++SubItem", Gson().toJson(subItem))
            Log.e("+++SubNotificationFragmentItemAction", action)
        }

        imageViewBack.setOnClickListener {
            navigator.goBack()
        }

        imageViewCalendar.setOnClickListener {
            CalenderDialog {}.show(childFragmentManager, "")
        }
    }

    private fun setAdapter() = with(binding) {
        recyclerView.apply {
            adapter = intercityRideMainAdapter
            intercityRideMainAdapter.setItems(requireActivity().getScheduleRideList(), 1)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}