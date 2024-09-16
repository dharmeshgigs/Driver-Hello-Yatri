package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class VerificationPending(
    @SerializedName("status")
    val status: Boolean?,
    @SerializedName("details")
    val details: VerificationDetails?
)