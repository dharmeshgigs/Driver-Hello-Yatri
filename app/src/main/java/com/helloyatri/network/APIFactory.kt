package com.helloyatri.network

import okhttp3.HttpUrl

object APIFactory {
    fun getHttpUrl() : HttpUrl {
        return HttpUrl.Builder()
            .scheme("http")
            .host("3.111.159.32")
            .addPathSegments("api/")
            .build()
    }

    object AuthApi {

        const val RESEND_OTP = "resend-otp"
        const val DRIVER_REGISTER = "driver/register"
        const val DRIVER_LOGIN = "driver/login"
        const val VERIFY_OTP = "verify-otp"
        const val GET_DRIVER_STATUS = "driver/get-driver-status"
        const val UPDATE_PROFILE = "update-profile"
        const val GET_CITIES = "getCities"

    }

    object ResponseCode {
        const val SUCCESS = 200
        const val UNAUTHORIZED = 401
    }
}