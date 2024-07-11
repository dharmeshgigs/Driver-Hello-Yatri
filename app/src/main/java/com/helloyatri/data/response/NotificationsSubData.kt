package com.helloyatri.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationsSubData(
    val title: String? = null,
    var isRead : Boolean = true
) : Parcelable