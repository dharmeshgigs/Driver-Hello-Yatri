package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.databinding.FragmentNotificationBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.ScheduleRideMainAdapter
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.getScheduleRideList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleRideFragment : BaseFragment<FragmentNotificationBinding>() {

    private val scheduleRideMainAdapter by lazy {
        ScheduleRideMainAdapter()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        binding.textViewMarkAllRead.hide()
        setAdapter()
    }

    private fun setAdapter() = with(binding) {
        recyclerView.apply {
            adapter = scheduleRideMainAdapter
            scheduleRideMainAdapter.setItems(requireActivity().getScheduleRideList(), 1)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarColor(R.color.backgroundColor)
                .setToolbarTitle(getString(R.string.titleschedule_ride)).build()
    }
}