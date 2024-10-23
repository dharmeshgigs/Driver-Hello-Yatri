package com.helloyatri.ui.home.fragment

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavInfo
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.SimulationOptions
import com.google.android.libraries.navigation.SupportNavigationFragment
import com.google.android.libraries.navigation.Waypoint
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.GetCencellation
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.FragmentNavPickUpSpotBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
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
import com.helloyatri.utils.InitializedMapScope
import com.helloyatri.utils.InitializedNavRunnable
import com.helloyatri.utils.InitializedNavScope
import com.helloyatri.utils.NavForwardingManager
import com.helloyatri.utils.NavInfoReceivingService
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.textdecorator.TextDecorator
import com.mukeshsolanki.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class NavPickUpSpotFragment : BaseFragment<FragmentNavPickUpSpotBinding>(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private val apiViewModel: ApiViewModel by activityViewModels()

    private var address = ""
    private var lat = ""
    private var long = ""
    private var tripRiderModel: TripRiderModel? = null
    var location: LatLng? = null
    var endLocation: LatLng? = null
    var pickupLocation: LatLng? = null
    var cencellationDataList: ArrayList<String> = arrayListOf()
    var startMarker: Marker? = null
    var endMarker: Marker? = null
    private var arrivalListener: Navigator.ArrivalListener? = null
    private var navigatorScope: InitializedNavScope? = null
    private var routeChangedListener: Navigator.RouteChangedListener? = null
    private var pendingNavActions = mutableListOf<InitializedNavRunnable>()
    private lateinit var navFragment: SupportNavigationFragment
    private var headerNavInfo: NavInfo? = null

    companion object {
        fun createBundle(
            data: String? = null
        ) = bundleOf(
            "data" to data
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): FragmentNavPickUpSpotBinding {
        return FragmentNavPickUpSpotBinding.inflate(layoutInflater)
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
        initObservers()
        registerNavigationListeners()
        initializeNavigationApi()
    }

    override fun bindData() {
        navFragment =
            childFragmentManager.findFragmentById(R.id.navigation_fragment) as SupportNavigationFragment
        navFragment.setEtaCardEnabled(false)
        initViews()
        apiViewModel.getCancelletionReasonAPI()
//        setUpView()
        setUpClickListener()
        getCurrentLocation()
        withNavigatorAsync {
            NavForwardingManager.startNavForwarding(navigatorNav, requireActivity())
        }
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
                if(it.status == "ARRIVED") {
                    showVerificationDialog()
                }
            }
        }

        apiViewModel.tripStartLiveData.value?.takeIf { it }?.let {
            if(it) {
                constraintLayoutApprovedRide.show()
                layoutRideDetails.hide()
            } else {
                constraintLayoutApprovedRide.hide()
                layoutRideDetails.show()
            }
        } ?: run {
            layoutRideDetails.show()
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
                            .clearHistory(this@NavPickUpSpotFragment::class.java.simpleName).add(false)
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
                        }.show(childFragmentManager, NavPickUpSpotFragment::class.java.simpleName)
                    } else if (it.status.equals(Constants.FINISHED)) {
                        navigator.load(RideCompleteFragment::class.java)
                            .clearHistory(this@NavPickUpSpotFragment::class.java.simpleName).add(false)
                    }
                }
            }
        }

        val navInfoObserver = Observer { navInfo: NavInfo? ->
            navInfo?.let {
                Log.e("TAG",Gson().toJson(it))
            }
        }
        NavInfoReceivingService.navInfoLiveData.observe(this, navInfoObserver)
    }

    private fun getCurrentLocation() {

        apiViewModel.locationLiveData.observe(this) {
            it?.let {
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.navigation_fragment) as SupportMapFragment
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
                childFragmentManager, NavPickUpSpotFragment::class.java.simpleName
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
                        ),"Trip"
                    )
                }
            }, cencellationDataList).show(
                childFragmentManager, NavPickUpSpotFragment::class.java.simpleName
            )
        }

        textViewEmergency.setOnClickListener {
            EmergencyAssistanceBottomSheet(callBack = {
                location?.let {
                    apiViewModel.location = Pair(it, address)
                    navigator.load(TripReportCrashFragment::class.java).add(false)
                }
            }).show(
                childFragmentManager, NavPickUpSpotFragment::class.java.simpleName
            )
        }

        textViewEndTrip.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment {
                if (it == YES) {
                    apiViewModel.completeTrip(
                        Request(
                            trip_id = tripRiderModel?.tripDetails?.id.toString(),
                            latitude = apiViewModel.locationLiveData.value?.latitude?.toString() ?: "",
                            longitude = apiViewModel.locationLiveData.value?.longitude?.toString() ?: "",
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
                childFragmentManager, NavPickUpSpotFragment::class.java.simpleName
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

    override fun onMapReady(map: GoogleMap) {
        showLoader()
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap = map
        setUpMapCamera()
    }

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
                childFragmentManager, NavPickUpSpotFragment::class.java.simpleName
            )
        }.show(childFragmentManager, NavPickUpSpotFragment::class.java.simpleName)
    }

    /**
     * Registers a number of example event listeners that show an on screen message when certain
     * navigation events occur (e.g. the driver's route changes or the destination is reached).
     */
    private fun registerNavigationListeners() {
        withNavigatorAsync {
            arrivalListener =
                Navigator.ArrivalListener { // Show an onscreen message
                    showMessage("User has arrived at the destination!")

                    // Stop turn-by-turn guidance and return to TOP_DOWN perspective of the map
                    navigatorNav.stopGuidance()

                    // Stop simulating vehicle movement.
                    if (BuildConfig.DEBUG) {
                        navigatorNav.simulator.unsetUserLocation()
                    }
                }
            navigatorNav.addArrivalListener(arrivalListener)

            routeChangedListener =
                Navigator.RouteChangedListener { // Show an onscreen message when the route changes
                    showMessage("onRouteChanged: the driver's route changed")
                }
            navigatorNav.addRouteChangedListener(routeChangedListener)
        }
    }

    /**
     * Runs [block] once navigator is initialized. Block is ignored if the navigator is never
     * initialized (error, etc.).
     *
     * This ensures that calls using the navigator before the navigator is initialized gets executed
     * after the navigator has been initialized.
     */
    private fun withNavigatorAsync(block: InitializedNavRunnable) {
        val navigatorScope = navigatorScope
        if (navigatorScope != null) {
            navigatorScope.block()
        } else {
            pendingNavActions.add(block)
        }
    }

    /** Starts the Navigation API, saving a reference to the ready Navigator instance. */
    private fun initializeNavigationApi() {
        NavigationApi.getNavigator(
            (activity as HomeActivity),
            object : NavigationApi.NavigatorListener {
                override fun onNavigatorReady(navigator: Navigator) {
                    val scope = InitializedNavScope(navigator)
                    navigatorScope = scope
                    pendingNavActions.forEach { block -> scope.block() }
                    pendingNavActions.clear()
                    navigateToPlace()
                }

                override fun onError(@NavigationApi.ErrorCode errorCode: Int) {
                    when (errorCode) {
                        NavigationApi.ErrorCode.NOT_AUTHORIZED -> {
                            // Note: If this message is displayed, you may need to check that
                            // your API_KEY is specified correctly in AndroidManifest.xml
                            // and is been enabled to access the Navigation API
                            showErrorMessage(
                                "Error loading Navigation API: Your API key is " +
                                        "invalid or not authorized to use Navigation."
                            )
                        }
                        NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> {
                            showErrorMessage(
                                "Error loading Navigation API: User did not " +
                                        "accept the Navigation Terms of Use."
                            )
                        }
                        else -> showErrorMessage("Error loading Navigation API: $errorCode")
                    }
                }
            },
        )
    }

    /**
     * Runs [block] once map is initialized. Block is ignored if map is never initialized.
     *
     * This ensures that calls using the map before the map is initialized gets executed after the map
     * has been initialized.
     */
    private fun withMapAsync(block: InitializedMapScope.() -> Unit) {
        navFragment.getMapAsync { map ->
            object : InitializedMapScope {
                override val map = map
            }
                .block()
        }
    }

    private fun navigateToPlace() {
        val waypoint: Waypoint = Waypoint.builder().setLatLng(pickupLocation?.latitude ?: 0.0, pickupLocation?.longitude ?: 0.0).build()
        val waypointDestination: Waypoint = Waypoint.builder().setLatLng(endLocation?.latitude ?: 0.0, endLocation?.longitude ?: 0.0).build()
//            if (place.types?.contains(Place.Type.GEOCODE) == true) {
//                // An example of setting a destination via Lat-Lng.
//                // Note: Setting LatLng destinations can result in poor routing quality/ETA calculation.
//                // Wherever possible you should use a Place ID to describe the destination accurately.
//                place.latLng?.let { Waypoint.builder().setLatLng(it.latitude, it.longitude).build() }
//            } else {
//                // Set a destination by using a Place ID (the recommended method)
//                try {
//                    Waypoint.builder().setPlaceIdString(place.id).build()
//                } catch (e: Waypoint.UnsupportedPlaceIdException) {
//                    showErrorMessage("Place ID was unsupported.")
//                    return
//                }
//            }

        withNavigatorAsync {
            val points = arrayListOf<Waypoint>()
            points.add(waypoint)
            points.add(waypointDestination)
            val pendingRoute = navigatorNav.setDestinations(points)

            // Set an action to perform when a route is determined to the destination
            pendingRoute.setOnResultListener { code ->
                when (code) {
                    Navigator.RouteStatus.OK -> {
                        // Hide the toolbar to maximize the navigation UI
//                        actionBar?.hide()

                        // Enable voice audio guidance (through the device speaker)
//                        navigator.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)

                        // Simulate vehicle progress along the route (for demo/debug builds)
                        if (BuildConfig.DEBUG) {
                            navigatorNav.simulator.simulateLocationsAlongExistingRoute(
                                SimulationOptions().speedMultiplier(5f)
                            )
                        }

                        // Start turn-by-turn guidance along the current route
                        navigatorNav.startGuidance()
                    }
                    Navigator.RouteStatus.ROUTE_CANCELED -> {
                        // Return to top-down perspective
                        showErrorMessage("Route guidance cancelled.")
                    }
                    Navigator.RouteStatus.NO_ROUTE_FOUND,
                    Navigator.RouteStatus.NETWORK_ERROR -> {
                        // TODO: Add logic to handle when a route could not be determined
                        showErrorMessage("Error starting guidance: $code")
                    }
                    else -> showErrorMessage("Error starting guidance: $code")
                }
            }
        }
    }


}