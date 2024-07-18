package com.helloyatri.network

import com.gamingyards.sms.app.utils.Resource
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.model.Driver
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
}