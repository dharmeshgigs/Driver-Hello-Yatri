package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class VehicleDocuments(
    @SerializedName("status")
    val status: Boolean?
)