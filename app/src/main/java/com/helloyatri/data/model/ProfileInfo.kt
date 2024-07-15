package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class ProfileInfo(
    @SerializedName("status")
    val status: Boolean?
)