package com.helloyatri.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gamingyards.sms.app.utils.Resource
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.response.City
import com.helloyatri.data.response.Driver
import com.helloyatri.data.response.DriverStatus
import com.helloyatri.data.response.Login
import com.helloyatri.data.response.Otp
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
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

    val verifyOtpLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun verifyOtp(request: Request) {
        run {
            verifyOtpLiveData.value = Resource.loading()
            verifyOtpLiveData.value = authRepo.verifyOtp(request)
        }
    }

    val updateProfileLiveData by lazy { ResLiveData<Driver>() }

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


    val getDriverStatus by lazy { MutableLiveData<Resource<JsonObject>>() }

    fun getDriverStatus() {
        run {
            getDriverStatus.value = authRepo.getDriverStatus()
        }
    }

    val getCitiesLiveData by lazy { ResLiveData<ArrayList<CommonFieldSelection>>() }

    fun getCities() {
        run {
            getCitiesLiveData.value = authRepo.getCities()
        }
    }
}