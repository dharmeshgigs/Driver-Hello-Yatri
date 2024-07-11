package com.helloyatri.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
    data class NotificationsData(
            val title: String? = null,
            val icon: Int? = null,
            val subList: ArrayList<NotificationsSubData>? = null,
            var isExpanded: Boolean = true,
    ) : Parcelable
