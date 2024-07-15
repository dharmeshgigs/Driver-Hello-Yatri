package com.helloyatri.network

import com.gamingyards.sms.app.utils.Resource
import com.google.gson.JsonObject
import com.helloyatri.data.Request
import com.helloyatri.data.response.Driver
import com.helloyatri.data.response.DriverStatus
import com.helloyatri.data.response.Login
import com.helloyatri.data.response.Otp
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
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

    override suspend fun updateProfile(request: Request): Res<Driver> {
        return run { authService.updateProfile(request) }
    }

    override suspend fun updateProfileImage(request: RequestBody): Resource<JsonObject> {
        return run2 { authService.updateProfileImage(request) }
    }

    override suspend fun getDriverStatus(): Resource<JsonObject> {
        return run2 { authService.getDriverStatus() }
    }

    override suspend fun getDriverProfile(): Resource<JsonObject> {
        return run2 { authService.getDriverProfile() }
    }

    override suspend fun getCities(): Res<ArrayList<CommonFieldSelection>> {
        return run { authService.getCities() }
    }
}