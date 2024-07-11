package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("name")
    val name: String?
)