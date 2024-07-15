package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class DriverStatusResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: DriverStatus?
)
