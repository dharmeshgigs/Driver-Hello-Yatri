package com.helloyatri.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmergencyAssistance(
    val title: String? = null,
    val icon : Int?=null
) : Parcelable
