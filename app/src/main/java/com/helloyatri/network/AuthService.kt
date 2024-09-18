package com.helloyatri.network

import com.google.gson.JsonObject
import com.helloyatri.data.Request
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface AuthService {

    @POST(APIFactory.AuthApi.RESEND_OTP)
    suspend fun resendOTP(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.DRIVER_REGISTER)
    suspend fun driverRegister(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.DRIVER_LOGIN)
    suspend fun driverLogin(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.VERIFY_OTP)
    suspend fun verifyOtp(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_PROFILE)
    suspend fun updateProfile(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_PROFILE)
    suspend fun updateProfileImage(@Body request : RequestBody) : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_DRIVER_STATUS)
    suspend fun getDriverStatus() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_PROFILE)
    suspend fun getDriverProfile() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_CITIES)
    suspend fun getCities() : Response<JsonObject>

    @DELETE(APIFactory.AuthApi.DELETE_USER_PROFILE)
    suspend fun deleteUserImage(): Response<JsonObject>

    @POST(APIFactory.AuthApi.SEND_OTP_MOBILE_NUMBER)
    suspend fun sendOtpToMobileNumber(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.RESET_PASSWORD)
    suspend fun resetPassword(@Body request: Request) : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_REQUIRED_ALL_DOCUMENT)
    suspend fun getRequiredAllDocument() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_VEHICLE_DOCUMENT)
    suspend fun getVehicleDocument() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_VEHICLE_PHOTOS)
    suspend fun getVehiclePhotos() : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPLOAD_DOCUMENT)
    suspend fun uploadDocument(@Body request : RequestBody) : Response<JsonObject>

    @POST(APIFactory.AuthApi.REMOVE_SPECIFIC_DOCUMENT)
    suspend fun removeSpecificDocument(@Body request: Request) : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_VEHICLE_TYPE)
    suspend fun getVehicleType() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_VEHICLE_DETAILS)
    suspend fun getVehicleDetails() : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_VEHICLE_DETAILS)
    suspend fun updateVehicleDetails(@Body request: Request) : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_HOME_DATA)
    suspend fun getHomeScreenData() : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_CURRENT_LOCATION)
    suspend fun updateCurrentLocation(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_DRIVER_AVALABILITY)
    suspend fun updateDriverAvalability(@Body request: Request) : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_ALL_ADDRESS)
    suspend fun getAllAddress() : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_ADDRESS)
    suspend fun updateAddress(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_DRIVER_VERIFICATION_STATUS)
    suspend fun updateDriverVerificationStatus() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_ALL_RIDE)
    suspend fun getAllRide() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_ALL_NOTIFICATION)
    suspend fun getAllNotification() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_ALL_PAYMENT)
    suspend fun getAllPayment() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_ALL_REVIEW)
    suspend fun getAllReview() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_ALL_SCHEDULE_RIDE)
    suspend fun getAllScheduleRide() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_CANCLLETION_REASON)
    suspend fun getCancellationReason() : Response<JsonObject>

    @POST(APIFactory.AuthApi.ACCEPT_REQUEST_TEST)
    suspend fun acceptRequest(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.DECLINE_REQUEST_TEST)
    suspend fun declineRequest(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.VERIFY_TRIP)
    suspend fun verifyTrip(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_ARRIVE_STATUS)
    suspend fun updateArriveStatus(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.CANCLE_RIDE)
    suspend fun cancelRide(@Body request: Request) : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_TRIPS)
    suspend fun getAllTrips(@QueryMap request: Map<String, String>) : Response<JsonObject>

    @POST(APIFactory.AuthApi.TRIP_COMPLETE)
    suspend fun completeTrip(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.COLLECT_TRIP_PAYMENT)
    suspend fun collectTripPayment(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_FIREBASE_TOKEN)
    suspend fun updateFirebaseToken(@Body request: Request) : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_NOTIFICATIONS_AS_MARK)
    suspend fun markAllReadNotifications() : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_DRIVER_PREFERENCES)
    suspend fun getDriverPreferences() : Response<JsonObject>

    @POST(APIFactory.AuthApi.UPDATE_DRIVER_PREFERENCES)
    suspend fun updateDriverPreferences(@Body request: Request) : Response<JsonObject>

    @GET(APIFactory.AuthApi.GET_TRIP_PAYMENTS)
    suspend fun getTripPayments(@QueryMap request: Map<String, String>) : Response<JsonObject>

    @POST(APIFactory.AuthApi.REPORT_CRASH)
    suspend fun tripReportCrash(@Body request: Request) : Response<JsonObject>
}