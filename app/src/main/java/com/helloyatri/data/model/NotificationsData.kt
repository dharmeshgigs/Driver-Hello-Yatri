package com.helloyatri.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class NotificationsResponse (

    @SerializedName("status"  ) var status  : String?         = null,
    @SerializedName("code"    ) var code    : Int?            = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<NotificationsData>? = null

)

@Parcelize
    data class NotificationsData(
    @SerializedName("id"         ) var id        : String?  = null,
    @SerializedName("title"      ) var title     : String?  = null,
    @SerializedName("message"    ) var message   : String?  = null,
    @SerializedName("created_at" ) var createdAt : String?  = null,
    @SerializedName("is_read"    ) var isRead    : Boolean? = null
    ) : Parcelable
