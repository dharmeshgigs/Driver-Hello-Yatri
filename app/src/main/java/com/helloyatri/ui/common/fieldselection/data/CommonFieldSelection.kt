package com.helloyatri.ui.common.fieldselection.data

import com.google.gson.annotations.SerializedName

data class CommonFieldSelection(@SerializedName("name") var options: String? = null,
                                var isOptionSelected: Boolean = false, @SerializedName("id") var id: Int? = null,
                                @SerializedName("base_fare") var baseFare: Int? = null,
                                @SerializedName("price_per_km") var pricePerKm: Int? = null,
                                @SerializedName("price_per_minute") var pricePerMinute: Int? = null,
                                @SerializedName("capacity") var capacity: Int? = null,
                                @SerializedName("logo") var logo: String? = null,
                                @SerializedName("status") var status: Int? = null,
                                @SerializedName("created_at") var createdAt: String? = null,
                                @SerializedName("updated_at") var updatedAt: String? = null,)
