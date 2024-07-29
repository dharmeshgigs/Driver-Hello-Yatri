package com.helloyatri.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import com.helloyatri.data.model.SavedAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor(private val authRepo: AuthRepo) : ParentViewModel() {

    val removeSpecificDocumentLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

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

    //    val getCitiesLiveData by lazy { ResLiveData<ArrayList<CommonFieldSelection>>() }
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

    fun getHomeData() {
        run {
            getHomeLiveData.value = Resource.loading()
            getHomeLiveData.value = authRepo.getHomeScreenData()
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

    val _sharedData by lazy { MutableLiveData<Triple<String,String,String>>() }

    fun setSharedData(address: String, lat: String, long: String) {
        _sharedData.value = Triple(address,lat,long)
    }

    val updateDriverVerificationStatusLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun updateDriverVerificationStatus() {
        run {
            updateDriverVerificationStatusLiveData.value = Resource.loading()
            updateDriverVerificationStatusLiveData.value = authRepo.updateDriverVerificationStatus()
        }
    }

    val getAllRideLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getAllRideData() {
        run {
            getAllRideLiveData.value = Resource.loading()
            getAllRideLiveData.value = authRepo.getAllRide()
        }
    }

    val getAllNotificationLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getAllNotificationData() {
        run {
            getAllNotificationLiveData.value = Resource.loading()
            getAllNotificationLiveData.value = authRepo.getAllNotification()
        }
    }

}
