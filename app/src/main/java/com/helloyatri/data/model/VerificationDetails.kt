package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class VerificationDetails (

    @SerializedName("LOCATION"        ) var LOCATION       : LOCATION?       = null,
    @SerializedName("CONTACT_DETAILS" ) var CONTACTDETAILS : CONTACTDETAILS? = null,
    @SerializedName("description"     ) var description    : String?         = null,
    @SerializedName("btn_lbl") var btn_lbl    : String?         = null,
    @SerializedName("TITLE") var TITLE    : String?         = null,
    @SerializedName("DESCRIPTION") var DESCRIPTION    : String?         = null,
    @SerializedName("AGENCY") var AGENCY    : Agency?         = null,

)

data class Agency (

    @SerializedName("LOCATION"        ) var LOCATION       : LOCATION?       = null,
    @SerializedName("CONTACT_DETAILS" ) var CONTACTDETAILS : CONTACTDETAILS? = null,
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