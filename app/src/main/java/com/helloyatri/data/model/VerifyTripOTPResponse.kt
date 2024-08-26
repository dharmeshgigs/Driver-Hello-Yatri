package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class VerifyTripOTPResponse (
    @SerializedName("status"  ) var status  : String? = null,
    @SerializedName("code"    ) var code    : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : PopUp?   = null
)

data class PopUp (
    @SerializedName("waiting_time"     ) var waitingTime    : Int?    = null,
    @SerializedName("POPUP_TITLE"      ) var POPUPTITLE     : String? = null,
    @SerializedName("POPUP_MESSAGE"    ) var POPUPMESSAGE   : String? = null,
    @SerializedName("POPUP_BUTTON_LBL" ) var POPUPBUTTONLBL : String? = null,
    var status : String? = null
)