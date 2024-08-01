package com.helloyatri.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng


class LocationProvider(private val activity: ComponentActivity, fragment: Fragment? = null) {

    private val permissionUtil: PermissionUtilNew
    private val fusedLocationProviderClient: FusedLocationProviderClient

    init {
        permissionUtil = PermissionUtilNew(activity, fragment)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(priority: Int = Priority.PRIORITY_HIGH_ACCURACY, updated : Boolean = false, onLocationFound: (latLng: LatLng) -> Unit) {
        requestLocationPermission {
            if(updated) {
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(100)
                    .setMaxUpdateDelayMillis(10000)
                    .build()
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult ?: return
                        if(locationResult.locations.isNotEmpty()) {
                            onLocationFound(LatLng(locationResult.locations[0].latitude, locationResult.locations[0].longitude))
                        }
//                    for (location in locationResult.locations){
//                        // Update UI with location data
//                        // ...
//                    }
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
                    Looper.getMainLooper())
            } else {
                fusedLocationProviderClient.getCurrentLocation(priority, null).addOnSuccessListener { location ->
                    location?.let {
                        onLocationFound(LatLng(it.latitude, it.longitude))
                    }
                }
            }
        }
    }

    private fun requestLocationPermission(onPermissionGranted: () -> Unit) {
        permissionUtil.apply {
            requestPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            )

            setOnPermissionGrantedListener {
                checkGpsEnabled()
                setOnGpsPermissionEnabledListener {
                    onPermissionGranted()
                }
            }
        }
    }

    private fun openLocationSettings() {
        activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}