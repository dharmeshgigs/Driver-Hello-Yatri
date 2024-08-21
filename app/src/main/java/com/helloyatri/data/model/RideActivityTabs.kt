package com.helloyatri.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
enum class Status : Parcelable {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

@Parcelize
data class RideActivityTabs(
    var title: String? = null,
    var strResTitle: Int? = null,
    var paramType: String,
) : Parcelable
