package com.helloyatri.ui.home.fragment

import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.GetCencellation
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.FragmentPickUpSpotBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.bottomsheet.CancelRideBottomSheet
import com.helloyatri.ui.home.bottomsheet.EmergencyAssistanceBottomSheet
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.CANCEL_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.ICON
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.OK_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.TITLE
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.YES
import com.helloyatri.ui.home.dialog.RideVerificationDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment.Companion.SUCCESS
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.location.LocationProvider
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Locale

@AndroidEntryPoint
class PickUpSpotFragment : BaseFragment<FragmentPickUpSpotBinding>(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private val apiViewModel: ApiViewModel by activityViewModels()
    private var address = ""
    private var lat = ""
    private var long = ""
    private var countDownTimer : CountDownTimer? = null
    private var savedAddress: SavedAddress? = null
    private var locationProvider : LocationProvider? = null
    var location : LatLng? = null
    var cencellationDataList: ArrayList<String> = arrayListOf()


    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): FragmentPickUpSpotBinding {
        return FragmentPickUpSpotBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun bindData() {
        if(lat.isEmpty() && long.isEmpty()) {
            getCurrentLocation()
        }
        setTextDecorator()
        apiViewModel.getCancelletionReasonAPI()
        initObservers()
        setUpView()
        setClickListener()
    }

    private fun initObservers() {
        apiViewModel.getCanclletionReasonLiveData.observe(this){resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                    val response =
                        Gson().fromJson(resource.data.toString(), GetCencellation::class.java)
                    cencellationDataList.clear()
                    cencellationDataList.addAll(response.data)
                }
                Status.ERROR -> {
                    hideLoader()
                }

                Status.LOADING -> showLoader()
            }
        }
    }

    private fun getCurrentLocation() {
        locationProvider = LocationProvider((activity as BaseActivity),this)
        locationProvider?.getCurrentLocation(updated = true) {
            it?.let {
                showMessage("Location is ${it.latitude.toString()}")
                lat = it.latitude.toString()
                long = it.longitude.toString()
                location =
                    LatLng(
                        lat.toDouble(), long.toDouble()
                    )
                setUpMapCamera()
                getAddress(it)
            }
        }
    }

    private fun setUpView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
            }
        }
    }


    private fun setClickListener() = with(binding) {
        textViewArrivedAtPoint.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment { action ->
                if (action == YES) {
                    RideVerificationDialogFragment {
                        val rideVerificationResultDialogFragment =
                                RideVerificationResultDialogFragment()
                        if (it == SUCCESS) {
                            rideVerificationResultDialogFragment.arguments = bundleOf(
                                    RideVerificationResultDialogFragment.STATUS to SUCCESS,
                            )

                            rideVerificationResultDialogFragment.show(childFragmentManager,
                                    PickUpSpotFragment::class.java.simpleName)
                        } else {
                            rideVerificationResultDialogFragment.arguments = bundleOf(
                                    RideVerificationResultDialogFragment.STATUS to RideVerificationResultDialogFragment.FAILED,
                            )
                            rideVerificationResultDialogFragment.show(childFragmentManager,
                                    PickUpSpotFragment::class.java.simpleName)
                        }
                    }.show(childFragmentManager, PickUpSpotFragment::class.java.simpleName)
                }
            }

            commonYesNoDialogFragment.arguments = bundleOf(
                    TITLE to getString(R.string.label_have_you_arrived_to_pooja_s_pickup_point),
                    CANCEL_TEXT to getString(R.string.btn_no_still_not),
                    OK_TEXT to getString(R.string.btn_yes_i_reached),
                    ICON to R.drawable.ic_car_location)

            commonYesNoDialogFragment.show(childFragmentManager,
                    PickUpSpotFragment::class.java.simpleName)
        }

        textViewCancelRide.setOnClickListener {
            CancelRideBottomSheet({
                activity?.apply {
                    finish()
                }
            }, cencellationDataList).show(childFragmentManager, PickUpSpotFragment::class.java.simpleName)
        }

        textViewEmergency.setOnClickListener {
            EmergencyAssistanceBottomSheet().show(childFragmentManager,
                    PickUpSpotFragment::class.java.simpleName)
        }

        textViewEndTrip.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment {
                if (it == YES) {
                    navigator.load(RideCompleteFragment::class.java).replace(false)
                }
            }
            commonYesNoDialogFragment.arguments = bundleOf(
                    TITLE to getString(R.string.label_have_you_arrived_to_pooja_s_pickup_point),
                    CANCEL_TEXT to getString(R.string.btn_no_still_not),
                    OK_TEXT to getString(R.string.btn_yes_i_reached),
                    ICON to R.drawable.ic_car_location)

            commonYesNoDialogFragment.show(childFragmentManager,
                    PickUpSpotFragment::class.java.simpleName)
        }
    }

    private fun setTextDecorator() = with(binding) {
        TextDecorator.decorate(textViewNote, textViewNote.trimmedText)
                .setTextColor(R.color.homeBgBlueColor, "Note:").build()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRideApproved(status: String) {
        showLoader()
        activity?.runOnUiThread {
            requireActivity().runCatching {
                setUpApprovedRideUI()
            }
        }
    }

    private fun setUpApprovedRideUI() = with(binding) {
        hideLoader()
        textViewLabelReachIn.text = getString(R.string.label_reaching_destination)
        textViewMeetDriverAt.text = getString(R.string.label_you_will_reach_at_approx_10_00pm)
        textViewEstimatedTime.text = getString(R.string.label_15_min)
        layoutRideDetails.hide()
        constraintLayoutApprovedRide.show()

    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
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
       // binding.editTextLocation.setText(address)
        setUpMapCamera()
        googleMap?.setOnCameraIdleListener {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    Log.e("TAG", "onFinish")
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

}