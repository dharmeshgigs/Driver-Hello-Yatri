package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class RequiredDocuments(
    @SerializedName("status")
    val status: Boolean?
)