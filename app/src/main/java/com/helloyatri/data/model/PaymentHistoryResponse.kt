package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class PaymentHistoryResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: PaymentHistory? = null
)

data class PaymentHistory(
    @SerializedName("formatted_filter_date") var formattedFilterDate: String? = null,
    @SerializedName("total_earn") var totalEarn: String? = null,
    @SerializedName("total_rides") var totalRides: Int? = null,
    @SerializedName("total_distance") var totalDistance: String? = null,
    @SerializedName("total_duration") var totalDuration: String? = null,
    @SerializedName("total_accepted_fare_txt") var totalAcceptedFareTxt: String? = null,
    @SerializedName("total_requested_fare_txt") var totalRequestedFareTxt: String? = null,
    @SerializedName("total_accepted_fare") var totalAcceptedFare: String? = null,
    @SerializedName("total_requested_fare") var totalRequestedFare: String? = null,
    @SerializedName("data") var data: ArrayList<Payment>? = null,
//    @SerializedName("filters") var filters: String? = null
)

data class Payment(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("user") var user: UserPayment? = null,
    @SerializedName("start_address") var startAddress: String? = null,
    @SerializedName("end_address") var endAddress: String? = null,
    @SerializedName("start_latitude") var startLatitude: String? = null,
    @SerializedName("start_longitude") var startLongitude: String? = null,
    @SerializedName("end_latitude") var endLatitude: String? = null,
    @SerializedName("end_longitude") var endLongitude: String? = null,
    @SerializedName("tip_amount") var tipAmount: Int? = null,
    @SerializedName("tip_amount_txt") var tipAmountTxt: String? = null,
    @SerializedName("total_fare") var totalFare: Double? = null,
    @SerializedName("estimated_fare") var estimatedFare: Double? = null,
    @SerializedName("estimated_fare_txt") var estimatedFareTxt: String? = null,
    @SerializedName("estimated_arriving_duration") var estimatedArrivingDuration: Int? = null,
    @SerializedName("estimated_arriving_distance") var estimatedArrivingDistance: Int? = null,
    @SerializedName("waiting_time") var waitingTime: Int? = null,
    @SerializedName("wheelchair_required") var wheelchairRequired: Int? = null,
    @SerializedName("wheelchair_note") var wheelchairNote: String? = null,
    @SerializedName("estimated_duration") var estimatedDuration: Int? = null,
    @SerializedName("estimated_distance") var estimatedDistance: Double? = null,
    @SerializedName("distance_txt") var distanceTxt: String? = null,
    @SerializedName("duration_txt") var durationTxt: String? = null,
    @SerializedName("total_fare_txt") var totalFareTxt: String? = null,
    @SerializedName("reach_time_note") var reachTimeNote: String? = null,
    @SerializedName("total_duration") var totalDuration: Double? = null,
    @SerializedName("total_distance") var totalDistance: Double? = null,
    @SerializedName("no_of_passengers") var noOfPassengers: Int? = null,
    @SerializedName("pickup_note") var pickupNote: String? = null,
    @SerializedName("cancel_reason") var cancelReason: String? = null,
    @SerializedName("vehicle_type_data") var vehicleTypeData: String? = null,
    @SerializedName("verification_code") var verificationCode: String? = null,
    @SerializedName("is_verified") var isVerified: Int? = null,
    @SerializedName("scheduled_time") var scheduledTime: String? = null,
    @SerializedName("cancelled_by") var cancelledBy: String? = null,
    @SerializedName("cancelled_by_txt") var cancelledByTxt: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("payment_status") var paymentStatus: String? = null,
    @SerializedName("is_active_trip") var isActiveTrip: Boolean? = null,
    @SerializedName("active_trip_btn_lbl") var activeTripBtnLbl: String? = null,
    @SerializedName("stop_over_points") var stopOverPoints: ArrayList<StopOverPoints2>? = null,
    @SerializedName("common_total_fare") var commonTotalFare: String? = null,
    @SerializedName("common_total_fare_txt") var commonTotalFareTxt: String? = null

)


data class UserPayment(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("profile_image") var profileImage: String? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("referral_code") var referralCode: String? = null,
    @SerializedName("disability") var disability: String? = null
)

data class StopOverPoints2 (
    @SerializedName("id"                    ) var id                  : Int?    = null,
    @SerializedName("trip_id"               ) var tripId              : Int?    = null,
    @SerializedName("dest_latitude"         ) var destLatitude        : String? = null,
    @SerializedName("dest_longitude"        ) var destLongitude       : String? = null,
    @SerializedName("dest_address"          ) var destAddress         : String? = null,
    @SerializedName("actual_dest_latitude"  ) var actualDestLatitude  : String? = null,
    @SerializedName("actual_dest_longitude" ) var actualDestLongitude : String? = null,
    @SerializedName("actual_dest_address"   ) var actualDestAddress   : String? = null,
    @SerializedName("created_at"            ) var createdAt           : String? = null,
    @SerializedName("updated_at"            ) var updatedAt           : String? = null
)