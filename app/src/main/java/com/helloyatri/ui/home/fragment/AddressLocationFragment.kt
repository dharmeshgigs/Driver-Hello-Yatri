package com.helloyatri.ui.home.fragment

import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.AdddressLocationFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AddressLocationFragment : BaseFragment<AdddressLocationFragmentBinding>(),
    OnMapReadyCallback, LocationListener {
    private var googleMap: GoogleMap? = null
    private val apiViewModel: ApiViewModel by activityViewModels()
    private var address = ""
    private var lat = ""
    private var long = ""
    private var countDownTimer : CountDownTimer? = null
    private var savedAddress: SavedAddress? = null

    var location : LatLng ? = null
    companion object {
        fun createBundle(
            data: String? = null

        ) = bundleOf(
            "data" to data,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedAddress = Gson().fromJson(arguments?.getString("data") ?: "", SavedAddress::class.java)
        lat = savedAddress?.latitude ?: ""
        long = savedAddress?.longitude ?: ""
        address = savedAddress?.location ?: ""
        if(lat.isEmpty() && long.isEmpty()) {
            getUserCurrentLocation( {
                it?.let {
                    lat = it.latitude.toString()
                    long = it.longitude.toString()
                    location =
                        LatLng(
                            lat.toDouble(), long.toDouble()
                        )
                    setUpMapCamera()
                    getAddress(it)
                }
            })
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AdddressLocationFragmentBinding {
        return AdddressLocationFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpView()
        fileUpForm()
        setUpClickListeners()
    }

    private fun fileUpForm() = with(binding) {
        editTextLocation.setText(address)
    }

    private fun setUpView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setUpClickListeners() = with(binding) {
        btnConfirmPoint.setOnClickListener {
            apiViewModel.setSharedData(address, lat = lat, long = long)
            navigator.goBack()
        }
    }

    override fun setUpToolbar() {
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
//
        // Set a pin at a specific location

        if(lat.isEmpty() && long.isEmpty()) {
//            location =
//                LatLng(
//                    20.5937, 78.9629
//                )
//            getAddress(location)
//            getCurrentLocation()
        } else {
            location =
                LatLng(
                    lat.toDouble(), long
                        .toDouble()
                )
        }
        binding.editTextLocation.setText(address)
        setUpMapCamera()
        googleMap?.setOnCameraIdleListener {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    try {
                        val center = googleMap?.cameraPosition?.target
                        center?.let {
                            getAddress(it)
                        }
                    } catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
            }
            countDownTimer?.start()
        }
    }

    private fun setUpMapCamera() {
        location?.let { CameraUpdateFactory.newLatLngZoom(it, 10f) }
            ?.let { googleMap?.moveCamera(it) }
    }

    fun getAddress(center : LatLng?) {
        center?.let {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(center.latitude, center.longitude, 1)
            lat = center.latitude.toString()
            long = center.longitude.toString()
            if (!addresses.isNullOrEmpty()) {
                address = addresses[0].getAddressLine(0)
                binding.editTextLocation.setText(address)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        Toast.makeText(context,"Lat:"+location.latitude+"Long:"+location.longitude,Toast.LENGTH_LONG)
    }
}