package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.GetCencellation
import com.helloyatri.databinding.FragmentScheduleRideBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.activity.IsolatedActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.ScheduleRideSubAdapter
import com.helloyatri.ui.home.bottomsheet.CancelRideBottomSheet
import com.helloyatri.utils.AppUtils.fareAmount
import com.helloyatri.utils.AppUtils.openCallDialer
import com.helloyatri.utils.extension.nullify
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleRideFragment : BaseFragment<FragmentScheduleRideBinding>() {

    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var cencellationDataList: ArrayList<String> = arrayListOf()
    private val scheduleRideMainAdapter by lazy {
        ScheduleRideSubAdapter()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): FragmentScheduleRideBinding {
        return FragmentScheduleRideBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        apiViewModel.getCancelletionReasonAPI()
        initObservers()
        setUpClickListner()
        setAdapter()
    }

    private fun setUpClickListner() {
//        scheduleRideMainAdapter.setOnItemClickListener {
//            navigator.load(PickUpSpotFragment::class.java).replace(true)
//        }
    }

    private fun initObservers() {
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

        apiViewModel.cancleRideScheduleLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        apiViewModel.cancleRideScheduleLiveData.value = null
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message ?: getString(resource.resId!!)
                        showErrorMessage(error)
                        apiViewModel.cancleRideScheduleLiveData.value = null
                    }

                    Status.LOADING -> {
                        showLoader()
                    }
                }
            }
        }
    }

    private fun setAdapter() = with(binding) {
        recyclerView.apply {
            adapter = scheduleRideMainAdapter
            scheduleRideMainAdapter.setItems(apiViewModel.scheduleTrips, 1)
            scheduleRideMainAdapter.setOnViewItemClickListener { item, view ->
                when (view.id) {
                    R.id.textViewNavigateTo -> {
                        navigator.loadActivity(
                            IsolatedActivity::class.java,
                            StartRideFromFragment::class.java
                        ).start()
                    }
                    R.id.textViewCancelRide -> {
                        CancelRideBottomSheet({
                            activity?.apply {
                                apiViewModel.cancelRide(
                                    Request(
                                        trip_id = item.id.fareAmount(),
                                        cancel_reason = it
                                    ),
                                    "Schedule"
                                )
                            }
                        }, cencellationDataList).show(
                            childFragmentManager, HomeFragment::class.java.simpleName
                        )
                    }
                    R.id.imageViewCall -> {
                        activity?.openCallDialer(item.user?.mobile.nullify())
                    }
                }
            }
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true).setToolbarColor(R.color.backgroundColor)
            .setToolbarTitle(getString(R.string.titleschedule_ride)).build()
    }
}