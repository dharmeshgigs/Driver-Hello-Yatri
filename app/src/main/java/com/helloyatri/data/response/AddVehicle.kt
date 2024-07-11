package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class AddVehicle(
    @SerializedName("status")
    val status: Boolean?
)