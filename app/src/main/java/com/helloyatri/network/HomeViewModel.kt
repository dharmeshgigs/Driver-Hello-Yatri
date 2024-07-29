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
class HomeViewModel @Inject constructor(private val authRepo: AuthRepo) : ParentViewModel() {

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


    val getDriverProfileLiveData by lazy { MutableLiveData<Resource<JsonObject>>() }
    fun getDriverProfile() {
        run {
            getDriverProfileLiveData.value = Resource.loading()
            getDriverProfileLiveData.value = authRepo.getDriverProfile()
        }
    }

}
