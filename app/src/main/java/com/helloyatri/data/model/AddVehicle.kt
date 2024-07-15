package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class AddVehicle(
    @SerializedName("status")
    val status: Boolean?
)