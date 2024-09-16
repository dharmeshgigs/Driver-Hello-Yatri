package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class ReviewsResponse (
    @SerializedName("status"  ) var status  : String? = null,
    @SerializedName("code"    ) var code    : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : ReviewData?   = null
)
data class ReviewData (
    @SerializedName("DETAILS" ) var DETAILS : DETAILS?        = null,
    @SerializedName("data"    ) var data    : ArrayList<Review>? = null
)

data class Review (
    @SerializedName("id"         ) var id        : Int?      = null,
    @SerializedName("trip_id"    ) var tripId    : Int?      = null,
    @SerializedName("rating"     ) var rating    : Double?      = null,
    @SerializedName("comment"    ) var comment   : String?   = null,
    @SerializedName("created_at" ) var createdAt : String?   = null,
    @SerializedName("reviewer"   ) var reviewer  : Reviewer? = null
)
data class DETAILS (
    @SerializedName("TITLE"                 ) var TITLE               : String? = null,
    @SerializedName("average_review_rating" ) var averageReviewRating : Double? = null
)

data class Reviewer (
    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("name"          ) var name         : String? = null,
    @SerializedName("profile_image" ) var profileImage : String? = null
)