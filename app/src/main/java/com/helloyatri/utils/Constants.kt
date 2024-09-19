package com.helloyatri.utils

object Constants {
    const val MIN_NAME = 3
    const val MIN_PASSWORD = 8
    const val MIN_NUMBER = 10
    const val PASSWORD_REX: String = "^(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+*!=]).*\$"
//    const val PERSONAL_PROFILE_SCREEN = "1001"

    const val UPDATE_PROFILE_INFO = "1001"
    const val UPDATE_PROFILE_PICTURE = "1002"
    const val DRIVER_REQUIRED_DOCUMENT = "1003"
    const val ADD_VEHICLE = "1004"
    const val VEHICLE_DOCUMENT = "1005"
    const val VEHICLE_PHOTO = "1006"
    const val VERIFICATION_PENDING = "1007"
    const val VERIFICATION_COMPLETED = "1008"

    const val UPLOAD_DRIVING_LICENCE = "31"
    const val UPLOAD_GOVERNMENT_ID = "32"
    const val UPLOAD_BANK_DETAILS = "33"

    const val UPLOAD_VEHICLE_PUC = "51"
    const val UPLOAD_VEHICLE_INSURANCE = "52"
    const val UPLOAD_REGISTRATION_CERTIFICATION = "53"
    const val UPLOAD_VEHICLE_PERMIT = "54"

    const val UPLOAD_FRONTBACK_WITH_NUMBER_PLATE = "61"
    const val UPLOAD_LEFT_RIGHT_SIDE_EXTERIOR = "62"
    const val UPLOAD_CHASIS_NUMBER_IMAGES = "63"
    const val UPLOAD_YOUR_PHOTO_WITH_VEHICLE = "64"

    const val DRIVER_DOC = "1"
    const val VEHICLE_DOC = "2"
    const val VEHICLE_IMAGE = "3"
    const val REQUEST_CALL_PERMISSION = 1
    const val DRIVING_LICENCE = "Driving Licence"
    const val GOVERNMENT_ID = "Goverment Id"
    const val BANK_DETAILS = "Bank Details"
    const val VEHICLE_PUC = "Vehicle PUC"
    const val VEHICLE_INSURANCE = "Vehicle Insurance"
    const val VEHICLE_REGISTRATION_CERTIFICATE = "Vehicle Registration Certificate"
    const val VEHICLE_PERMIT = "Vehicle Permit"
    const val FRONT_BACK_WITH_NUMBER_PLAGE = "Front-back with Number plage"
    const val LEFT_RIGHT_SIDE_EXTERIOR = "Left-Right Side Exterior"
    const val CHASSIS_NUMBER_IMAGES = "Chassis Number Images"
    const val YOUR_PHOTO_WITH_VEHICLE = "Your photo with vehicle"
    const val Documents = "documents[]"
    const val Images = "images[]"
    const val PROFILE_IMAGE = "profile_image"
    const val PLACE_CATEGORY = 1


    // API Params
    const val PARAM_FILTER_PARAMETER = "filter_parameter"

    // TRIP Statuses
    const val CANCELLED = "CANCELLED"
    const val FINISHED = "FINISHED"

}