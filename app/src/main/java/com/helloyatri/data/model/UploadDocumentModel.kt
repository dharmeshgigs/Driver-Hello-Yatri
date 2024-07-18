package com.helloyatri.data.model

import com.google.gson.annotations.SerializedName

data class UploadDocumentModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null

)