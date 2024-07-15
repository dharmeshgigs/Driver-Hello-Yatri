package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class ProfileImage(
    @SerializedName("status")
    val status: Boolean?
)