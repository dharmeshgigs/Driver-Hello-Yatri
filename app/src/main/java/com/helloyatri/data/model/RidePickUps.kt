package com.helloyatri.data.model

data class RidePickUps(
        var customerName: String? = null,
        var customerProfileImage: String? = null,
        var paymentType: String? = null,
        var rideStatus:Status?=null,
        var isShowWaitingTime:Boolean?=false
)