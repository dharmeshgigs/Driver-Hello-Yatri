package com.helloyatri.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
    enum class TabType : Parcelable {
    FAQ,
    CONTACT_US
}

@Parcelize
data class HelpCenterTabs(
    val title: String,
    val status: TabType
) : Parcelable