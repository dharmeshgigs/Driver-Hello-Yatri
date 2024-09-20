package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class ReportCrashImageDataModel(
    @SerializedName("image")
    val image: String,
)