package com.helloyatri.network

import androidx.lifecycle.MutableLiveData
import com.gamingyards.sms.app.utils.Resource
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor(private val authRepo: AuthRepo) : ParentViewModel() {

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

}
