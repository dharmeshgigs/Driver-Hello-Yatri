package com.helloyatri.network

import androidx.lifecycle.MutableLiveData
import com.gamingyards.sms.app.utils.Resource
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.response.City
import com.helloyatri.data.response.Driver
import com.helloyatri.data.response.DriverStatus
import com.helloyatri.data.response.Login
import com.helloyatri.data.response.Otp
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body

interface AuthRepo {
    suspend fun resendOTP(request: Request) : Resource<JsonObject>
//    suspend fun driverRegister(request: Request) : Res<Any>
    suspend fun driverRegister(request: Request) : Resource<JsonObject>

    suspend fun driverLogin(request: Request) : Resource<JsonObject>

    suspend fun verifyOtp(request: Request) : Resource<JsonObject>

    suspend fun updateProfile(request: Request) : Res<Driver>

    suspend fun updateProfileImage(body : RequestBody) : Resource<JsonObject>

    suspend fun getDriverStatus() : Resource<JsonObject>

    suspend fun getCities() : Res<ArrayList<CommonFieldSelection>>

}