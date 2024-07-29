package com.helloyatri.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.Driver
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.databinding.HomeAcitivtyBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.home.dialog.LogoutDialogFragment
import com.helloyatri.ui.home.fragment.AccountAllReviewsFragment
import com.helloyatri.ui.home.fragment.AccountDocumentsFragment
import com.helloyatri.ui.home.fragment.AccountEditProfileFragment
import com.helloyatri.ui.home.fragment.AccountHelpCenterFragment
import com.helloyatri.ui.home.fragment.AccountPaymentFragment
import com.helloyatri.ui.home.fragment.AccountPreferencesFragment
import com.helloyatri.ui.home.fragment.AccountSavedAddressFragment
import com.helloyatri.ui.home.fragment.HomeFragment
import com.helloyatri.ui.home.fragment.RideActivityFragment
import com.helloyatri.ui.home.sidemenu.SideMenu
import com.helloyatri.ui.home.sidemenu.SideMenuAdapter
import com.helloyatri.ui.home.sidemenu.SideMenuTag
import com.helloyatri.utils.Constants.YOUR_APP_CLUSTER
import com.helloyatri.utils.Constants.YOUR_APP_KEY
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpChannelAuthorizer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity() {

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
        load(HomeFragment::class.java).replace(false)
        initObservers()
        setUpPushUp()

//        homeAcitivtyBinding.navigationDrawerContent.imageViewUserProfile.loadImageFromServerWithPlaceHolder(
//                "https://plus.unsplash.com/premium_photo-1669047668540-9e1712e29f1f?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
    }

    private fun setUpPushUp() {
        val map = mutableMapOf<String, String>()
        map["Accept"] = "application/json"
        map["Authorization"] = "Bearer ".plus(appSession.userSession)

        val channelAuthorizer = HttpChannelAuthorizer("http://3.111.159.32/api/pusher/auth")
        channelAuthorizer.setHeaders(map)
        val options = PusherOptions().setCluster(YOUR_APP_CLUSTER).setUseTLS(true)
            .setChannelAuthorizer(channelAuthorizer)
        val pusher = Pusher(YOUR_APP_KEY, options)
        var userId = appSession.user?.id.toString()
        Log.i("TAG", "setUpPushUp: " + userId)
        Log.e("TAG", "Invoked")
        var channelName = "private-driver.$userId"
        val channel = pusher.subscribePrivate(channelName)
        channel.bind(
            "NewRideRequest", pushEvent
        )
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i("TAG", "onConnectionStateChange: " + change.currentState)
                Log.i("TAG", "onConnectionStateChange: " + change.previousState)
                println(
                    "State changed to " + change.currentState +
                            " from " + change.previousState
                )
                if (change.currentState === ConnectionState.CONNECTED) {
                    var channelName = "private-driver.$userId"
                    Log.e("TAG", "setUpPushUp: $channelName")

                }

            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                println("There was a problem connecting!")
                Log.i("TAG", "onError: " + message)
            }
        })
    }

    private fun initObservers() {
        apiViewModel.getDriverProfileLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response =
                        Gson().fromJson(resource.data.toString(), DriverResponse::class.java)
                    setUserData(response.data)
                    Log.i("TAG", "initObservers: " + response.data.toString())
                }

                Status.ERROR -> {

                }

                Status.LOADING -> {

                }
            }

        }
    }

    private fun setUserData(data: Driver?) = with(homeAcitivtyBinding) {
        navigationDrawerContent.imageViewUserProfile.loadImageFromServerWithPlaceHolder(
            data?.profileImage ?: ""
        )
        navigationDrawerContent.textViewUserName.text = buildString {
            append("Hello \n ")
            append(data?.name)
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
            SideMenu(sideMenuName = "Edit Profile", sideMenuTag = SideMenuTag.EDIT_PROFILE)
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
                    load(RideActivityFragment::class.java).replace(true)
                }

                SideMenuTag.PAYMENT -> {
                    load(AccountPaymentFragment::class.java).replace(true)
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

    val pushEvent = object : PrivateChannelEventListener {
        override fun onEvent(event: PusherEvent?) {
            event?.let {
                Log.e("TAG", "onEvent ${Gson().toJson(event)}")
            } ?: run {
                Log.e("TAG", "onEvent null")
            }
        }

        override fun onSubscriptionSucceeded(channelName: String?) {
            Log.e("TAG", "Channel Name $channelName")
        }

        override fun onAuthenticationFailure(
            message: String?,
            e: java.lang.Exception?
        ) {
            e?.let {
                it.printStackTrace()
            }

        }
    }
}