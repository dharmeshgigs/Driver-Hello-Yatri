package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName


data class GetHomeDataModel(
    @SerializedName("status") var status: String? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: HomeDataModel? = null
)

data class RideRequestModel(
    @SerializedName("title") var title: String? = null,
    @SerializedName("sub_title") var subTitle: String? = null
)

data class HomeDataModel(

    @SerializedName("today_date") var todayDate: String? = null,
    @SerializedName("total_earn") var totalEarn: String? = null,
    @SerializedName("total_rides") var totalRides: Int? = null,
    @SerializedName("total_distance") var totalDistance: String? = null,
    @SerializedName("total_duration") var totalDuration: String? = null,
    @SerializedName("driver_availability_status") var driverAvailabilityStatus: Int? = null,
    @SerializedName("driver_availability_status_btn_lbl") var driverAvailabilityStatusBtnLbl: String? = null,
    @SerializedName("ride_request") var rideRequest: RideRequestModel? = null,
    @SerializedName("waiting_trips_count") var waitingTripsCount: Int? = null,
    @SerializedName("acceptance_ratio") var acceptanceRatio: Double? = null,
    @SerializedName("average_review_rating") var averageReviewRating: Double? = null,
    @SerializedName("total_review_rating") var totalReviewRating: Int? = null,


)