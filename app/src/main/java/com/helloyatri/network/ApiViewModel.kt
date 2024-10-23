package com.helloyatri.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.model.HomeDataModel
import com.helloyatri.data.model.PaymentHistory
import com.helloyatri.data.model.PopUp
import com.helloyatri.data.model.RideActivityResponse
import com.helloyatri.data.model.RideActivityTabs
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.data.model.Trips
import com.helloyatri.ui.usecases.HomeUseCases
import com.helloyatri.ui.usecases.RideActivityUseCases
import com.helloyatri.ui.usecases.ScheduleTripUseCases
import com.helloyatri.ui.usecases.TripPaymentUseCases
import com.helloyatri.ui.usecases.TripUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor(private val authRepo: AuthRepo) : ParentViewModel() {

    val removeSpecificDocumentLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    var data = MutableLiveData<JsonObject>()
    val tripRequest: MutableLiveData<TripRiderModel> by lazy { MutableLiveData<TripRiderModel>() }

    fun removeSpecificDocument(request: Request) {
        run {
            removeSpecificDocumentLiveData.value = Resource.loading()
            removeSpecificDocumentLiveData.value = authRepo.removeSpecificDocument(request)
        }
    }

    val resendOtpLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun resendOtp(request: Request) {
        run {
            resendOtpLiveData.value = Resource.loading()
            resendOtpLiveData.value = authRepo.resendOTP(request)
        }
    }

    val driverRegisterLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun driverRegister(request: Request) {
        run {
            driverRegisterLiveData.value = Resource.loading()
            driverRegisterLiveData.value = authRepo.driverRegister(request)
        }
    }

    val driverLoginLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun driverLogin(request: Request) {
        run {
            driverLoginLiveData.value = Resource.loading()
            driverLoginLiveData.value = authRepo.driverLogin(request)
        }
    }

    val sendOTPByMobileNumberLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun sendOTPByMobileNumber(request: Request) {
        run {
            sendOTPByMobileNumberLiveData.value = Resource.loading()
            sendOTPByMobileNumberLiveData.value = authRepo.sendOtpToMobileNumber(request)
        }
    }


    val verifyOtpLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun verifyOtp(request: Request) {
        run {
            verifyOtpLiveData.value = Resource.loading()
            verifyOtpLiveData.value = authRepo.verifyOtp(request)
        }
    }

    val updateProfileLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun updateProfile(request: Request) {
        run {
            updateProfileLiveData.value = authRepo.updateProfile(request)
        }
    }

    val updateProfileImageLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun updateProfileImage(body: RequestBody) {
        run {
            updateProfileImageLiveData.value = Resource.loading()
            updateProfileImageLiveData.value = authRepo.updateProfileImage(body)
        }
    }

    val uploadDocumentLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun updateDocument(body: RequestBody) {
        run {
            uploadDocumentLiveData.value = Resource.loading()
            uploadDocumentLiveData.value = authRepo.uploadDocument(body)
        }
    }


    val getDriverProfileLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun getDriverProfile() {
        run {
            getDriverProfileLiveData.value = Resource.loading()
            getDriverProfileLiveData.value = authRepo.getDriverProfile()
        }
    }


    val getDriverStatus by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getDriverStatus() {
        run {
            getDriverStatus.value = authRepo.getDriverStatus()
        }
    }

    val getCitiesLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getCities() {
        run {
            getCitiesLiveData.value = authRepo.getCities()
        }
    }

    val resetPasswordLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun resetPassword(request: Request) {
        run {
            resetPasswordLiveData.value = Resource.loading()
            resetPasswordLiveData.value = authRepo.resetPassword(request)
        }
    }

    val getRequiredAllDocumentLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getAllRequiredDocument() {
        run {
            getRequiredAllDocumentLiveData.value = Resource.loading()
            getRequiredAllDocumentLiveData.value = authRepo.getRequiredAllDocument()
        }
    }

    val getVehicleDocumentLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun getVehicleDocument() {
        run {
            getVehicleDocumentLiveData.value = Resource.loading()
            getVehicleDocumentLiveData.value = authRepo.getVehicleDocument()
        }
    }

    val getVehiclePhotosLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun getVehiclePhotos() {
        run {
            getVehiclePhotosLiveData.value = Resource.loading()
            getVehiclePhotosLiveData.value = authRepo.getVehiclePhotos()
        }
    }

    val getVehicleTypeLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun getVehicleType() {
        run {
            getVehicleTypeLiveData.value = Resource.loading()
            getVehicleTypeLiveData.value = authRepo.getVehicleType()
        }
    }

    val getVehicleDetailsLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun getVehicleDetails() {
        run {
            getVehicleDetailsLiveData.value = Resource.loading()
            getVehicleDetailsLiveData.value = authRepo.getVehicleDetails()
        }
    }

    val updateVehicleDetailsLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun updateVehicleDetails(request: Request) {
        run {
            updateVehicleDetailsLiveData.value = Resource.loading()
            updateVehicleDetailsLiveData.value = authRepo.updateVehicleDetails(request)
        }
    }

    val deleteUserImageLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun deleteUserImage() {
        run {
            deleteUserImageLiveData.value = Resource.loading()
            deleteUserImageLiveData.value = authRepo.deleteUserImage()
        }
    }

    val getHomeLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    var homeData: HomeDataModel? = null

    fun getHomeData() {
        run {
            getHomeLiveData.value = Resource.loading()
            val resource     = authRepo.getHomeScreenData()
            val homeUseCases = HomeUseCases()
            homeData =  homeUseCases.getHomeData(resource)
            getHomeLiveData.value = resource
        }
    }

    val updateCurrentLocationLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun updateCurrentLocation(request: Request) {
        run {
            updateCurrentLocationLiveData.value = Resource.loading()
            updateCurrentLocationLiveData.value = authRepo.updateCurrentLocation(request)
        }
    }

    val updateDriverAvalabilityLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun updateDriverAvalability(request: Request) {
        run {
            updateDriverAvalabilityLiveData.value = Resource.loading()
            updateDriverAvalabilityLiveData.value = authRepo.updateDriverAvalability(request)
        }
    }

    val getAllAddressLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getAllAddress() {
        run {
            getAllAddressLiveData.value = Resource.loading()
            getAllAddressLiveData.value = authRepo.getAllAddress()
        }
    }

    val _updateAddressLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val updateAddressLiveData: MutableLiveData<Resource<JsonObject>> get() = _updateAddressLiveData
    fun updateAddress(request: Request) {
        run {
            _updateAddressLiveData.value = Resource.loading()
            _updateAddressLiveData.value = authRepo.updateAddress(request)
        }
    }

    val _sharedData by lazy { MutableLiveData<Triple<String, String, String>>() }

    fun setSharedData(address: String, lat: String, long: String) {
        _sharedData.value = Triple(address, lat, long)
    }

    val updateDriverVerificationStatusLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun updateDriverVerificationStatus() {
        run {
            updateDriverVerificationStatusLiveData.value = Resource.loading()
            updateDriverVerificationStatusLiveData.value = authRepo.updateDriverVerificationStatus()
        }
    }

    val getAllRideLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val rideTabs by lazy { ArrayList<RideActivityTabs>() }

    fun getAllRideData(request: Map<String, String>) {
        run {
            getAllRideLiveData.value = Resource.loading()
            val response = authRepo.getAllTrips(request)
            val rideActivityUseCases = RideActivityUseCases()
            rideTabs.clear()
            rideTabs.addAll(response.data?.let {
                val rideActivityResponse = Gson().fromJson(it, RideActivityResponse::class.java)
                rideActivityResponse?.data?.filters?.takeIf { it.isNotEmpty() }?.let {
                    it.map {
                        RideActivityTabs(
                            title = it.vTitle, paramType = it.vSubFilterParam ?: ""
                        )
                    } as ArrayList<RideActivityTabs>?
                } ?: run {
                    rideActivityUseCases.getDefaultTabs()
                }
            } ?: run {
                rideActivityUseCases.getDefaultTabs()
            })
            getAllRideLiveData.value = response
        }
    }

    val getAllNotificationLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getAllNotificationData() {
        run {
            getAllNotificationLiveData.value = Resource.loading()
            getAllNotificationLiveData.value = authRepo.getAllNotification()
        }
    }

    val getRequestedPaymentTripsLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val requestedPaymentTrips by lazy { MutableLiveData<PaymentHistory?>() }

    fun getRequestedTripPayments(request: Map<String, String>) {
        run {
            getRequestedPaymentTripsLiveData.value = Resource.loading()
            val response = authRepo.getTripPayments(request)
            val tripPaymentUseCases = TripPaymentUseCases()
            requestedPaymentTrips.value = tripPaymentUseCases.getPaymentHistory(response)
            getRequestedPaymentTripsLiveData.value = response
        }
    }

    val getAllReviewLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getAllReviewAPI() {
        run {
            getAllReviewLiveData.value = Resource.loading()
            getAllReviewLiveData.value = authRepo.getAllReview()
        }
    }

    val getCanclletionReasonLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getCancelletionReasonAPI() {
        run {
            getCanclletionReasonLiveData.value = Resource.loading()
            getCanclletionReasonLiveData.value = authRepo.getCancellationReason()
        }
    }

    val acceptRequestLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun acceptRequestAPI(request: Request) {
        run {
            acceptRequestLiveData.value = Resource.loading()
            acceptRequestLiveData.value = authRepo.acceptRequest(request)
        }
    }

    val declineRequestLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun declineRequestAPI(request: Request) {
        run {
            declineRequestLiveData.value = Resource.loading()
            declineRequestLiveData.value = authRepo.declineRequest(request)
        }
    }

    val verifyTripLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    var popUp: PopUp? = null
    fun verifyTrip(request: Request) {
        run {
            verifyTripLiveData.value = Resource.loading()
            val response = authRepo.verifyTrip(request)
            val tripUseCases = TripUseCases()
            popUp = tripUseCases.getTripVerificationResult(response)
            verifyTripLiveData.value = response
        }
    }

    val updateArriveStatusLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun updateArriveStatus(request: Request) {
        run {
            updateArriveStatusLiveData.value = Resource.loading()
            updateArriveStatusLiveData.value = authRepo.updateArriveStatus(request)
        }
    }

    val cancleRideLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val cancleRideHomeLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val cancleRideScheduleLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun cancelRide(request: Request, screen: String) {
        if(screen.equals("home", true)) {
            run {
                cancleRideHomeLiveData.value = Resource.loading()
                cancleRideHomeLiveData.value = authRepo.cancelRide(request)
            }
        } else if(screen.equals("Schedule", true)) {
            run {
                cancleRideScheduleLiveData.value = Resource.loading()
                cancleRideScheduleLiveData.value = authRepo.cancelRide(request)
            }
        } else {
            run {
                cancleRideLiveData.value = Resource.loading()
                cancleRideLiveData.value = authRepo.cancelRide(request)
            }
        }
    }

    val getActiveRideLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val activeTrips by lazy { mutableListOf<Trips>() }

    fun getActiveRideData(request: Map<String, String>) {
        run {
            getActiveRideLiveData.value = Resource.loading()
            val response = authRepo.getAllTrips(request)
            val rideActivityUseCases = RideActivityUseCases()
            activeTrips.clear()
            activeTrips.addAll(rideActivityUseCases.getTrips(response))
            getActiveRideLiveData.value = response
        }
    }

    val getCompletedRideLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val completedTrips by lazy { mutableListOf<Trips>() }

    fun getCompletedRideData(request: Map<String, String>) {
        run {
            getCompletedRideLiveData.value = Resource.loading()
            val response = authRepo.getAllTrips(request)
            val rideActivityUseCases = RideActivityUseCases()
            completedTrips.clear()
            completedTrips.clear()
            completedTrips.addAll(rideActivityUseCases.getTrips(response))
            getCompletedRideLiveData.value = response
        }
    }

    val getCancelledRideLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val cancelledTrips by lazy { mutableListOf<Trips>() }

    fun getCancelledRideData(request: Map<String, String>) {
        run {
            getCancelledRideLiveData.value = Resource.loading()
            val response = authRepo.getAllTrips(request)
            val rideActivityUseCases = RideActivityUseCases()
            cancelledTrips.clear()
            cancelledTrips.addAll(rideActivityUseCases.getTrips(response))
            getCancelledRideLiveData.value = response
        }
    }

    val _pickupNoteLiveData = MutableLiveData<String>()
    val pickupNoteLiveData: LiveData<String> get() = _pickupNoteLiveData

    val _tripStartLiveData = MutableLiveData<Boolean>()
    val tripStartLiveData: LiveData<Boolean> get() = _tripStartLiveData

    val completeTripLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun completeTrip(request: Request) {
        run {
            completeTripLiveData.value = Resource.loading()
            val response = authRepo.completeTrip(request)
            completeTripLiveData.value = response
        }
    }

    val _paymentCollectedLiveData = MutableLiveData<TripRiderModel?>()
    val paymentCollectedLiveData: LiveData<TripRiderModel?> get() = _paymentCollectedLiveData

    val collectPaymentLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun collectTripPayment(request: Request) {
        run {
            collectPaymentLiveData.value = Resource.loading()
            val response = authRepo.collectTripPayment(request)
            collectPaymentLiveData.value = response
        }
    }

    val firebaseTokenLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun updateFirebaseToken(request: Request) {
        run {
            firebaseTokenLiveData.value = Resource.loading()
            val response = authRepo.updateFirebaseToken(request)
            firebaseTokenLiveData.value = response
        }
    }

    val getAcceptedPaymentLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getAcceptedPaymentAPI() {
        run {
            getAcceptedPaymentLiveData.value = Resource.loading()
            getAcceptedPaymentLiveData.value = authRepo.getAllNotification()
        }
    }

    val markAllReadNotificationLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun markAllReadNotifications() {
        run {
            markAllReadNotificationLiveData.value = Resource.loading()
            markAllReadNotificationLiveData.value = authRepo.markAllReadNotifications()
        }
    }

    val _tripStatusUpdatedLiveData = MutableLiveData<TripRiderModel>()
    val tripStatusUpdatedLiveData: LiveData<TripRiderModel> get() = _tripStatusUpdatedLiveData

    val getDriverPreferencesLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getDriverPreferences() {
        run {
            getDriverPreferencesLiveData.value = Resource.loading()
            getDriverPreferencesLiveData.value = authRepo.getDriverPreferences()
        }
    }

    val updateDriverPreferencesLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun updateDriverPreferences(request: Request) {
        run {
            updateDriverPreferencesLiveData.value = Resource.loading()
            updateDriverPreferencesLiveData.value = authRepo.updateDriverPreferences(request)
        }
    }

    val tripReportCrashLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    var location = Pair<LatLng, String>(LatLng(0.0, 0.0), "")

    fun tripReportCrash(request: RequestBody) {
        run {
            tripReportCrashLiveData.value = Resource.loading()
            tripReportCrashLiveData.value = authRepo.tripReportCrash(request)
        }
    }

    val getAcceptedPaymentTripsLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    var filterDate = ""
    fun getAcceptedTripPayments(request: Map<String, String>) {
        run {
            getAcceptedPaymentTripsLiveData.value = Resource.loading()
            val response = authRepo.getTripPayments(request)
            val tripPaymentUseCases = TripPaymentUseCases()
            requestedPaymentTrips.value = tripPaymentUseCases.getPaymentHistory(response)
            getAcceptedPaymentTripsLiveData.value = response
        }
    }

    val getActiveTripLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun tripConfigData() {
        run {
            getActiveTripLiveData.value = Resource.loading()
            getActiveTripLiveData.value = authRepo.tripConfigData()
        }
    }

    val locationLiveData by lazy { MutableLiveData<LatLng>() }

    val getScheduleTripLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    val scheduleTrips by lazy { mutableListOf<Trips>() }
    val scheduleTripAccepted by lazy { MutableLiveData<Boolean?>() }
    fun getScheduleTrips() {
        run {
            getScheduleTripLiveData.value = Resource.loading()
            val response = authRepo.getScheduleTrips()
            val scheduleTripUseCases = ScheduleTripUseCases()
            scheduleTrips.clear()
            scheduleTrips.addAll(scheduleTripUseCases.getScheduleTrips(response))
            getScheduleTripLiveData.value = response
        }
    }
}
