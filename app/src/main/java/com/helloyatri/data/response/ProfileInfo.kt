package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class ProfileInfo(
    @SerializedName("status")
    val status: Boolean?
)