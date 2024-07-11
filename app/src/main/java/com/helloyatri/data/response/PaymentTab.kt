package com.helloyatri.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentTab(
    val title: String,
    val status: TabTypeForPayment
) : Parcelable

@kotlinx.parcelize.Parcelize
enum class TabTypeForPayment : Parcelable {
    REQUESTED,
    ACCEPTED
}