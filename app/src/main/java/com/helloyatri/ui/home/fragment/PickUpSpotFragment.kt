package com.helloyatri.ui.home.fragment

import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.GetCencellation
import com.helloyatri.data.model.TripRiderModel
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
import com.helloyatri.ui.home.dialog.RideCancelledDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment.Companion.SUCCESS
import com.helloyatri.utils.AppUtils
import com.helloyatri.utils.AppUtils.openCallDialer
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.location.LocationProvider
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.util.Locale


@AndroidEntryPoint
class PickUpSpotFragment : BaseFragment<FragmentPickUpSpotBinding>(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private val apiViewModel: ApiViewModel by activityViewModels()

    private var address = ""
    private var lat = ""
    private var long = ""
    private var tripRiderModel: TripRiderModel? = null
    private var locationProvider: LocationProvider? = null
    var location: LatLng? = null
    var endLocation: LatLng? = null
    var pickupLocation: LatLng? = null
    var cencellationDataList: ArrayList<String> = arrayListOf()
    var startMarker: Marker? = null
    var endMarker: Marker? = null

    companion object {
        fun createBundle(
            data: String? = null
        ) = bundleOf(
            "data" to data
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): FragmentPickUpSpotBinding {
        return FragmentPickUpSpotBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripRiderModel = apiViewModel.tripRequest.value
        pickupLocation = LatLng(
            tripRiderModel?.tripDetails?.startLocation?.latitude?.toDouble() ?: 0.0,
            tripRiderModel?.tripDetails?.startLocation?.longitude?.toDouble() ?: 0.0
        )
        endLocation = LatLng(
            tripRiderModel?.tripDetails?.endLocation?.latitude?.toDouble() ?: 0.0,
            tripRiderModel?.tripDetails?.endLocation?.longitude?.toDouble() ?: 0.0
        )
        getCurrentLocation()
        initObservers()
    }

    override fun bindData() {
        initViews()
        apiViewModel.getCancelletionReasonAPI()
        setUpView()
        setUpClickListener()
    }

    private fun initViews() = with(binding) {
        tripRiderModel?.let {
            it.riderDetails?.let {
                it.name?.let {
                    textViewMeetDriverAt.text = String.format(
                        getString(R.string.label_meet_rahul_patel_at_their_pick_up_spot), it
                    )
                    textViewUserName.text = it
                }
                it.paymentType?.let {
                    textViewPaymentType.text = it
                }


            }
            it.tripDetails?.let {
                it.arrivingDuration?.let {
                    textViewEstimatedTime.text = String.format(
                        getString(R.string.label_min), AppUtils.updateTimerUIMin(it)
                    )
                }
                it.startLocation?.address?.let {
                    textViewStartLocation.text = it
                    textViewApprovedStartLocation.text = it
                }
                it.endLocation?.address?.let {
                    textViewDestinationLocation.text = it
                    textViewApprovedDestinationLocation.text = it
                }
                it.pickup_note?.let {
                    textViewNote.text = String.format(
                        getString(R.string.label_dynamic_note_note_message_show_here), it
                    )
                }
            }
        }
    }

    private fun initObservers() {
        apiViewModel.getCanclletionReasonLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        val response =
                            Gson().fromJson(resource.data.toString(), GetCencellation::class.java)
                        cencellationDataList.clear()
                        cencellationDataList.addAll(response.data)
                        apiViewModel.getCanclletionReasonLiveData.value = null
                    }

                    Status.ERROR -> {
                        hideLoader()
                        apiViewModel.getCanclletionReasonLiveData.value = null
                    }

                    Status.LOADING -> showLoader()
                }
            }
        }

        apiViewModel.updateArriveStatusLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        showVerificationDialog()
                        apiViewModel.updateArriveStatusLiveData.value = null
                    }

                    Status.ERROR -> {
                        hideLoader()
                        apiViewModel.updateArriveStatusLiveData.value = null
                    }

                    Status.LOADING -> {
                        showLoader()
                    }
                }
            }
        }

        apiViewModel.pickupNoteLiveData.observe(requireActivity()) {
            it?.let {
                TextDecorator.decorate(
                    binding.textViewNote, String.format(
                        getString(R.string.label_dynamic_note_message_show_here), it
                    )
                ).setTextColor(R.color.colorPrimary, getString(R.string.label_note)).build()
            }
        }

        apiViewModel.tripStartLiveData.observe(requireActivity()) {
            it?.let {
                if (it) {
                    activity?.runOnUiThread {
                        requireActivity().runCatching {
                            setUpApprovedRideUI()
                        }
                    }
                }
            }
        }

        apiViewModel.completeTripLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        navigator.load(RideCompleteFragment::class.java)
                            .clearHistory(this@PickUpSpotFragment::class.java.simpleName).add(false)
                        apiViewModel.completeTripLiveData.value = null
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message ?: getString(resource.resId!!)
                        showErrorMessage(error)
                        apiViewModel.completeTripLiveData.value = null
                    }

                    Status.LOADING -> {
                        showLoader()
                    }
                }
            }
        }

        apiViewModel.cancleRideLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        navigator.goBack()
                        apiViewModel.cancleRideLiveData.value = null
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message ?: getString(resource.resId!!)
                        showErrorMessage(error)
                        apiViewModel.cancleRideLiveData.value = null
                    }

                    Status.LOADING -> {
                        showLoader()
                    }
                }
            }
        }

        apiViewModel._tripStatusUpdatedLiveData.observe(this) {
            it?.let {
                it.tripDetails?.let {
                    if (it.status.equals(Constants.CANCELLED)) {
                        RideCancelledDialogFragment {
                            navigator.goBack()
                            apiViewModel._tripStatusUpdatedLiveData.value = null
                        }.show(childFragmentManager, PickUpSpotFragment::class.java.simpleName)
                    } else if (it.status.equals(Constants.FINISHED)) {
                        navigator.load(RideCompleteFragment::class.java)
                            .clearHistory(this@PickUpSpotFragment::class.java.simpleName).add(false)
                    }
                }
            }
        }
    }

    private fun getCurrentLocation() {
        locationProvider = LocationProvider((activity as BaseActivity), this)
        locationProvider?.getCurrentLocation(updated = true) {
            it.let {
                hideLoader()
                lat = it.latitude.toString()
                long = it.longitude.toString()
                location = LatLng(
                    lat.toDouble(), long.toDouble()
                )
                googleMap?.let {
                    setUpMapCamera()
                    //                    addStartMarker()
                    //                    addPickUpMarker()
                }
                getAddress(it)
            }
        }
    }

    private fun addStartMarker() {
        location?.let {
            startMarker = googleMap!!.addMarker(
                MarkerOptions().position(it).title("You")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker)) // Custom marker icon
            )
        }
    }

    private fun addPickUpMarker() {
        pickupLocation?.let {
            endMarker = googleMap!!.addMarker(
                MarkerOptions().position(it).title(tripRiderModel?.riderDetails?.name ?: "")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_marker)) // Custom marker icon
            )
        }
    }

    private fun setUpView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setUpMapCamera() {
        location?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
            ?.let { googleMap?.moveCamera(it) }
    }

    private fun getAddress(center: LatLng?) {
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

    private fun setUpClickListener() = with(binding) {
        textViewArrivedAtPoint.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment { action ->
                if (action == YES) {
                    tripRiderModel?.tripDetails?.id?.let {
                        apiViewModel.updateArriveStatus(
                            Request(
                                trip_id = it.toString(),
                            )
                        )
                    }
                }
            }

            commonYesNoDialogFragment.arguments = bundleOf(
                TITLE to String.format(
                    getString(R.string.label_dynamic_have_you_arrived_to_pooja_s_pickup_point),
                    tripRiderModel?.riderDetails?.name ?: ""
                ),
                CANCEL_TEXT to getString(R.string.btn_no_still_not),
                OK_TEXT to getString(R.string.btn_yes_i_reached),
                ICON to R.drawable.ic_car_location
            )

            commonYesNoDialogFragment.show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        }

        textViewCancelRide.setOnClickListener {
            CancelRideBottomSheet({
                activity?.apply {
//                    navigator.goBack()
                    apiViewModel.cancelRide(
                        Request(
                            trip_id = tripRiderModel?.tripDetails?.id?.toString() ?: "0",
                            cancel_reason = it
                        )
                    )
                }
            }, cencellationDataList).show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        }

        textViewEmergency.setOnClickListener {
            EmergencyAssistanceBottomSheet(callBack = {
                location?.let {
                    apiViewModel.location = Pair(it, address)
                    navigator.load(TripReportCrashFragment::class.java).add(false)
                }
            }).show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        }

        textViewEndTrip.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment {
                if (it == YES) {
                    apiViewModel.completeTrip(
                        Request(
                            trip_id = tripRiderModel?.tripDetails?.id.toString()
                        )
                    )
                }
            }
            commonYesNoDialogFragment.arguments = bundleOf(
                TITLE to String.format(
                    getString(R.string.label_dynamic_have_you_reached_to_pooja_s_destination_point),
                    AppUtils.getFirstName(tripRiderModel?.riderDetails?.name ?: "")
                ),
                CANCEL_TEXT to getString(R.string.btn_no_still_not),
                OK_TEXT to getString(R.string.btn_yes_i_reached),
                ICON to R.drawable.ic_car_location
            )

            commonYesNoDialogFragment.show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        }

        imageViewCurrentLocation.setOnClickListener {

        }
        textViewNavigateToFullScreen.setOnClickListener {
            if (textViewNavigateToFullScreen.text.toString()
                    .equals(getString(R.string.label_navigate_in_full_screen), false)
            ) {
                textViewNavigateToFullScreen.text = getString(R.string.label_show_rider_details)
                apiViewModel.tripStartLiveData.value?.takeIf { it }?.let {
                    constraintLayoutApprovedRide.hide()
                } ?: run {
                    layoutRideDetails.hide()
                }
            } else {
                textViewNavigateToFullScreen.text =
                    getString(R.string.label_navigate_in_full_screen)
                apiViewModel.tripStartLiveData.value?.takeIf { it }?.let {
                    constraintLayoutApprovedRide.show()
                } ?: run {
                    layoutRideDetails.show()
                }
            }
        }
        imageViewCall.setOnClickListener {
            // TODO: Set Phone Number Dynamic
            tripRiderModel?.riderDetails?.mobile?.let {
                activity?.openCallDialer(it)
            }
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onRideApproved(status: String) {
//        showLoader()
//        activity?.runOnUiThread {
//            requireActivity().runCatching {
//                setUpApprovedRideUI()
//            }
//        }
//    }

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
        showLoader()
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap = map
        setUpMapCamera()

//        getCurrentLocation()
//        // Define the locations
//        val startLocation = LatLng(23.033863, 72.585022)
//        val endLocation = LatLng(21.740521, 72.148827)
//
//        // Add custom markers for start and end locations
//        val startMarker = googleMap!!.addMarker(
//            MarkerOptions()
//                .position(startLocation)
//                .title("Start Location")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker)) // Custom marker icon
//        )


//
//        // Add a polyline between the locations
//        val polylineOptions = PolylineOptions()
//            .add(startLocation)
//            .add(endLocation)
//            .color(R.color.polyline_color) // Set the color of the polyline
//            .width(5f) // Set the width of the polyline
//
//        val polyline: Polyline = googleMap!!.addPolyline(polylineOptions)
//        val bounds = LatLngBounds.Builder()
//            .include(startLocation)
//            .include(endLocation)
//            .build()
//        // Move the camera to the first location
//        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
//        googleMap!!.animateCamera(cameraUpdate)
//
        // Set a pin at a specific location

//        if(lat.isEmpty() && long.isEmpty()) {
////            location =
////                LatLng(
////                    20.5937, 78.9629
////                )
////            getAddress(location)
////            getCurrentLocation()
//        } else {
//            location =
//                LatLng(
//                    lat.toDouble(), long
//                        .toDouble()
//                )
//        }

        // binding.editTextLocation.setText(address)

//        setUpMapCamera()
//        googleMap?.setOnCameraIdleListener {
//            countDownTimer?.cancel()
//            countDownTimer = object : CountDownTimer(2000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                }
//                override fun onFinish() {
//                    try {
//                        val center = googleMap?.cameraPosition?.target
//                        center?.let {
//                            getAddress(it)
//                        }
//                    } catch (e : Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//            countDownTimer?.start()
//        }
    }

//    private fun drawRoute(origin: LatLng, destination: LatLng) {
//        GlobalScope.launch(Dispatchers.IO) {
//            val apiKey = "YOUR_API_KEY_HERE"
//            val geoApiContext = GeoApiContext.Builder()
//                .apiKey(apiKey)
//                .build()
//
//            try {
//                val result: DirectionsResult = DirectionsApi.newRequest(geoApiContext)
//                    .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
//                    .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
//                    .await()
//
//                val route: DirectionsRoute = result.routes[0]
//                val polylineOptions = PolylineOptions().color(R.color.purple_200).width(5f)
//
//                for (step in route.legs[0].steps) {
//                    val decodedPath = PolyUtil.decode(step.polyline.encodePath)
//                    polylineOptions.addAll(decodedPath)
//                }
//
//                runOnUiThread {
//                    mMap.addPolyline(polylineOptions)
//                    val bounds = LatLngBounds.builder()
//                        .include(origin)
//                        .include(destination)
//                        .build()
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    private fun showVerificationDialog() {
        RideVerificationDialogFragment {
            val rideVerificationResultDialogFragment = RideVerificationResultDialogFragment()
            if (it == SUCCESS) {
                rideVerificationResultDialogFragment.arguments = bundleOf(
                    RideVerificationResultDialogFragment.STATUS to SUCCESS,
                )
            } else {
                rideVerificationResultDialogFragment.arguments = bundleOf(
                    RideVerificationResultDialogFragment.STATUS to RideVerificationResultDialogFragment.FAILED,
                )
            }
            rideVerificationResultDialogFragment.show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        }.show(childFragmentManager, PickUpSpotFragment::class.java.simpleName)
    }
}