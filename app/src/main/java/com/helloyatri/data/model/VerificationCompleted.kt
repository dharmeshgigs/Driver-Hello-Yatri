package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class VerificationCompleted(
    @SerializedName("status")
    val status: Boolean?
)