package com.helloyatri.network

import okhttp3.HttpUrl

object APIFactory {

//    https://koolmindapps.com/Yatriapp/api/
    fun getHttpUrl() : HttpUrl {
        return HttpUrl.Builder()
            .scheme("http")
            .host("3.111.159.32")
            .addPathSegments("api/")
            .build()
    }

    object AuthApi {

        const val PUSHER_AUTH_URL = "http://3.111.159.32/api/pusher/auth"
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
        const val GET_VEHICLE_TYPE = "driver/vehicle-types"
        const val GET_VEHICLE_DETAILS = "driver/get-vehicle"
        const val UPDATE_VEHICLE_DETAILS = "driver/store_vehicle"
        const val GET_HOME_DATA = "getHomeScreenData"
        const val UPDATE_CURRENT_LOCATION = "updateCurrentLoccation"
        const val UPDATE_DRIVER_AVALABILITY = "driver/updateDriverAvailability"

        const val UPDATE_ADDRESS = "address"
        const val UPDATE_DRIVER_VERIFICATION_STATUS = "driver/update-driver-verification-status"
        const val ACCEPT_REQUEST_TEST = "driver/acceptRequest"
        const val DECLINE_REQUEST_TEST = "driver/declineRequest"
        const val VERIFY_TRIP = "driver/verifyTrip"
        const val UPDATE_ARRIVE_STATUS = "driver/updateArriveStatus"
        const val CANCLE_RIDE = "cancelRide"
        const val TRIP_COMPLETE = "driver/completeTrip"

        const val GET_TRIPS = "getTrips"
        const val GET_ALL_RIDE = "getallride"
        const val GET_ALL_NOTIFICATION = "getNotifications"
        const val GET_ALL_PAYMENT = "getAllPayment"
        const val GET_ALL_REVIEW = "getReviewRatings"
        const val GET_ALL_SCHEDULE_RIDE = "getAllScheduleRide"
        const val GET_CANCLLETION_REASON = "getCancellationReasons"
        const val GET_ALL_ADDRESS = "address"
        const val COLLECT_TRIP_PAYMENT = "driver/collectTripPayment"
        const val UPDATE_FIREBASE_TOKEN = "updateFirebaseToken"
        const val UPDATE_NOTIFICATIONS_AS_MARK = "markAllNotificationAsRead"
        const val GET_DRIVER_PREFERENCES = "driver/get-driver-preferences"
        const val UPDATE_DRIVER_PREFERENCES = "driver/update-driver-preferences"

        const val GET_TRIP_PAYMENTS = "getTripPaymentList"
        const val REPORT_CRASH = "reportCrash"
        const val GET_TRIP_CONFIG_DATA = "getTripConfigData"
        const val GET_SCHEDULE_TRIPS = "getScheduledTrips"
    }

    object ResponseCode {
        const val SUCCESS = 200
        const val UNAUTHORIZED = 401
    }
}