package com.helloyatri.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Login?
)

data class Login(
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("mobile")
    var mobile: String? = null
)