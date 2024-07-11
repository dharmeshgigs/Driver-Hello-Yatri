package com.helloyatri.core

import com.helloyatri.data.User
import com.helloyatri.data.response.Driver

interface Session {

    var apiKey: String

    var userSession: String

    var userId: String

    val deviceId: String

    var deviceToken: String

    var user: Driver?

    val language: String

    var isInitial: Boolean

    var isDriverVerified: Boolean

    var isLoggedIn: Boolean?

    var isPersonalDetailsAdded: Boolean

    var isProfilePictureAdded: Boolean

    var isRequiredDocumentsAdded: Boolean

    var isDrivingLicenseAdded: Boolean

    var isGovernmentIDAdded: Boolean

    var isBankAccountDetailsAdded: Boolean

    var isAddVehicle: Boolean

    var isVehicleDocumentsAdded: Boolean

    var isVehiclePUCAdded: Boolean

    var isVehicleInsuranceAdded: Boolean

    var isVehicleRegistrationAdded: Boolean

    var isVehiclePermitAdded: Boolean

    var isVehiclePhotosAdded: Boolean

    var isVehicleFrontBackPhotoAdded: Boolean

    var isVehicleLeftRightPhotoAdded: Boolean

    var isVehicleChassisAdded: Boolean

    var isVehicleWithYourPhotoAdded: Boolean

    fun clearSession()

    fun isAllDocumentUploaded() : Boolean

    companion object {
        const val API_KEY = "api-key"
        const val USER_SESSION = "Authorization"
        const val USER_ID = "USER_ID"
        const val DEVICE_TYPE = "A"
        const val LANGUAGE = "accept-language"
        const val IS_INITIAL = "isInitial"
        const val IS_LOGGED_IN = "isLoggedIn"
        const val IS_PAYMENT_SKIP = "isPaymentSkipped"
        const val IS_AWARDS_ACHIEVED = "isAwardsAchieved"
        const val DEVICE_TOKEN = "deviceToken"
        const val CURRENT_CITY = "currentCity"
        const val DRIVER_VERIFIED = "driverVerified"
        const val PERSONAL_DETAILS = "personalDetails"
        const val PROFILE_PICTURE = "profilePicture"
        const val REQUIRED_DOCUMENTS = "requiredDocuments"
        const val DRIVING_LICENSE = "drivingLicense"
        const val GOVERNMENT_ID = "governmentID"
        const val BANK_DETAILS = "bankDetails"
        const val ADD_VEHICLE = "addVehicle"
        const val VEHICLE_DOCUMENTS = "vehicleDocuments"
        const val VEHICLE_PUC = "vehiclePUC"
        const val VEHICLE_INSURANCE = "vehicleInsurance"
        const val VEHICLE_REGISTRATION = "vehicleRegistration"
        const val VEHICLE_PERMIT = "vehiclePermit"
        const val VEHICLE_PHOTOS = "vehiclePhotos"
        const val VEHICLE_FRONT_BACK_PHOTO = "vehicleFrontBackPhoto"
        const val VEHICLE_LEFT_RIGHT_PHOTO = "vehicleLeftRightPhoto"
        const val VEHICLE_CHASSIS = "vehicleChassis"
        const val VEHICLE_PHOTO_WITH_YOUR = "vehiclePhotoWithYour"
    }
}
