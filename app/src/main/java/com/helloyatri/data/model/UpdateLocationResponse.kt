package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName
data class UpdateLocationResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Address?
)
data class Address(
    @SerializedName("address")
    val address: String?
)