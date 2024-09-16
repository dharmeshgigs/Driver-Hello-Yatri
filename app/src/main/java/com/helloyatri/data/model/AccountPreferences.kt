package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class PreferencesResponse(
    @SerializedName("status"  ) var status  : String?         = null,
    @SerializedName("code"    ) var code    : Int?            = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<Preference>? = null
)
data class Preference (
    @SerializedName("key"   ) var key   : String? = null,
    @SerializedName("title" ) var title : String? = null,
    @SerializedName("value" ) var value : String? = null
)