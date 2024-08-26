package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.GetCencellation
import com.helloyatri.databinding.FragmentNotificationBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.ScheduleRideMainAdapter
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.getScheduleRideList
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class ScheduleRideFragment : BaseFragment<FragmentNotificationBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    private val scheduleRideMainAdapter by lazy {
        ScheduleRideMainAdapter()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(layoutInflater)
    }

    private var cencellationDataList: ArrayList<String> = arrayListOf()

    override fun bindData() {
        binding.textViewMarkAllRead.hide()
        apiViewModel.getAllScheduleRideAPI()
        apiViewModel.getCancelletionReasonAPI()
        initObservers()
        setUpClickListner()

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: MessageEvent) {
//        if (event.message == "Cancle_Ride") {
//            val cancelRideBottomSheet = CancelRideBottomSheet(cancelRideCallBack = {
//
//            }, cencellationDataList)
//            cancelRideBottomSheet.show(childFragmentManager, cancelRideBottomSheet.tag)
//        } else if (event.message == "Navigate_To") {
//            navigator.load(PickUpSpotFragment::class.java).replace(false)
//        }
//
//    }

    private fun setUpClickListner() {
//        scheduleRideMainAdapter.setOnItemClickListener {
//            navigator.load(PickUpSpotFragment::class.java).replace(true)
//        }
    }

    private fun initObservers() {
        apiViewModel.getAllScheduleRideLiveData.observe(this) { resourse ->
            when (resourse.status) {
                Status.SUCCESS -> {
                    hideLoader()
                }

                Status.ERROR -> {
                    hideLoader()
                    setAdapter()
                }

                Status.LOADING -> showLoader()
            }
        }

        apiViewModel.getCanclletionReasonLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    val response =
                        Gson().fromJson(resource.data.toString(), GetCencellation::class.java)
                    cencellationDataList.clear()
                    cencellationDataList.addAll(response.data)
                }

                Status.ERROR -> {
                    hideLoader()
                }

                Status.LOADING -> showLoader()
            }
        }
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

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }
}