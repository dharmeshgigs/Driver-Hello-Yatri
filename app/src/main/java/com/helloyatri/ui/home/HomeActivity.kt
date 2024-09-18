package com.helloyatri.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.data.model.PopUp
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.HomeAcitivtyBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.home.dialog.LogoutDialogFragment
import com.helloyatri.ui.home.dialog.RequestRideDialogFragment
import com.helloyatri.ui.home.fragment.AccountAllReviewsFragment
import com.helloyatri.ui.home.fragment.AccountDocumentsFragment
import com.helloyatri.ui.home.fragment.AccountEditProfileFragment
import com.helloyatri.ui.home.fragment.AccountHelpCenterFragment
import com.helloyatri.ui.home.fragment.AccountPaymentFragment
import com.helloyatri.ui.home.fragment.AccountPreferencesFragment
import com.helloyatri.ui.home.fragment.AccountSavedAddressFragment
import com.helloyatri.ui.home.fragment.HomeFragment
import com.helloyatri.ui.home.fragment.PickUpSpotFragment
import com.helloyatri.ui.home.fragment.RideActivityFragment
import com.helloyatri.ui.home.sidemenu.SideMenu
import com.helloyatri.ui.home.sidemenu.SideMenuAdapter
import com.helloyatri.ui.home.sidemenu.SideMenuTag
import com.helloyatri.utils.AppUtils.fareAmount
import com.helloyatri.utils.PushEventListener
import com.helloyatri.utils.PusherManager
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : BaseActivity(), PushEventListener {

    private lateinit var homeAcitivtyBinding: HomeAcitivtyBinding
    private val apiViewModel by viewModels<ApiViewModel>()

    private val sideMenuAdapter by lazy {
        SideMenuAdapter()
    }

    private val sideMenuList = ArrayList<SideMenu>()

    override fun findFragmentPlaceHolder(): Int {
        return R.id.placeHolder
    }

    override fun createViewBinding(): View {
        homeAcitivtyBinding = HomeAcitivtyBinding.inflate(layoutInflater)
        return homeAcitivtyBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        setUpSideMenu()
        setUpSideMenuClickListener()
        initObservers()
        load(HomeFragment::class.java).replace(false)
        myApp.pusherManager.setPushEventListener(this)
        createFirebaseToken()
    }

    private fun initObservers() {
        apiViewModel.getDriverProfileLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response =
                        Gson().fromJson(resource.data.toString(), DriverResponse::class.java)
                    setUserData(response.data)
                }

                else -> {}
            }
        }
        apiViewModel.acceptRequestLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    load(PickUpSpotFragment::class.java).add(true)
                }

                else -> {}
            }
        }
        apiViewModel.declineRequestLiveData.observe(this) {}
        apiViewModel.firebaseTokenLiveData.observe(this) { }
    }

    fun setUserData(data: Driver?) = with(homeAcitivtyBinding) {
        navigationDrawerContent.imageViewUserProfile.loadImageFromServerWithPlaceHolder(
            data?.profileImage
        )
        navigationDrawerContent.textViewUserName.text = buildString {
            append("Hello \n")
            append(data?.name)
        }
    }

    fun setDrawerData() = with(homeAcitivtyBinding) {
        navigationDrawerContent.textViewCoins.text = "0"

        apiViewModel.homeData?.let {

            navigationDrawerContent.textViewPrice.text =
                it.totalEarn.nullify(getString(R.string.label_currency).plus(" 0"))
            it.averageReviewRating?.let {
                navigationDrawerContent.textViewRatings.text = it.toString()
            } ?: run {
                navigationDrawerContent.textViewRatings.text = "0.0"
            }
            TextDecorator.decorate(
                navigationDrawerContent.textViewRide,
                String.format(getString(R.string.label_dynamic_ride_n3), it.totalRides.fareAmount())
            )
                .setTypeface(R.font.lufga_medium, it.totalRides.fareAmount())
                .setAbsoluteSize(
                    resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    it.totalRides.fareAmount()
                ).build()

            TextDecorator.decorate(
                navigationDrawerContent.textViewDistance,
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
                navigationDrawerContent.textViewDuration,
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
        }
    }

    fun openDrawer() {
        hideKeyboard()
        homeAcitivtyBinding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        homeAcitivtyBinding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun setUpSideMenu() = with(homeAcitivtyBinding) {
        navigationDrawerContent.recyclerViewSideMenu.apply {
            adapter = sideMenuAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }
        sideMenuList.clear()
        sideMenuList.add(
            SideMenu(
                sideMenuName = getString(R.string.menu_item_edit_profile),
                sideMenuTag = SideMenuTag.EDIT_PROFILE
            )
        )
        sideMenuList.add(
            SideMenu(sideMenuName = "Ride Activity", sideMenuTag = SideMenuTag.RIDE_ACTIVITY)
        )
        sideMenuList.add(SideMenu(sideMenuName = "Payment", sideMenuTag = SideMenuTag.PAYMENT))
        sideMenuList.add(
            SideMenu(sideMenuName = "Saved Address", sideMenuTag = SideMenuTag.SAVED_ADDRESS)
        )
        sideMenuList.add(
            SideMenu(sideMenuName = "Preferences", sideMenuTag = SideMenuTag.PREFERENCES)
        )
        sideMenuList.add(
            SideMenu(sideMenuName = "Help Center", sideMenuTag = SideMenuTag.HELP_CENTER)
        )
        sideMenuList.add(SideMenu(sideMenuName = "Documents", sideMenuTag = SideMenuTag.DOCUMENTS))
        sideMenuList.add(SideMenu(sideMenuName = "Logout", sideMenuTag = SideMenuTag.LOGOUT))
        sideMenuAdapter.setItems(sideMenuList, 1)
    }

    private fun setUpSideMenuClickListener() = with(homeAcitivtyBinding) {
        navigationDrawerContent.imageViewCancel.setOnClickListener {
            closeDrawer()
        }

        navigationDrawerContent.textViewRatings.setOnClickListener {
            load(AccountAllReviewsFragment::class.java).replace(true)
            closeDrawer()
        }

        sideMenuAdapter.setOnItemClickListener {
            when (it.sideMenuTag) {
                SideMenuTag.EDIT_PROFILE -> {
                    load(AccountEditProfileFragment::class.java).replace(true)
                }

                SideMenuTag.RIDE_ACTIVITY -> {
                    load(RideActivityFragment::class.java).add(true)
                }

                SideMenuTag.PAYMENT -> {
                    load(AccountPaymentFragment::class.java).add(true)
                }

                SideMenuTag.SAVED_ADDRESS -> {
                    load(AccountSavedAddressFragment::class.java).replace(true)
                }

                SideMenuTag.PREFERENCES -> {
                    load(AccountPreferencesFragment::class.java).replace(true)
                }

                SideMenuTag.HELP_CENTER -> {
                    load(AccountHelpCenterFragment::class.java).replace(true)
                }

                SideMenuTag.DOCUMENTS -> {
                    load(AccountDocumentsFragment::class.java).replace(true)
                }

                SideMenuTag.LOGOUT -> {
                    val logoutDialogFragment = LogoutDialogFragment()
                    logoutDialogFragment.show(supportFragmentManager, logoutDialogFragment.tag)
                }
            }
            closeDrawer()
        }
    }

    private fun setUpToolbar() = with(homeAcitivtyBinding) {
        setToolbar(customToolbar)
        showToolbar(false)
    }

    override fun onResume() {
        super.onResume()
        getProfileAPI()
    }

    private fun getProfileAPI() {
        apiViewModel.getDriverProfile()
    }

    override fun onEvent(data: JsonObject, eventName: String) {
        try {
            val response = Gson().fromJson(data.toString(), TripRiderModel::class.java)
            response?.let {
                when (eventName) {
                    PusherManager.TRIP_UPDATED -> {
                        if (it.tripDetails?.id == apiViewModel.tripRequest.value?.tripDetails?.id) {
                            apiViewModel._pickupNoteLiveData.postValue(
                                it.tripDetails?.pickup_note ?: "-"
                            )
                        }
                    }

                    PusherManager.YOUR_EVENT_NAME -> {
                        it.let {
                            apiViewModel.tripRequest.postValue(null)
                            apiViewModel._pickupNoteLiveData.postValue(null)
                            apiViewModel._paymentCollectedLiveData.postValue(null)
                            apiViewModel._tripStatusUpdatedLiveData.postValue(null)
                            if (it.tripDetails?.driverId == appSession.user?.id) {
                                showRequestDialog(it)
                                apiViewModel.tripRequest.postValue(it)
                            }
                        }
                    }

                    PusherManager.PAYMENT_COMPLETED -> {
                        if (it.tripDetails?.id == apiViewModel.tripRequest.value?.tripDetails?.id) {
                            it.let {
                                apiViewModel._paymentCollectedLiveData.postValue(
                                    it
                                )
                            }
                        }
                    }

                    PusherManager.TRIP_STATUS_UPDATED -> {
                        if (it.tripDetails?.id == apiViewModel.tripRequest.value?.tripDetails?.id) {
                            val popUP = PopUp(
                                POPUPTITLE = it.popupDetails?.title,
                                POPUPMESSAGE = it.popupDetails?.description
                            )
                            apiViewModel.popUp = popUP
                            apiViewModel._tripStatusUpdatedLiveData.postValue(
                                it
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showRequestDialog(tripRiderModel: TripRiderModel) {
        CoroutineScope(Dispatchers.IO).launch {
            RequestRideDialogFragment(acceptCallBack = {
                apiViewModel.acceptRequestAPI(Request(trip_id = tripRiderModel.tripDetails?.id.toString()))
            }, declineCallBack = {
                apiViewModel.declineRequestAPI(Request(trip_id = tripRiderModel.tripDetails?.id.toString()))
            }, tripRiderModel).show(supportFragmentManager, BaseActivity::class.java.simpleName)
        }
    }

    private fun createFirebaseToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM_TOKEN", task.isSuccessful.toString())
                    return@OnCompleteListener
                }
                //Get new Instance ID token
                val token = task.result
                Log.e("FCM_TOKEN", token)
                try {
                    if (appSession.deviceToken != token) {
                        appSession.deviceToken = token
                        apiViewModel.updateFirebaseToken(
                            Request(
                                firebase = token
                            )
                        )
                    }
                    apiViewModel.updateFirebaseToken(
                        Request(
                            firebase = token
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}