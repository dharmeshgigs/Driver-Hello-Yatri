package com.helloyatri.network

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.helloyatri.data.model.IntegerAdapter

open class ResBody<T>(
    @JsonAdapter(IntegerAdapter::class)
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("status") val status : String?,
    @SerializedName("data") val data : T?)



