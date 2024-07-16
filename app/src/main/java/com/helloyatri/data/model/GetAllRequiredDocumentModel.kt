package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class GetAllRequiredDocument(
    @SerializedName("status") var status: String? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<DataDocument> = arrayListOf()
)

data class DataDocument(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("is_required") var isRequired: Int? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("uploaded_driver_document") var uploadedDriverDocument: String? = null,
    @SerializedName("uploaded_vehicle_document") var uploaded_vehicle_document: String? = null,
    var isDataAdded: Boolean = false
)