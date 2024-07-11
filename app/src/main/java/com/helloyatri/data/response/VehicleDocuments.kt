package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class VehicleDocuments(
    @SerializedName("status")
    val status: Boolean?
)