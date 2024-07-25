package com.helloyatri.ui.home.fragment

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.helloyatri.R
import com.helloyatri.databinding.ConfirmStartRidePointFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.base.MapBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ConfirmStartRidePointFragment : BaseFragment<ConfirmStartRidePointFragmentBinding>(),
    OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var currentMarker: Marker? = null
    var address: String? = null
    var lat: String? = null
    var long: String? = null

    //    private val apiViewModel by viewModels<ApiViewModel>()
    private val apiViewModel: ApiViewModel by activityViewModels()


//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnChildFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException("$context must implement OnChildFragmentInteractionListener")
//        }
//    }

    companion object {
        fun createBundle(
            lat: String? = null,
            long: String? = null,
            name: String? = null,
            location: String? = null

        ) = bundleOf(
            "lat" to lat,
            "long" to long,
            "name" to name,
            "location" to location
        )
    }

//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): ConfirmStartRidePointFragmentBinding {
        return ConfirmStartRidePointFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        loadLocation()
//        binding.mapView.getMapAsync {
//            googleMap = it
//
//            // Set a pin at a specific location
//            val location = LatLng(37.7749, -122.4194) // Replace with your desired coordinates
//            googleMap.addMarker(MarkerOptions().position(location).title("Marker in San Francisco"))
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
//        }
    }

    private fun loadLocation() = with(binding) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(900)
            address = arguments?.getString("location") ?: ""
            editTextLocation.setText(arguments?.getString("location") ?: "")
        }

        btnConfirmPoint.setOnClickListener {
            apiViewModel.setSharedData(address ?: "")
            apiViewModel.setLatitudeData(lat?:"")
            apiViewModel.setLongitudeData(long?:"")
            // address?.let { it1 -> apiViewModel.setSharedData(it1) }
            navigator.goBack()
        }
    }

    override fun setUpToolbar() = with(binding.customToolbar) {
        showBackButton(true)
        setToolbarTitle(getString(R.string.title_start_ride_from)).setToolbarColor(
            android.R.color.transparent
        ).build()
    }

//    override fun onMapReady(map: GoogleMap) {
//
//    }

//    override fun onResume() {
//        //binding.mapView.onResume()
//        super.onResume()
//    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
//
        // Set a pin at a specific location
        val location =
            arguments?.getString("lat")?.let {
                LatLng(
                    it.toDouble(), arguments?.getString("long")!!
                        .toDouble()
                )
            } // Replace with your desired coordinates
        binding.editTextLocation.setText(arguments?.getString("location") ?: "")
        location?.let { MarkerOptions().position(it).title(arguments?.getString("name")) }
            ?.let { currentMarker = googleMap.addMarker(it) }
        location?.let { CameraUpdateFactory.newLatLngZoom(it, 10f) }
            ?.let { googleMap.moveCamera(it) }

        googleMap.setOnCameraIdleListener {
            currentMarker?.remove()
            val center = googleMap.cameraPosition.target
            val zoom = googleMap.cameraPosition.zoom

            //  latLongText.text = "Lat: ${center.latitude}, Long: ${center.longitude}"

            val addresses = geocoder.getFromLocation(center.latitude, center.longitude, 1)
            lat = center.latitude.toString()
            long = center.longitude.toString()
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    address = addresses[0].getAddressLine(0)
                    binding.editTextLocation.setText(address)

                    currentMarker = googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                center.latitude,
                                center.longitude
                            )
                        ).title(address)
                    )

                    //  addressText.text = "Address: $address"
                } else {
                    // addressText.text = "Address: Not found"
                }
            }

        }
    }


}