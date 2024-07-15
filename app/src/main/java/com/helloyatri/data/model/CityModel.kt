package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection

data class CityModel(
    @SerializedName("status") var status  : String?         = null,
    @SerializedName("code") var code    : Int?            = null,
    @SerializedName("message") var message : String?         = null,
    @SerializedName("data") var data    : ArrayList<CommonFieldSelection> = arrayListOf()
)