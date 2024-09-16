package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class DriverStatus(
    @SerializedName("add_vehicle")
    val addVehicle: AddVehicle?,
    @SerializedName("profile_image")
    val profileImage: ProfileImage?,
    @SerializedName("profile_info")
    val profileInfo: ProfileInfo?,
    @SerializedName("required_documents")
    val requiredDocuments: RequiredDocuments?,
    @SerializedName("vehicle_documents")
    val vehicleDocuments: VehicleDocuments?,
    @SerializedName("vehicle_images")
    val vehicleImages: VehicleImages?,
    @SerializedName("verification_completed")
    val verificationCompleted: VerificationCompleted?,
    @SerializedName("verification_pending")
    val verificationPending: VerificationPending?,
    @SerializedName("CHANGE_DETAILS")
    val CHANGE_DETAILS: VerificationDetails
)