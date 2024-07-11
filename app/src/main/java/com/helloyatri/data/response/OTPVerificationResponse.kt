package com.helloyatri.data.response

import com.google.gson.annotations.SerializedName

data class OTPVerificationResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Driver?
)

