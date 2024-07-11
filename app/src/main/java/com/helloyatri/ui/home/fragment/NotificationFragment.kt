package com.helloyatri.ui.home.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.databinding.FragmentNotificationBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.NotificationMainAdapter
import com.helloyatri.utils.getNotificationList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {

    private val notificationAdapter by lazy {
        NotificationMainAdapter()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setAdapter()
        setClickListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setClickListener() = with(binding) {
        textViewMarkAllRead.setOnClickListener {
            notificationAdapter.items?.flatMap { items ->
                items.subList!!.map {
                    if (!it.isRead) {
                        it.isRead = true
                    }
                }
            }
            notificationAdapter.notifyDataSetChanged()
        }
    }

    private fun setAdapter() = with(binding) {
        recyclerView.apply {
            adapter = notificationAdapter
            notificationAdapter.setItems(requireActivity().getNotificationList(), 1)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarColor(R.color.backgroundColor)
                .setToolbarTitle(getString(R.string.title_notifications)).build()
    }
}