package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("name")
    val name: String?
)