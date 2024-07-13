package com.helloyatri.data

import com.google.gson.annotations.SerializedName

data class Request(
 @SerializedName("type") var type : String? = null,
    @SerializedName("mobile") var mobile : String? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("user_id") var userId : String? = null,
    @SerializedName("password") var password : String? = null,
    @SerializedName("otp") var otp : String? = null,
   @SerializedName("gender") var gender : String? = null,
 @SerializedName("drive_in_city") var driverInCity : String? = null
)