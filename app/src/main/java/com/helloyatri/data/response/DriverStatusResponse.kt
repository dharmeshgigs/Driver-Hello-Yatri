package com.helloyatri.data.response

import com.google.gson.annotations.SerializedName

data class DriverStatusResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: DriverStatus?
)
