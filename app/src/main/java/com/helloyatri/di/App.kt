package com.helloyatri.di

import android.app.Application
import com.google.firebase.FirebaseApp
import com.helloyatri.utils.PusherManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    lateinit var pusherManager: PusherManager

    override fun onCreate() {
        super.onCreate()
        pusherManager = PusherManager(this)
        FirebaseApp.initializeApp(this)
    }

}