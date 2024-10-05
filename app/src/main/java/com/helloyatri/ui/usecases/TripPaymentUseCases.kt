package com.helloyatri.ui.usecases

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.data.model.PaymentHistory
import com.helloyatri.data.model.PaymentHistoryResponse
import com.helloyatri.network.Resource

class TripPaymentUseCases {

    fun getPaymentHistory(resource: Resource<JsonObject>): PaymentHistory? {
        resource.data?.let {
            val rideActivityResponse =
                Gson().fromJson(it.toString(), PaymentHistoryResponse::class.java)
            return rideActivityResponse?.data
        } ?: return null
    }
}