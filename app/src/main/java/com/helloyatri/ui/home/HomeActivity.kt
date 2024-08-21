package com.helloyatri.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.network.Status
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import com.helloyatri.data.model.DriverResponse
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.HomeAcitivtyBinding
import com.helloyatri.network.ApiViewModel
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
import com.helloyatri.utils.PushEventListener
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : BaseActivity(),PushEventListener {

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
    }

    private fun initObservers() {
        apiViewModel.getDriverProfileLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response =
                        Gson().fromJson(resource.data.toString(), DriverResponse::class.java)
                    setUserData(response.data)
                }

                Status.ERROR -> {

                }

                Status.LOADING -> {

                }
            }
        }
        apiViewModel.acceptRequestLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    load(PickUpSpotFragment::class.java).replace(true)
                }

                Status.ERROR -> {
                }

                Status.LOADING -> {}
            }
        }
        apiViewModel.declineRequestLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                }

                Status.ERROR -> {
                }

                Status.LOADING -> {}
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
                    load(RideActivityFragment::class.java).add(true)
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

    override fun onEvent(data: JsonObject) {
        try {
            val response =
                Gson().fromJson(data.toString(), TripRiderModel::class.java)
            response?.let {
                showRequestDialog(it)
                apiViewModel.tripRequest.postValue(it)
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
}