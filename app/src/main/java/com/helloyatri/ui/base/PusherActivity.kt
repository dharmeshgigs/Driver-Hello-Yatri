package com.helloyatri.ui.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.helloyatri.R
import com.helloyatri.core.Session
import com.helloyatri.di.App
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class PusherActivity : AppCompatActivity() {
    private lateinit var myApp: App
    @Inject
    lateinit var appSession: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApp = application as App
        pusherConnection()
    }

    private fun pusherConnection() {
        if (appSession.isLoggedIn == true) {
            myApp.pusherManager.initializePusher(
                appSession
                    .user?.id.toString() ?: "",
                appSession.userSession
            )
        }else{
            Log.i("TAG", "pusherConnection: "+appSession.isLoggedIn)
        }
    }

}