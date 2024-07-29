package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName
data class DriverResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Driver?
)
data class Driver(
    @SerializedName("drive_in_city")
    val driveInCity: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("referral_code")
    val referralCode: Any?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("id")
    val id: Int?
)