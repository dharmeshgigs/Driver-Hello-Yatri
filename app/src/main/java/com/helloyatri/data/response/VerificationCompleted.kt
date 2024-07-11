package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class VerificationCompleted(
    @SerializedName("status")
    val status: Boolean?
)