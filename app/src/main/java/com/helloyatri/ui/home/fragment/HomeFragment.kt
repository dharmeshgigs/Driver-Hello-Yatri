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
import com.helloyatri.data.model.UpdateLocationResponse
import com.helloyatri.databinding.HomeFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.activity.IsolatedActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.ui.home.adapter.AdapterRidesForPickups
import com.helloyatri.utils.AppUtils.fairValue
import com.helloyatri.utils.AppUtils.fareAmount
import com.helloyatri.utils.extension.changeStatusBarColor
import com.helloyatri.utils.extension.enableTextView
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.visible
import com.helloyatri.utils.getRidePickUpList
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    private var isOnline = false
    private val apiViewModel by activityViewModels<ApiViewModel>()

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
    }

    private fun initObservers() {
        apiViewModel.getHomeLiveData.observe(this) { resource ->
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

        apiViewModel.updateCurrentLocationLiveData.observe(this) { resource ->
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

        apiViewModel.updateDriverAvalabilityLiveData.observe(this) { resource ->
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

        apiViewModel.getDriverProfileLiveData.observe(this) { resource ->
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
                    it.totalDistance.nullify("0")
                )
            )
                .setTypeface(R.font.lufga_medium, it.totalDistance.nullify("0"))
                .setAbsoluteSize(
                    resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    it.totalDistance.nullify("0")
                ).build()

            TextDecorator.decorate(
                textViewDuration,
                String.format(
                    getString(R.string.label_dynamic_duration_n02_40_hr),
                    it.totalDuration.nullify("00:00 Hr")
                )
            )
                .setTypeface(R.font.lufga_medium, it.totalDuration.nullify("00:00 Hr"))
                .setAbsoluteSize(
                    resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    it.totalDuration.nullify("00:00 Hr")
                ).build()
            it.acceptanceRatio?.let {
                textViewPercent.text = it.toInt().toString().plus("%")
            } ?: run {
                textViewPercent.text = "0%"
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
        textViewUserName.text = buildString {
            append("Hello \n")
            append(data?.name)
        }
    }

    private fun setAdapter() = with(binding) {
        recyclerViewPickUps.apply {
            adapter = adapterPickUp
            adapterPickUp.setItems(requireActivity().getRidePickUpList(), 1)
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
            navigator.loadActivity(IsolatedActivity::class.java, ScheduleRideFragment::class.java)
                .start()
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

        adapterPickUp.setOnViewItemClickListener { _, view ->
            when (view.id) {
                R.id.textViewNavigateTo -> {
                    navigator.loadActivity(
                        IsolatedActivity::class.java,
                        StartRideFromFragment::class.java
                    ).start()
                }
            }
        }

        textViewIntercityRides.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java, IntercityRideFragment::class.java)
                .start()
        }
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
        getUserCurrentLocation(update = true, onLocation = {
            it?.let {
//                val request = Request(
//                    latitude = "23.0708186",
//                    longitude = "72.5365617"
//                )
                val request = Request(
                    latitude = "21.214776",
                    longitude = "72.8902554"
                )
                // TODO: Remove static latlong
//                val request = Request(
//                    latitude = it.latitude.toString(),
//                    longitude = it.longitude.toString()
//                )
                apiViewModel.updateCurrentLocation(
                    request
                )
            }
        })
    }

    private fun getNearestLatLong() {
//        val apiKey = "YOUR_API_KEY"
//        val latitude = 21.1702 // Example latitude
//        val longitude = 72.8311 // Example longitude
//
//        val url = "https://roads.googleapis.com/v1/snapToRoad?path=${latitude},${longitude}&interpolate=true&key=${apiKey}"
//
//        val request = Request.Builder()
//            .url(url)
//            .build()
//
//        val client = OkHttpClient()
//        val response = client.newCall(request).execute()
//
//        if (response.isSuccessful)
//        {
//            val data = response.body()?.string()
//            // Parse the JSON data and extract the snapped points
//        } else {
//            // Handle errors
//        }
    }
}