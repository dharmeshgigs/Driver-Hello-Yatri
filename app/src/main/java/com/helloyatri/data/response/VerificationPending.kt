package com.helloyatri.data.response


import com.google.gson.annotations.SerializedName

data class VerificationPending(
    @SerializedName("status")
    val status: Boolean?,
    @SerializedName("details")
    val details: Details?
)

data class Details (

    @SerializedName("LOCATION"        ) var LOCATION       : LOCATION?       = null,
    @SerializedName("CONTACT_DETAILS" ) var CONTACTDETAILS : CONTACTDETAILS? = null,
    @SerializedName("description"     ) var description    : String?         = null,
    @SerializedName("btn_lbl") var btn_lbl    : String?         = null

)

data class LOCATION (

    @SerializedName("ADDRESS"   ) var ADDRESS   : String? = null,
    @SerializedName("LATITUDE"  ) var LATITUDE  : String? = null,
    @SerializedName("LONGITUDE" ) var LONGITUDE : String? = null,
    @SerializedName("BTN_LBL"   ) var BTNLBL    : String? = null
)

data class CONTACTDETAILS (

    @SerializedName("BTN_LBL"         ) var BTNLBL         : String?           = null,
    @SerializedName("CONTACT_NUMBERS" ) var CONTACTNUMBERS : ArrayList<String>? = null

)