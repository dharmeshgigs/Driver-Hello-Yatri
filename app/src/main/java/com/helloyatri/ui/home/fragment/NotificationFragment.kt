package com.helloyatri.ui.home.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.NotificationsData
import com.helloyatri.data.model.NotificationsResponse
import com.helloyatri.databinding.FragmentNotificationBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.NotificationSubAdapter
import com.helloyatri.ui.home.bottomsheet.NotificationDetailBottomSheet
import com.helloyatri.utils.extension.enableTextView
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()
    private val notificationList = ArrayList<NotificationsData>()

    private val notificationAdapter by lazy {
        NotificationSubAdapter(onClickListener = {
            NotificationDetailBottomSheet(it, callBack = {
                // TODO: Invoke Read Notification API
            }).show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        })
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initViews()
        setClickListener()
        initObservers()
        apiViewModel.getAllNotificationData()
    }

    private fun initViews() = with(binding) {
        recyclerView.apply {
            adapter = notificationAdapter
        }
    }

    private fun initObservers() {
        apiViewModel.getAllNotificationLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        notificationList.clear()
                        it.data?.let { it ->
                            val response = Gson().fromJson(it, NotificationsResponse::class.java)
                            response?.data?.takeIf { it.isNotEmpty() }?.let {
                                notificationList.addAll(it)
                                setUpData()
                            }
                        }
                        showPlaceholder()
                    }

                    Status.ERROR -> {
                        hideLoader()
                        showPlaceholder()
                    }

                    Status.LOADING -> showLoader()
                }
            }
        }
        apiViewModel.markAllReadNotificationLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    apiViewModel.getAllNotificationData()
                }

                Status.ERROR -> {
                    hideLoader()
                }

                Status.LOADING -> showLoader()
            }
        }
    }

    private fun setUpData() {
        notificationAdapter.setItems(notificationList, 1)
    }

    private fun showPlaceholder() = with(binding) {
        if (notificationList.isEmpty()) {
            textViewPlaceholder.show()
            textViewMarkAllRead.hide()
        } else {
            textViewPlaceholder.hide()
            textViewMarkAllRead.show()
        }
        var isAllMarked = true
        notificationList.forEach {
            if (it.isRead == false) {
                isAllMarked = false
                return@forEach
            }
        }
        binding.textViewMarkAllRead.enableTextView(!isAllMarked)
        binding.textViewMarkAllRead.isClickable = !isAllMarked
        binding.textViewMarkAllRead.isEnabled = !isAllMarked
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setClickListener() = with(binding) {
        textViewMarkAllRead.setOnClickListener {
            apiViewModel.markAllReadNotifications()
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarColor(R.color.backgroundColor)
            .setToolbarTitle(getString(R.string.title_notifications)).build()
    }
}