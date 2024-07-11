package com.helloyatri.utils.location

import android.app.Activity
import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class PermissionUtilNew(private val activity: ComponentActivity, fragment: Fragment? = null) {

    private var onPermissionGranted: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null
    private var onPermissionDeniedForever: (() -> Unit)? = null

    private var onGpsPermissionEnabled: (() -> Unit)? = null
    private var onGpsPermissionDisabled: (() -> Unit)? = null

    private val permissionLauncher: ActivityResultLauncher<Array<String>>
    private val gpsLauncher: ActivityResultLauncher<IntentSenderRequest>

    init {
        permissionLauncher = (fragment
                ?: activity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                checkAllPermission(it) -> {
                    onPermissionGranted?.invoke()
                }

                !deniedForever(it) -> {
                    onPermissionDeniedForever?.invoke()
                }

                else -> {
                    onPermissionDenied?.invoke()
                }
            }
        }

        gpsLauncher = (fragment
                ?: activity).registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        onGpsPermissionEnabled?.invoke()
                    } else {
                        onGpsPermissionDisabled?.invoke()
                    }
                }
    }

    fun requestPermissions(vararg permissions: String) {
        permissionLauncher.launch(arrayOf(*permissions))
    }

    private fun checkAllPermission(grantResults: Map<String, Boolean>): Boolean {
        for (data in grantResults) {
            if (!data.value) return false
        }

        return true
    }

    private fun deniedForever(grantResults: Map<String, Boolean>): Boolean {
        for (data in grantResults) {
            if (!activity.shouldShowRequestPermissionRationale(data.key)) return false
        }

        return true
    }

    fun setOnPermissionGrantedListener(onPermissionGranted: () -> Unit) {
        this.onPermissionGranted = onPermissionGranted
    }

    fun setOnPermissionDeniedListener(onPermissionDenied: () -> Unit) {
        this.onPermissionDenied = onPermissionDenied
    }

    fun setOnPermissionDeniedForeverListener(onPermissionDeniedForever: () -> Unit) {
        this.onPermissionDeniedForever = onPermissionDeniedForever
    }

    /**
     * Checks whether gps is enabled or not, if not it prompts user a dialog to enable gps.
     * */
    fun checkGpsEnabled() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setWaitForAccurateLocation(true)
        }.build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            this.onGpsPermissionEnabled?.invoke()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()

                    gpsLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setOnGpsPermissionEnabledListener(onGpsPermissionEnabled: () -> Unit) {
        this.onGpsPermissionEnabled = onGpsPermissionEnabled
    }

    fun setOnGpsPermissionDisabledListener(onGpsPermissionDisabled: () -> Unit) {
        this.onGpsPermissionDisabled = onGpsPermissionDisabled
    }
}