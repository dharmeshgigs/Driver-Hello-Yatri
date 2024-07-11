package com.helloyatri.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelRideReasons(

    val title: String? = null,

    var reportReasonOther: String? = null,

    var isSelected: Boolean = false,
) : Parcelable
