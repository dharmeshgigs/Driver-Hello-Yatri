package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName


data class GetVehicleDetailsModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: DataVehicle? = DataVehicle()

)

data class DataVehicle(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("driver_id") var driverId: Int? = null,
    @SerializedName("vehicle_type") var vehicleType: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("no_of_sheets") var noOfSheets: String? = null,
    @SerializedName("vehicle_number") var vehicleNumber: String? = null,
    @SerializedName("fuel_type") var fuelType: String? = null,
    @SerializedName("model_year") var modelYear: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)