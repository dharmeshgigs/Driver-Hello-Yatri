package com.helloyatri.network

import com.google.gson.JsonObject
import com.helloyatri.data.Request
import okhttp3.RequestBody
import javax.inject.Inject

class ApiDataSource @Inject constructor(
    private val authService: AuthService
) : ParentDataSource(), AuthRepo {

    override suspend fun resendOTP(request: Request): Resource<JsonObject> {
        return run2 { authService.resendOTP(request) }
    }

    override suspend fun driverRegister(request: Request): Resource<JsonObject> {
        return run2 { authService.driverRegister(request) }
    }

    override suspend fun driverLogin(request: Request): Resource<JsonObject> {
        return run2 { authService.driverLogin(request) }
    }

    override suspend fun verifyOtp(request: Request): Resource<JsonObject> {
        return run2 { authService.verifyOtp(request) }
    }

    override suspend fun updateProfile(request: Request): Resource<JsonObject> {
        return run2 { authService.updateProfile(request) }
    }

    override suspend fun updateProfileImage(request: RequestBody): Resource<JsonObject> {
        return run2 { authService.updateProfileImage(request) }
    }

    override suspend fun uploadDocument(body: RequestBody): Resource<JsonObject> {
        return run2 { authService.uploadDocument(body) }
    }

    override suspend fun getDriverStatus(): Resource<JsonObject> {
        return run2 { authService.getDriverStatus() }
    }

    override suspend fun getDriverProfile(): Resource<JsonObject> {
        return run2 { authService.getDriverProfile() }
    }

    override suspend fun getCities(): Resource<JsonObject> {
        return run2 { authService.getCities() }
    }

    override suspend fun deleteUserImage(): Resource<JsonObject> {
        return run2 { authService.deleteUserImage() }
    }

    override suspend fun sendOtpToMobileNumber(request: Request): Resource<JsonObject> {
        return run2 {
            authService.sendOtpToMobileNumber(request)
        }
    }

    override suspend fun resetPassword(request: Request): Resource<JsonObject> {
        return run2 {
            authService.resetPassword(request)
        }
    }

    override suspend fun getRequiredAllDocument(): Resource<JsonObject> {
        return run2 { authService.getRequiredAllDocument() }
    }

    override suspend fun getVehicleDocument(): Resource<JsonObject> {
        return run2 { authService.getVehicleDocument() }
    }

    override suspend fun getVehiclePhotos(): Resource<JsonObject> {
        return run2 { authService.getVehiclePhotos() }
    }

    override suspend fun getVehicleType(): Resource<JsonObject> {
        return run2 { authService.getVehicleType() }
    }

    override suspend fun getVehicleDetails(): Resource<JsonObject> {
        return run2 { authService.getVehicleDetails() }
    }

    override suspend fun removeSpecificDocument(request: Request): Resource<JsonObject> {
        return run2 { authService.removeSpecificDocument(request) }
    }

    override suspend fun updateVehicleDetails(request: Request): Resource<JsonObject> {
        return run2 { authService.updateVehicleDetails(request) }
    }

    override suspend fun updateDriverAvalability(request: Request): Resource<JsonObject> {
        return run2 { authService.updateDriverAvalability(request) }
    }

    override suspend fun updateCurrentLocation(request: Request): Resource<JsonObject> {
        return run2 { authService.updateCurrentLocation(request) }
    }

    override suspend fun getHomeScreenData(): Resource<JsonObject> {
        return run2 { authService.getHomeScreenData() }
    }

    override suspend fun getAllAddress(): Resource<JsonObject> {
        return run2 { authService.getAllAddress() }
    }

    override suspend fun updateAddress(request: Request): Resource<JsonObject> {
        return run2 { authService.updateAddress(request) }
    }

    override suspend fun updateDriverVerificationStatus(): Resource<JsonObject> {
        return run2 { authService.updateDriverVerificationStatus() }
    }

    override suspend fun getAllRide(): Resource<JsonObject> {
        return run2 { authService.getAllRide() }
    }

    override suspend fun getAllNotification(): Resource<JsonObject> {
        return run2 { authService.getAllNotification() }
    }

    override suspend fun getAllPayment(): Resource<JsonObject> {
        return run2 { authService.getAllPayment() }
    }

    override suspend fun getAllReview(): Resource<JsonObject> {
        return run2 { authService.getAllReview() }
    }

    override suspend fun getAllScheduleRide(): Resource<JsonObject> {
        return run2 { authService.getAllScheduleRide() }
    }

    override suspend fun getCancellationReason(): Resource<JsonObject> {
        return run2 { authService.getCancellationReason() }
    }

    override suspend fun acceptRequest(request: Request): Resource<JsonObject> {
        return run2 { authService.acceptRequest(request) }
    }

    override suspend fun declineRequest(request: Request): Resource<JsonObject> {
        return run2 { authService.declineRequest(request) }
    }

    override suspend fun verifyTrip(request: Request): Resource<JsonObject> {
        return run2 { authService.verifyTrip(request) }
    }

    override suspend fun updateArriveStatus(request: Request): Resource<JsonObject> {
        return run2 { authService.updateArriveStatus(request) }
    }
}