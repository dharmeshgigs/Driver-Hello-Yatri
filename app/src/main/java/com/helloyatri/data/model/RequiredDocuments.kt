package com.helloyatri.data.model


import com.google.gson.annotations.SerializedName

data class RequiredDocuments(
    @SerializedName("status")
    val status: Boolean?
)