package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class RideActivityResponse (
    @SerializedName("status"  ) var status  : String? = null,
    @SerializedName("code"    ) var code    : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Data?   = null
)
data class Data (
    @SerializedName("data"    ) var trips    : ArrayList<Trips>?    = null,
    @SerializedName("filters" ) var filters : ArrayList<Filters>? =  null
)

data class Trips (
    @SerializedName("id"                          ) var id                        : Int?              = null,
    @SerializedName("user"                        ) var user                      : User?             = null,
    @SerializedName("vehicle_type"                ) var vehicleType               : VehicleType?      = null,
    @SerializedName("start_address"               ) var startAddress              : String?           = null,
    @SerializedName("end_address"                 ) var endAddress                : String?           = null,
    @SerializedName("start_latitude"              ) var startLatitude             : String?           = null,
    @SerializedName("start_longitude"             ) var startLongitude            : String?           = null,
    @SerializedName("end_latitude"                ) var endLatitude               : String?           = null,
    @SerializedName("end_longitude"               ) var endLongitude              : String?           = null,
    @SerializedName("tip_amount"                  ) var tipAmount                 : Double?              = null,
    @SerializedName("total_fare"                  ) var totalFare                 : Double?              = null,
    @SerializedName("estimated_fare"              ) var estimatedFare             : Double?           = null,
    @SerializedName("estimated_arriving_duration" ) var estimatedArrivingDuration : Double?              = null,
    @SerializedName("estimated_arriving_distance" ) var estimatedArrivingDistance : Double?              = null,
    @SerializedName("waiting_time"                ) var waitingTime               : Double?              = null,
    @SerializedName("wheelchair_required"         ) var wheelchairRequired        : Int?              = null,
    @SerializedName("wheelchair_note"             ) var wheelchairNote            : String?           = null,
    @SerializedName("estimated_duration"          ) var estimatedDuration         : Double?              = null,
    @SerializedName("estimated_distance"          ) var estimatedDistance         : Double?           = null,
    @SerializedName("total_duration"              ) var totalDuration             : Double?              = null,
    @SerializedName("total_distance"              ) var totalDistance             : Double?              = null,
    @SerializedName("no_of_passengers"            ) var noOfPassengers            : Int?              = null,
    @SerializedName("pickup_note"                 ) var pickupNote                : String?           = null,
    @SerializedName("cancel_reason"               ) var cancelReason              : String?           = null,
    @SerializedName("vehicle_type_data"           ) var vehicleTypeData           : String?           = null,
    @SerializedName("verification_code"           ) var verificationCode          : String?           = null,
    @SerializedName("is_verified"                 ) var isVerified                : Int?              = null,
    @SerializedName("scheduled_time"              ) var scheduledTime             : String?           = null,
    @SerializedName("cancelled_by"                ) var cancelledBy               : String?           = null,
    @SerializedName("status"                      ) var status                    : String?           = null,
    @SerializedName("payment_status"              ) var paymentStatus             : String?           = null,
    @SerializedName("stop_over_points"            ) var stopOverPoints            : ArrayList<String>? = null,
    @SerializedName("review_ratings"              ) var reviewRatings             : ReviewRatings?           = null,
    @SerializedName("distance_txt"              ) var distance_txt             : String?           = null,
    @SerializedName("duration_txt"              ) var duration_txt             : String?           = null,
    @SerializedName("total_fare_txt"              ) var total_fare_txt             : String?           = null,
    @SerializedName("created_at"              ) var created_at             : String?           = null,
    @SerializedName("reach_time_note"              ) var reach_time_note             : String?           = null,
    @SerializedName("active_trip_btn_lbl"              ) var activeTripBtnLbl             : String?           = null,
)

data class VehicleType (
    @SerializedName("id"               ) var id             : Int?    = null,
    @SerializedName("name"             ) var name           : String? = null,
    @SerializedName("base_fare"        ) var baseFare       : Int?    = null,
    @SerializedName("price_per_km"     ) var pricePerKm     : Int?    = null,
    @SerializedName("price_per_minute" ) var pricePerMinute : Int?    = null,
    @SerializedName("capacity"         ) var capacity       : Int?    = null,
    @SerializedName("logo"             ) var logo           : String? = null,
    @SerializedName("status"           ) var status         : Int?    = null
)

data class User (
    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("type"          ) var type         : String? = null,
    @SerializedName("name"          ) var name         : String? = null,
    @SerializedName("firstname"     ) var firstname    : String? = null,
    @SerializedName("lastname"      ) var lastname     : String? = null,
    @SerializedName("mobile"        ) var mobile       : String? = null,
    @SerializedName("profile_image" ) var profileImage : String? = null,
    @SerializedName("gender"        ) var gender       : String? = null,
    @SerializedName("referral_code" ) var referralCode : String? = null,
    @SerializedName("disability"    ) var disability   : String? = null
)

data class Filters (
    @SerializedName("vSubFilterParam" ) var vSubFilterParam : String? = null,
    @SerializedName("vTitle"          ) var vTitle          : String? = null
)

data class ReviewRatings (

    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("trip_id"    ) var tripId    : Int?    = null,
    @SerializedName("from"       ) var from      : Int?    = null,
    @SerializedName("to"         ) var to        : Int?    = null,
    @SerializedName("rating"     ) var rating    : Int?    = null,
    @SerializedName("comment"    ) var comment   : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null

)