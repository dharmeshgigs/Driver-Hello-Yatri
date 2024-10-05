package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.ActiveTripResponse
import com.helloyatri.data.model.RiderDetails
import com.helloyatri.data.model.TripDetails
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.AllRideStatusFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AllRidesStatusAdapter
import com.helloyatri.ui.home.bottomsheet.EmergencyAssistanceBottomSheet
import com.helloyatri.utils.AppUtils.doubleDefault
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AllRideStatusFragment : BaseFragment<AllRideStatusFragmentBinding>() {
    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var rideStatus: String? = null

    private val allRidesStatusAdapter by lazy {
        AllRidesStatusAdapter("ACTIVE", onEmergencyClick = {
            EmergencyAssistanceBottomSheet(callBack = {
                navigator.load(TripReportCrashFragment::class.java).add(false)
            }).show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        }, onEndHereClick = { position, item ->
            apiViewModel.tripConfigData()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rideStatus = arguments?.getString("status")
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AllRideStatusFragmentBinding {
        return AllRideStatusFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initObservers()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerView.adapter = allRidesStatusAdapter
    }

    private fun initObservers() {
        apiViewModel.getActiveRideLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (resource.status) {
                    com.helloyatri.network.Status.SUCCESS -> {
                        hideProgressBar()
                        allRidesStatusAdapter.setItems(apiViewModel.activeTrips.toList(), 1)
                        showPlaceHolder()
                    }

                    com.helloyatri.network.Status.ERROR -> {
                        hideProgressBar()
                        allRidesStatusAdapter.setItems(emptyList(), 1)
                        showPlaceHolder()
                    }

                    com.helloyatri.network.Status.LOADING -> {
                        showProgressBar()
                    }
                }
            }
        }

        apiViewModel.getActiveTripLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        val response =
                            Gson().fromJson(
                                resource.data.toString(),
                                ActiveTripResponse::class.java
                            )
                        var riderDetails: RiderDetails? = null
                        var tripDetails: TripDetails? = null
                        var tripRiderModel: TripRiderModel? = null
                        response?.data?.trip?.let { trip ->
                            response.data?.EVENTDATA?.let {
                                it.riderDetails?.let { item ->
                                    riderDetails = RiderDetails(
                                        id = item.id,
                                        name = item.name,
                                        profile = item.profile,
                                        paymentType = getString(R.string.dummy_cash_payment),
                                        note = "",
                                        mobile = item.mobile
                                    )
                                }
                                it.tripDetails?.let { item ->
                                    tripDetails = item
                                    tripDetails?.estimatedFareTxt = trip.commonTotalFareTxt
                                    tripDetails?.estimatedFare =
                                        trip.commonTotalFare.doubleDefault("0.0")
                                    tripDetails?.status = trip.status
                                }

                                riderDetails?.let { rd ->
                                    tripDetails?.let { td ->
                                        tripRiderModel = TripRiderModel(
                                            riderDetails = rd,
                                            tripDetails = td,
                                            popupDetails = it.popupDetails
                                        )
                                    }
                                }
                            }
                            tripRiderModel?.let {
                                if (trip.status == "ACTIVE" || trip.status == "ARRIVED") {
                                    response.data?.EVENTDATA?.let {
                                        apiViewModel.tripRequest.value = it
                                        apiViewModel._tripStartLiveData.value = false
                                        navigator.load(PickUpSpotFragment::class.java).replace(true)
                                        apiViewModel.getActiveTripLiveData.value = null
                                    }
                                } else if (trip.status == "FINISHED" && trip.paymentStatus == "PAID") {
                                    response.data?.EVENTDATA?.let {
                                        apiViewModel.tripRequest.value = it
                                        apiViewModel._tripStartLiveData.value = false
                                        navigator.load(RideCompleteFragment::class.java)
                                            .replace(false)
                                        apiViewModel.getActiveTripLiveData.value = null
                                    }
                                } else if(trip.status == "ON_GOING") {
                                    response.data?.EVENTDATA?.let {
                                        apiViewModel.tripRequest.value = it
                                        apiViewModel._tripStartLiveData.value = true
                                        navigator.load(PickUpSpotFragment::class.java).replace(true)
                                        apiViewModel.getActiveTripLiveData.value = null
                                    }
                                } else {

                                }
                            }
                        }
                    }

                    Status.ERROR -> {

                    }

                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun showPlaceHolder() = with(binding) {
        allRidesStatusAdapter.items?.takeIf { it.isNotEmpty() }?.let {
            textViewPlaceholder.gone()
        } ?: run {
            textViewPlaceholder.visible()
        }
        recyclerView.invalidate()
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onResume() {
        super.onResume()
        binding.textViewPlaceholder.gone()
        rideStatus?.let {
            apiViewModel.getActiveRideData(mutableMapOf<String, String>().apply {
                put(Constants.PARAM_FILTER_PARAMETER, "ACTIVE")
            })
        }
    }

    private fun showProgressBar() = with(binding) {
        progressBar.visible()
    }

    private fun hideProgressBar() = with(binding) {
        progressBar.gone()
    }

}