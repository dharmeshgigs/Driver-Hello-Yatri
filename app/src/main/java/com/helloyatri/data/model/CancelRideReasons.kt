package com.helloyatri.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelRideReasons(

    val title: String? = null,

    var reportReasonOther: String? = null,

    var isSelected: Boolean = false,
) : Parcelable
