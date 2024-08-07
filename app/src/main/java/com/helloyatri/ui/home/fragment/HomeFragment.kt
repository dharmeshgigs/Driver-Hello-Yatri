package com.helloyatri.ui.home.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import com.helloyatri.network.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.data.model.GetHomeDataModel
import com.helloyatri.data.model.HomeDataModel
import com.helloyatri.data.model.UpdateLocationResponse
import com.helloyatri.databinding.HomeFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.activity.IsolatedActivity
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.ui.home.adapter.AdapterRidesForPickups
import com.helloyatri.ui.home.dialog.RequestRideDialogFragment
import com.helloyatri.utils.extension.changeStatusBarColor
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.getRidePickUpList
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    private var isOnline = false
    private val apiViewModel by viewModels<ApiViewModel>()

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
        setTextDecorator()
        getHomeDataAPI()
        setUpUi()
        setClickListener()
        setAdapter()
    }

    private fun initObservers() {
        apiViewModel.getHomeLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    hideLoader()
                    resource.data?.let {
                        val response =
                            Gson().fromJson(it, GetHomeDataModel::class.java)
                        setData(response.data)
                    }
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
                        hideLoader()
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
                        hideLoader()
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }

                    Status.LOADING -> showLoader()
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

    private fun setData(data: HomeDataModel?) = with(binding){
        textViewOnlineOfflineStatus.text = data?.driverAvailabilityStatusBtnLbl
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

    private fun showRequestDialog() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            RequestRideDialogFragment {
                navigator.loadActivity(IsolatedActivity::class.java, PickUpSpotFragment::class.java)
                    .start()
            }.show(childFragmentManager, HomeFragment::class.java.simpleName)
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
            isOnline = !isOnline
            apiViewModel.updateDriverAvalability(Request(availability = (if (isOnline) "0" else "1")))
        }

        textViewLabelViewAll.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java, ScheduleRideFragment::class.java)
                .start()
        }

        imageViewNotification.setOnClickListener {
            (activity as BaseActivity).sendNotification("Test Test","test body")
            navigator.load(NotificationFragment::class.java).replace(true)

        }

        textViewRideRequestOnlineOfflineStatus.setOnClickListener {
            isOnline = !isOnline
            changeStatus()
            textViewOnlineOfflineStatus.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grey)
            textViewOnlineOfflineStatus.text = getString(R.string.label_go_offline)
            textViewRideRequest.hide()
            constraintLayoutRideRequest.hide()
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

    private fun setUpUi() = with(binding) {
        imageViewUserProfile.loadImageFromServerWithPlaceHolder(
            "https://plus.unsplash.com/premium_photo-1669047668540-9e1712e29f1f?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        )
    }

    private fun setTextDecorator() = with(binding) {
        TextDecorator.decorate(textViewUserName, textViewUserName.trimmedText)
            .setTypeface(R.font.lufga_medium, getString(R.string.dummy_username_value))
            .setAbsoluteSize(
                resources.getDimensionPixelSize(R.dimen._14ssp),
                getString(R.string.dummy_username_value)
            ).build()

        TextDecorator.decorate(textViewRide, textViewRide.trimmedText)
            .setTypeface(R.font.lufga_medium, "3")
            .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "3").build()

        TextDecorator.decorate(textViewDistance, textViewDistance.trimmedText)
            .setTypeface(R.font.lufga_medium, "105.5 Km")
            .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "105.5 Km")
            .build()

        TextDecorator.decorate(textViewDuration, textViewDuration.trimmedText)
            .setTypeface(R.font.lufga_medium, "02:40 Hr")
            .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "02:40 Hr")
            .build()
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getHomeDataAPI() {
        apiViewModel.getHomeData()
        apiViewModel.getDriverProfile()
        getUserCurrentLocation {
            if (it != null) {
                apiViewModel.updateCurrentLocation(
                    Request(
                        latitude = "21.740520",
                        longitude = "72.148827"
                    )
//                            Request(
//                        latitude = it.latitude.toString(),
//                        longitude = it.longitude.toString()
//                    )
                )
            }
        }
    }
}