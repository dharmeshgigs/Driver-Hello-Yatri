package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName


data class TripRiderModel(

    @SerializedName("riderDetails") var riderDetails: RiderDetails? = RiderDetails(),
    @SerializedName("tripDetails") var tripDetails: TripDetails? = TripDetails(),
    @SerializedName("popupDetails") var popupDetails: PopupDetails? = PopupDetails()

)

data class RiderDetails(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("profile") var profile: String? = null,
    @SerializedName("payment_type") var paymentType: String? = null

)

data class StartLocation(

    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("address") var address: String? = null

)

data class EndLocation(

    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("address") var address: String? = null

)

data class TripDetails(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("driver_id") var driverId: Int? = null,
    @SerializedName("start_location") var startLocation: StartLocation? = StartLocation(),
    @SerializedName("end_location") var endLocation: EndLocation? = EndLocation(),
    @SerializedName("drop_points") var dropPoints: ArrayList<String> = arrayListOf(),
    @SerializedName("distance") var distance: String? = null,
    @SerializedName("distance_txt") var distanceTxt: String? = null,
    @SerializedName("duration") var duration: String? = null,
    @SerializedName("duration_txt") var durationTxt: String? = null,
    @SerializedName("estimated_fare") var estimatedFare: String? = null,
    @SerializedName("arriving_duration") var arrivingDuration: Int? = null

)

data class PopupDetails(

    @SerializedName("TIMER_DURATION") var TIMERDURATION: Int? = null,
    @SerializedName("ACCEPT_BTN_LBL") var ACCEPTBTNLBL: String? = null,
    @SerializedName("DECLINE_BTN_LBL") var DECLINEBTNLBL: String? = null,
    @SerializedName("DISTANCE_LBL") var DISTANCELBL: String? = null,
    @SerializedName("DURATION_LBL") var DURATIONLBL: String? = null,
    @SerializedName("FARE_PRICE_LBL") var FAREPRICELBL: String? = null

)