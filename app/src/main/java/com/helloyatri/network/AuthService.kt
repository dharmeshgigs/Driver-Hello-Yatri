package com.helloyatri.network

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
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {

    @POST(APIFactory.AuthApi.RESEND_OTP)
    suspend fun resendOTP(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.DRIVER_REGISTER)
    suspend fun driverRegister(@Body request: Request) : ResBody<Any>

    @POST(APIFactory.AuthApi.DRIVER_LOGIN)
    suspend fun driverLogin(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.VERIFY_OTP)
    suspend fun verifyOtp(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_PROFILE)
    suspend fun updateProfile(@Body request: Request) : ResBody<Driver>

    @POST(APIFactory.AuthApi.UPDATE_PROFILE)
    suspend fun updateProfileImage(@Body body : RequestBody) : ResBody<Any>

    @GET(APIFactory.AuthApi.GET_DRIVER_STATUS)
    suspend fun getDriverStatus() : Response<JsonObject>


    @GET(APIFactory.AuthApi.GET_CITIES)
    suspend fun getCities() : ResBody<ArrayList<CommonFieldSelection>>



}