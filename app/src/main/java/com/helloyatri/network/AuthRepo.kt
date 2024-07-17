package com.helloyatri.network

import com.gamingyards.sms.app.utils.Resource
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
import okhttp3.RequestBody

interface AuthRepo {
    suspend fun resendOTP(request: Request) : Resource<JsonObject>
//    suspend fun driverRegister(request: Request) : Res<Any>
    suspend fun driverRegister(request: Request) : Resource<JsonObject>

    suspend fun driverLogin(request: Request) : Resource<JsonObject>

    suspend fun verifyOtp(request: Request) : Resource<JsonObject>

    suspend fun updateProfile(request: Request) : Resource<JsonObject>

    suspend fun updateProfileImage(body : RequestBody) : Resource<JsonObject>
    suspend fun uploadDocument(body : RequestBody) : Resource<JsonObject>

    suspend fun getDriverStatus() : Resource<JsonObject>
    suspend fun getDriverProfile() : Resource<JsonObject>

    suspend fun getCities() : Resource<JsonObject>
    suspend fun deleteUserImage() : Resource<JsonObject>
//    suspend fun getCities() : Resource<ArrayList<CommonFieldSelection>>

    suspend fun sendOtpToMobileNumber(request: Request) : Resource<JsonObject>
    suspend fun resetPassword(request: Request) : Resource<JsonObject>

    suspend fun getRequiredAllDocument() : Resource<JsonObject>
    suspend fun getVehicleDocument() : Resource<JsonObject>
    suspend fun getVehiclePhotos() : Resource<JsonObject>

    suspend fun removeSpecificDocument(request: Request) : Resource<JsonObject>


}