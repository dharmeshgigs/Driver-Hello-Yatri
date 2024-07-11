package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class Otp(
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("otp")
    val otp: String
)