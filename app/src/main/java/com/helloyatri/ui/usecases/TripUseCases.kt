package com.helloyatri.ui.usecases

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.data.model.PopUp
import com.helloyatri.data.model.VerifyTripOTPResponse
import com.helloyatri.network.Resource

class TripUseCases {
    fun getTripVerificationResult(resource: Resource<JsonObject>) : PopUp? {
        resource.data?.let {
            try {
                val response  =
                    Gson().fromJson(it.toString(), VerifyTripOTPResponse::class.java)
                response?.status?.takeIf { !it.isNullOrEmpty() }?.let {
                    if(it.equals("success", true) || it.equals("error", true)) {
                        response?.data?.let {
                            it.status = response.status
                            return it
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
        return null
    }
}