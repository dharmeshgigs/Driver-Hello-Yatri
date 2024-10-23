package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.data.model.GetCencellation
import com.helloyatri.data.model.UpdateLocationResponse
import com.helloyatri.databinding.HomeFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.activity.IsolatedActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.ui.home.adapter.AdapterRidesForPickups
import com.helloyatri.ui.home.bottomsheet.CancelRideBottomSheet
import com.helloyatri.utils.AppUtils.fairValue
import com.helloyatri.utils.AppUtils.fareAmount
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.changeStatusBarColor
import com.helloyatri.utils.extension.enableTextView
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.visible
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    private var isOnline = false
    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var cencellationDataList: ArrayList<String> = arrayListOf()

    private val adapterPickUp by lazy {
        AdapterRidesForPickups()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(requireContext(), R.color.backgroundColor), true
        )
        initObservers()
        getHomeDataAPI()
        setClickListener()
        setAdapter()
        apiViewModel.getCancelletionReasonAPI()
    }

    private fun initObservers() {
        apiViewModel.getHomeLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
//                    resource.data?.let {
//                        val response =
//                            Gson().fromJson(it, GetHomeDataModel::class.java)
                    setData()
//                    }
                }

                Status.ERROR -> {
                    hideLoader()
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> showLoader()
            }
        }

        apiViewModel.updateCurrentLocationLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let { it ->
                when (resource.status) {
                    Status.SUCCESS -> {
//                        hideLoader()
                        it.data?.let { it ->
                            val response =
                                Gson().fromJson(it, UpdateLocationResponse::class.java)
                            response?.data?.address?.let {
                                binding.apply {
                                    textViewCurrentLocation.text = it
                                }
                            }
                        }
                    }

                    Status.ERROR -> {
//                        hideLoader()
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }

                    Status.LOADING -> {
//                        showLoader()
                    }
                }
            }

        }

        apiViewModel.updateDriverAvalabilityLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    changeStatus()
                }

                Status.ERROR -> {
                    val error =
                        resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                    showErrorMessage(error)
                }

                Status.LOADING -> hideLoader()
            }
        }

        apiViewModel.getDriverProfileLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response =
                        Gson().fromJson(resource.data.toString(), DriverResponse::class.java)
                    setUserData(response.data)
                }

                Status.ERROR -> {}

                Status.LOADING -> {}
            }

        }

