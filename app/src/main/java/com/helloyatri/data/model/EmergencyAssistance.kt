package com.helloyatri.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmergencyAssistance(
    val title: String? = null,
    val icon : Int?=null
) : Parcelable
