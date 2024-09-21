package com.helloyatri.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentTab(
    val title: String,
    val status: String
) : Parcelable
