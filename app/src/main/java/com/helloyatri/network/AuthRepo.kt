package com.helloyatri.network

import com.google.gson.JsonObject
import com.helloyatri.data.Request
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
    suspend fun getVehicleType() : Resource<JsonObject>
    suspend fun getVehicleDetails() : Resource<JsonObject>

    suspend fun removeSpecificDocument(request: Request) : Resource<JsonObject>
    suspend fun updateVehicleDetails(request: Request) : Resource<JsonObject>
    suspend fun updateDriverAvalability(request: Request) : Resource<JsonObject>
    suspend fun updateCurrentLocation(request: Request) : Resource<JsonObject>
    suspend fun getHomeScreenData() : Resource<JsonObject>
    suspend fun getAllAddress() : Resource<JsonObject>
    suspend fun updateAddress(request: Request) : Resource<JsonObject>
    suspend fun updateDriverVerificationStatus() : Resource<JsonObject>
    suspend fun getAllRide() : Resource<JsonObject>
    suspend fun getAllNotification() : Resource<JsonObject>
    suspend fun getAllPayment() : Resource<JsonObject>
    suspend fun getAllReview() : Resource<JsonObject>
    suspend fun getAllScheduleRide() : Resource<JsonObject>
    suspend fun getCancellationReason() : Resource<JsonObject>
    suspend fun acceptRequest(request: Request) : Resource<JsonObject>
    suspend fun declineRequest(request: Request) : Resource<JsonObject>
    suspend fun verifyTrip(request: Request) : Resource<JsonObject>
    suspend fun updateArriveStatus(request: Request) : Resource<JsonObject>
    suspend fun cancelRide(request: Request) : Resource<JsonObject>
    suspend fun getAllTrips(request: Map<String, String>) : Resource<JsonObject>
    suspend fun completeTrip(request: Request) : Resource<JsonObject>
    suspend fun collectTripPayment(request: Request) : Resource<JsonObject>
    suspend fun updateFirebaseToken(request: Request) : Resource<JsonObject>
    suspend fun markAllReadNotifications() : Resource<JsonObject>
    suspend fun getDriverPreferences() : Resource<JsonObject>
    suspend fun updateDriverPreferences(request: Request) : Resource<JsonObject>
    suspend fun getTripPayments(request: Map<String, String>) : Resource<JsonObject>
    suspend fun tripReportCrash(request: RequestBody) : Resource<JsonObject>
    suspend fun tripConfigData() : Resource<JsonObject>

}