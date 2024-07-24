package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class SavedAddress(
        var saveCount: String? = null,
        var saveAddress: String? = null,
        var isAddressAdded: Boolean = false,
        @SerializedName("id") var id: Int? = null,
        @SerializedName("user_id") var userId: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("location") var location: String? = null,
        @SerializedName("latitude") var latitude: String? = null,
        @SerializedName("longitude") var longitude: String? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("deleted_at") var deletedAt: String? = null
)
