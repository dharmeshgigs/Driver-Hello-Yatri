package com.helloyatri.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.helloyatri.core.AppPreferences
import com.helloyatri.utils.PusherManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application(), LifecycleObserver {
    lateinit var pusherManager: PusherManager

    override fun onCreate() {
        super.onCreate()
        pusherManager = PusherManager(this)
        FirebaseApp.initializeApp(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(
                activity:
                Activity
            ) {
                val appPreferences: AppPreferences = AppPreferences(activity)
                appPreferences.putBoolean("isAppInForeground",true)
//                Toast.makeText(activity, "In Foreground", Toast.LENGTH_SHORT).show()
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                val appPreferences: AppPreferences = AppPreferences(activity)
                appPreferences.putBoolean("isAppInForeground",false)
//                Toast.makeText(activity, "In Background", Toast.LENGTH_SHORT).show()
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    fun isInForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        //App in background
    }
}