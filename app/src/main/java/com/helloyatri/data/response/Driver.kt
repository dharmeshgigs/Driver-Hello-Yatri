package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class Driver(
    @SerializedName("drive_in_city")
    val driveInCity: Any?,
    @SerializedName("gender")
    val gender: Any?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("profile_image")
    val profileImage: Any?,
    @SerializedName("referral_code")
    val referralCode: Any?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("user_id")
    val userId: String?
)