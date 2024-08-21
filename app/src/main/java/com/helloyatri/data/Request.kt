package com.helloyatri.data

import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("type") var type: Any? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("otp") var otp: String? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("drive_in_city") var driverInCity: String? = null,
    @SerializedName("id") var id: Any? = null,
    @SerializedName("document") var document: String? = null,
    @SerializedName("vehicle_type") var vehicleType: String? = null,
    @SerializedName("no_of_sheets") var noOfSheets: String? = null,
    @SerializedName("vehicle_number") var vehicleNumber: String? = null,
    @SerializedName("fuel_type") var fuelType: String? = null,
    @SerializedName("model_year") var modelYear: String? = null,
    @SerializedName("vehicle_name") var vehicleName: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("availability") var availability: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("trip_id") var trip_id: String? = null,
    @SerializedName("filter_parameter") var filter_parameter: String? = null
)

data class GetRequest(
    var queryMap: Map<String,Any>? = null,
)