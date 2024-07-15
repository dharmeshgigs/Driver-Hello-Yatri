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
    val title: String,
    val status: Status
) : Parcelable
