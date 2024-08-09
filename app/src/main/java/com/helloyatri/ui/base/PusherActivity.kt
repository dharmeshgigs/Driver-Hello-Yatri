package com.helloyatri.ui.base

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.helloyatri.core.Session
import com.helloyatri.data.Request
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.di.App
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.home.dialog.RequestRideDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class PusherActivity : AppCompatActivity() {
    lateinit var myApp: App

    @Inject
    lateinit var appSession: Session

    private val apiViewModel by viewModels<ApiViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApp = application as App
        pusherConnection()
        initObservers()

    }

    private fun initObservers() {

        apiViewModel.acceptRequestLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    Log.i("TAG", "initObservers:accept ")
                }
                Status.ERROR -> {
                    Log.i("TAG", "initObservers: "+resource.message)
                }
                Status.LOADING -> {}
            }
        }

        apiViewModel.declineRequestLiveData.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    Log.i("TAG", "initObservers:decline ")
                }

                Status.ERROR -> {
                    Log.i("TAG", "initObservers: "+resource.message)
                }
                Status.LOADING -> {}
            }
        }
    }

    private fun pusherConnection() {
        if (appSession.isLoggedIn == true) {
            myApp.pusherManager.initializePusher(
                appSession
                    .user?.id.toString() ?: "",
                appSession.userSession
            )

        } else {
            Log.i("TAG", "pusherConnection: " + appSession.isLoggedIn)
        }

    }

    fun openTripRequestDialog(tripRiderModel: TripRiderModel) {
        showRequestDialog(tripRiderModel)
    }

    private fun showRequestDialog(tripRiderModel: TripRiderModel) {
        Log.i("TAG", "showRequestDialog: "+tripRiderModel.toString())
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            RequestRideDialogFragment(acceptCallBack = {
                apiViewModel.acceptRequestAPI(Request(trip_id = tripRiderModel.tripDetails?.id.toString()))
                Log.i("TAG", "showRequestDialog:accept ")
            }, declineCallBack ={
                apiViewModel.declineRequestAPI(Request(trip_id = tripRiderModel.tripDetails?.id.toString()))
                Log.i("TAG", "showRequestDialog:decline ")
            },tripRiderModel).show(supportFragmentManager, PusherActivity::class.java.simpleName)
//            RequestRideDialogFragment {
////                navigator.loadActivity(IsolatedActivity::class.java, PickUpSpotFragment::class.java)
////                    .start()
//                ,
//
//            }.show(supportFragmentManager, PusherActivity::class.java.simpleName)
        }
    }



}