//        apiViewModel.getActiveTripLiveData.observe(viewLifecycleOwner) { resource ->
//            resource?.let {
//                when (resource.status) {
//                    Status.SUCCESS -> {
//                        val response =
//                            Gson().fromJson(
//                                resource.data.toString(),
//                                ActiveTripResponse::class.java
//                            )
//                        var riderDetails: RiderDetails? = null
//                        var tripDetails: TripDetails? = null
//                        var tripRiderModel: TripRiderModel? = null
//                        response?.data?.trip?.let { trip ->
//                            response.data?.EVENTDATA?.let {
//                                it.riderDetails?.let { item ->
//                                    riderDetails = RiderDetails(
//                                        id = item.id,
//                                        name = item.name,
//                                        profile = item.profile,
//                                        paymentType = getString(R.string.dummy_cash_payment),
//                                        note = "",
//                                        mobile = item.mobile
//                                    )
//                                }
//                                it.tripDetails?.let { item ->
//                                    tripDetails = item
//                                    tripDetails?.estimatedFareTxt = trip.commonTotalFareTxt
//                                    tripDetails?.estimatedFare =
//                                        trip.commonTotalFare.doubleDefault("0.0")
//                                    tripDetails?.status = trip.status
//                                }
//
//                                riderDetails?.let { rd ->
//                                    tripDetails?.let { td ->
//                                        tripRiderModel = TripRiderModel(
//                                            riderDetails = rd,
//                                            tripDetails = td,
//                                            popupDetails = it.popupDetails
//                                        )
//                                    }
//                                }
//                            }
//                            tripRiderModel?.let {
//                                if (trip.status == "ACTIVE" || trip.status == "ARRIVED") {
//                                    response.data?.EVENTDATA?.let {
//                                        apiViewModel.tripRequest.value = it
//                                        apiViewModel._tripStartLiveData.value = false
//                                        navigator.load(PickUpSpotFragment::class.java).replace(true)
//                                        apiViewModel.getActiveTripLiveData.value = null
//                                    }
//                                } else if (trip.status == "FINISHED" && trip.paymentStatus == "PAID") {
//                                    response.data?.EVENTDATA?.let {
//                                        apiViewModel.tripRequest.value = it
//                                        apiViewModel._tripStartLiveData.value = true
//                                        navigator.load(RideCompleteFragment::class.java)
//                                            .replace(false)
//                                        apiViewModel.getActiveTripLiveData.value = null
//                                    }
//                                } else if (trip.status == "ON_GOING") {
//                                    response.data?.EVENTDATA?.let {
//                                        apiViewModel.tripRequest.value = it
//                                        apiViewModel._tripStartLiveData.value = true
//                                        navigator.load(PickUpSpotFragment::class.java).replace(true)
//                                        apiViewModel.getActiveTripLiveData.value = null
//                                    }
//                                } else {
//
//                                }
//                            }
//                        }
//                    }
//
//                    Status.ERROR -> {
//
//                    }
//
//                    Status.LOADING -> {
//
//                    }
//                }
//            }
//        }

        apiViewModel.locationLiveData.observe(this) {
            it?.let {
                val request = Request(
                    latitude = it.latitude.toString(),
                    longitude = it.longitude.toString()
                )
                apiViewModel.updateCurrentLocation(request = request)
            }
        }

        apiViewModel.getScheduleTripLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        if(apiViewModel.scheduleTrips.size > 0) {
                            binding.textViewScheduledPickups.visible()
                            binding.recyclerViewPickUps.visible()
                            binding.textViewLabelViewAll.visible()
                            if(apiViewModel.scheduleTrips.size > 3) {
                                adapterPickUp.setItems(apiViewModel.scheduleTrips.subList(0,3),1)
                            } else {
                                adapterPickUp.setItems(apiViewModel.scheduleTrips,1)
                            }
                        } else {
                            binding.textViewScheduledPickups.gone()
                            binding.recyclerViewPickUps.gone()
                            binding.textViewLabelViewAll.gone()
                        }
                    }
                    else -> {
                        binding.textViewScheduledPickups.gone()
                        binding.recyclerViewPickUps.gone()
                        binding.textViewLabelViewAll.gone()
                    }
                }
            }
        }

        apiViewModel.getCanclletionReasonLiveData.observe(this) { resource ->
            resource?.let {
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

        apiViewModel.cancleRideHomeLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        apiViewModel.cancleRideHomeLiveData.value = null
                        getScheduleTrips()
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message ?: getString(resource.resId!!)
                        showErrorMessage(error)
                        apiViewModel.cancleRideHomeLiveData.value = null
                    }

                    Status.LOADING -> {
                        showLoader()
                    }
                }
            }
        }

        apiViewModel.scheduleTripAccepted.observe(this) {
            if (it == true) {
                getScheduleTrips()
            }
        }
    }

    private fun setData() = with(binding) {
        apiViewModel.homeData?.let {
            textViewDateAndTime.text = it.todayDate.nullify()
            textViewPrice.text = it.totalEarn.nullify(getString(R.string.label_currency).plus(" 0"))

            TextDecorator.decorate(
                textViewRide,
                String.format(getString(R.string.label_dynamic_ride_n3), it.totalRides.fareAmount())
            )
                .setTypeface(R.font.lufga_medium, it.totalRides.fareAmount())
                .setAbsoluteSize(
                    resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    it.totalRides.fareAmount()
                ).build()

            TextDecorator.decorate(
                textViewDistance,
                String.format(
                    getString(R.string.label_dynamic_distance_n105_5_km),
                    it.totalDistance.nullify(Constants.DEFAULT_DISTANCE)
                )
            )
                .setTypeface(
                    R.font.lufga_medium,
                    it.totalDistance.nullify(Constants.DEFAULT_DISTANCE)
                )
                .setAbsoluteSize(
                    resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    it.totalDistance.nullify(Constants.DEFAULT_DISTANCE)
                ).build()

            TextDecorator.decorate(
                textViewDuration,
                String.format(
                    getString(R.string.label_dynamic_duration_n02_40_hr),
                    it.totalDuration.nullify(Constants.DEFAULT_HOURS)
                )
            )
                .setTypeface(R.font.lufga_medium, it.totalDuration.nullify(Constants.DEFAULT_HOURS))
                .setAbsoluteSize(
                    resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    it.totalDuration.nullify(Constants.DEFAULT_HOURS)
                ).build()
            it.acceptanceRatio?.let {
                textViewPercent.text = it.toInt().toString().plus("%")
                roundSeekBar.progress = it.toLong()
            } ?: run {
                textViewPercent.text = "0%"
                roundSeekBar.progress = 0L
            }

            textViewLabelTotalTripsWaiting.text = String.format(
                getString(R.string.label_dummy_20_trips_are_in_waiting),
                it.waitingTripsCount.fairValue("0")
            )
            it.rideRequest?.let {
                textViewYouAreOffline.text =
                    it.subTitle ?: getString(R.string.label_you_are_offline)
                textViewLabelTurnOnAndFindRide.text =
                    it.title ?: getString(R.string.label_turn_on_go_online_to_find_ride_request)
            }
            it?.let {
                it.driverAvailabilityStatus?.let {
                    isOnline = it == 1
                }
                textViewOnlineOfflineStatus.text = it.driverAvailabilityStatusBtnLbl
            }
            if (isOnline) {
                textViewRideRequest.gone()
                constraintLayoutRideRequest.gone()
            } else {
                textViewRideRequest.visible()
                constraintLayoutRideRequest.visible()
            }
            textViewOnlineOfflineStatus.enableTextView(!isOnline)
            if (activity is HomeActivity) {
                (activity as HomeActivity).setDrawerData()
            }
        }
    }

    private fun setUserData(data: Driver?) = with(binding) {
        imageViewUserProfile.loadImageFromServerWithPlaceHolder(
            data?.profileImage ?: ""
        )
        TextDecorator.decorate(
            textViewUserName,
            String.format(getString(R.string.label_hello_home), data?.name)
        )
            .setTypeface(R.font.lufga_medium, data?.name.nullify())
            .setAbsoluteSize(
                resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                data?.name.nullify()
            ).build()
    }

    private fun setAdapter() = with(binding) {
        recyclerViewPickUps.apply {
            adapter = adapterPickUp
            adapterPickUp.setItems(arrayListOf(), 1)
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }
    }

    private fun setClickListener() = with(binding) {
        imageViewUserProfile.setOnClickListener {
            (activity as HomeActivity).openDrawer()
        }

        textViewOnlineOfflineStatus.setOnClickListener {
//            if(activity is BaseActivity) {
//                (activity as BaseActivity).myApp.pusherManager.triggerDriverLocationEvent()
//            }
            isOnline = !isOnline
            apiViewModel.updateDriverAvalability(Request(availability = (if (isOnline) "0" else "1")))
        }

        textViewLabelViewAll.setOnClickListener {
            navigator.load(ScheduleRideFragment::class.java)
                .replace(true)
        }

        imageViewNotification.setOnClickListener {
//            (activity as BaseActivity).myApp?.pusherManager?.triggerDriverLocationEvent()
//            (activity as BaseActivity).sendNotification("Test Test", "test body")
            navigator.load(NotificationFragment::class.java).replace(true)


        }

        textViewRideRequestOnlineOfflineStatus.setOnClickListener {
//            changeStatus()
//            textViewOnlineOfflineStatus.backgroundTintList =
//                ContextCompat.getColorStateList(requireContext(), R.color.grey)
//            textViewOnlineOfflineStatus.text = getString(R.string.label_go_offline)
//            textViewRideRequest.hide()
//            constraintLayoutRideRequest.hide()
        }

        adapterPickUp.setOnViewItemClickListener { item, view ->
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
                                ),"home"
                            )
                        }
                    }, cencellationDataList).show(
                        childFragmentManager, HomeFragment::class.java.simpleName
                    )
                }
            }
        }

//        textViewIntercityRides.setOnClickListener {
//            navigator.loadActivity(IsolatedActivity::class.java, IntercityRideFragment::class.java)
//                .start()
//        }
    }


    private fun changeStatus() = with(binding) {
        if (isOnline) {
//            showRequestDialog()
            textViewOnlineOfflineStatus.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grey)
            textViewOnlineOfflineStatus.text = getString(R.string.label_go_offline)
            textViewRideRequest.hide()
            constraintLayoutRideRequest.hide()
        } else {
            textViewOnlineOfflineStatus.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.homeBgBlueColor)
            textViewOnlineOfflineStatus.text = getString(R.string.label_go_online)
            textViewRideRequest.show()
            constraintLayoutRideRequest.show()
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    private fun getHomeDataAPI() {
        apiViewModel.getHomeData()
        apiViewModel.getDriverProfile()
    }

    override fun onResume() {
        super.onResume()
//        apiViewModel.tripConfigData()
        getScheduleTrips()
    }


    fun getScheduleTrips() {
        apiViewModel.getScheduleTrips()
    }
}