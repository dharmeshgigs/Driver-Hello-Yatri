package com.helloyatri.core

import android.content.Context
import android.provider.Settings
import com.google.gson.Gson
import com.helloyatri.data.User
import com.helloyatri.data.response.Details
import com.helloyatri.data.response.Driver
import com.helloyatri.di.DiConstants
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AppSession @Inject constructor(
    private val appPreferences: AppPreferences,
    private val context: Context,
    @Named(DiConstants.API_KEY) override var apiKey: String
) :
    Session {

    private val gson: Gson = Gson()

    override var user: Driver? = null
        get() {
            if (field == null) {
                val userJSON = appPreferences.getString(USER_JSON)
                field = gson.fromJson(userJSON, Driver::class.java)
            }
            return field
        }
        set(value) {
            field = value
            val userJson = gson.toJson(value)
            if (userJson != null) appPreferences.putString(USER_JSON, userJson)
        }


    override var userSession: String
        get() = appPreferences.getString(Session.USER_SESSION)
        set(userSession) = appPreferences.putString(Session.USER_SESSION, userSession)


    override var userId: String
        get() = appPreferences.getString(Session.USER_ID)
        set(userId) = appPreferences.putString(Session.USER_ID, userId)


    override/* open below comment after Firebase integration *///token = FirebaseInstanceId.getInstance().getToken();
    val deviceId: String
        get() {
            var token = ""
            if (token.isEmpty()) token =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

            return token
        }

    override var deviceToken: String
        get() = appPreferences.getString(Session.DEVICE_TOKEN)
        set(value) {
            appPreferences.putString(Session.DEVICE_TOKEN, value)
        }

    override var isInitial: Boolean
        get() = appPreferences.getBoolean(Session.IS_INITIAL, false)
        set(value) = appPreferences.putBoolean(Session.IS_INITIAL, value)

    override var isDriverVerified: Boolean
        get() = appPreferences.getBoolean(Session.DRIVER_VERIFIED, false)
        set(value) = appPreferences.putBoolean(Session.DRIVER_VERIFIED, value)

    override var isLoggedIn: Boolean?
        get() = appPreferences.getBoolean(Session.IS_LOGGED_IN)
        set(value) {
            value?.let { appPreferences.putBoolean(Session.IS_LOGGED_IN, it) }
        }

    override var isPersonalDetailsAdded: Boolean
        get() = appPreferences.getBoolean(Session.PERSONAL_DETAILS, false)
        set(value) = appPreferences.putBoolean(Session.PERSONAL_DETAILS, value)

    override var isProfilePictureAdded: Boolean
        get() = appPreferences.getBoolean(Session.PROFILE_PICTURE, false)
        set(value) = appPreferences.putBoolean(Session.PROFILE_PICTURE, value)

    override var isRequiredDocumentsAdded: Boolean
        get() = appPreferences.getBoolean(Session.REQUIRED_DOCUMENTS, false)
        set(value) = appPreferences.putBoolean(Session.REQUIRED_DOCUMENTS, value)

    override var isAddVehicle: Boolean
        get() = appPreferences.getBoolean(Session.ADD_VEHICLE, false)
        set(value) = appPreferences.putBoolean(Session.ADD_VEHICLE, value)

    override var isVehicleDocumentsAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_DOCUMENTS, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_DOCUMENTS, value)

    override var isVehiclePUCAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_PUC, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_PUC, value)

    override var isVehicleInsuranceAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_INSURANCE, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_INSURANCE, value)

    override var isVehicleRegistrationAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_REGISTRATION, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_REGISTRATION, value)

    override var isVehiclePermitAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_PERMIT, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_PERMIT, value)

    override var isDrivingLicenseAdded: Boolean
        get() = appPreferences.getBoolean(Session.DRIVING_LICENSE, false)
        set(value) = appPreferences.putBoolean(Session.DRIVING_LICENSE, value)

    override var isGovernmentIDAdded: Boolean
        get() = appPreferences.getBoolean(Session.GOVERNMENT_ID, false)
        set(value) = appPreferences.putBoolean(Session.GOVERNMENT_ID, value)

    override var isBankAccountDetailsAdded: Boolean
        get() = appPreferences.getBoolean(Session.BANK_DETAILS, false)
        set(value) = appPreferences.putBoolean(Session.BANK_DETAILS, value)

    override var isVehiclePhotosAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_PHOTOS, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_PHOTOS, value)

    override var isVehicleFrontBackPhotoAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_FRONT_BACK_PHOTO, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_FRONT_BACK_PHOTO, value)

    override var isVehicleLeftRightPhotoAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_LEFT_RIGHT_PHOTO, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_LEFT_RIGHT_PHOTO, value)

    override var isVehicleChassisAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_CHASSIS, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_CHASSIS, value)

    override var isVehicleWithYourPhotoAdded: Boolean
        get() = appPreferences.getBoolean(Session.VEHICLE_PHOTO_WITH_YOUR, false)
        set(value) = appPreferences.putBoolean(Session.VEHICLE_PHOTO_WITH_YOUR, value)

    override var verificationDetails: Details?
        get() {
            val data = appPreferences.getString(Session.VERIFICATION_DETAILS)
            if (data.isNullOrEmpty()) {
                return null
            }
            val details = Gson().fromJson(data, Details::class.java)
            return details
        }
        set(value) {
            appPreferences.putString(Session.VERIFICATION_DETAILS, Gson().toJson(value))
        }

    override val language: String
        get() = "en"

    override fun clearSession() {
        appPreferences.clearAll()
    }

    companion object {
        const val USER_JSON = "user_json"
    }

    override fun isAllDocumentUploaded(): Boolean {
        return isPersonalDetailsAdded &&
                isProfilePictureAdded &&
                isRequiredDocumentsAdded &&
                isAddVehicle &&
                isVehicleDocumentsAdded &&
                isVehiclePhotosAdded
    }
}
