package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection


data class GetTypeModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<CommonFieldSelection> = arrayListOf()

)

//data class DataFuel(
//
//    @SerializedName("id") var id: Int? = null,
//    @SerializedName("name") var name: String? = null,
//    @SerializedName("base_fare") var baseFare: Int? = null,
//    @SerializedName("price_per_km") var pricePerKm: Int? = null,
//    @SerializedName("price_per_minute") var pricePerMinute: Int? = null,
//    @SerializedName("capacity") var capacity: Int? = null,
//    @SerializedName("logo") var logo: String? = null,
//    @SerializedName("status") var status: Int? = null,
//    @SerializedName("created_at") var createdAt: String? = null,
//    @SerializedName("updated_at") var updatedAt: String? = null,
//    var isOptionSelected: Boolean = false
//
//)