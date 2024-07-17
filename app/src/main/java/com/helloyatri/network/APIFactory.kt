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
        const val GET_PROFILE = "profile"
        const val SEND_OTP_MOBILE_NUMBER = "login"
        const val RESET_PASSWORD = "driver/resetPassword"
        const val GET_REQUIRED_ALL_DOCUMENT = "driver/required-documents"
        const val GET_VEHICLE_DOCUMENT = "driver/vehicle-documents"
        const val GET_VEHICLE_PHOTOS = "driver/vehicle-photos"
        const val UPLOAD_DOCUMENT = "driver/upload-document"
        const val DELETE_USER_PROFILE = "profile"
        const val REMOVE_SPECIFIC_DOCUMENT = "driver/remove-uploaded-document"

//        const val RESEND_OTP = "driver/resetPassword"

    }

    object ResponseCode {
        const val SUCCESS = 200
        const val UNAUTHORIZED = 401
    }
}