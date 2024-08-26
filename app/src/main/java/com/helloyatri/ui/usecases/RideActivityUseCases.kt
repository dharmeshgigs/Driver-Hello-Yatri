package com.helloyatri.ui.usecases

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.R
import com.helloyatri.data.model.RideActivityResponse
import com.helloyatri.data.model.RideActivityTabs
import com.helloyatri.data.model.Trips
import com.helloyatri.network.Resource

class RideActivityUseCases {
    fun getDefaultTabs() = arrayListOf(
        RideActivityTabs(
            strResTitle = R.string.label_active, paramType = "ACTIVE"
        ), RideActivityTabs(
            strResTitle = R.string.label_completed, paramType = "COMPLETED"
        ), RideActivityTabs(
            strResTitle = R.string.label_cancelled, paramType = "CANCELLED"
        )
    )

    fun getTrips(resource: Resource<JsonObject>): MutableList<Trips> {
        resource.data?.let {
            val rideActivityResponse =
                Gson().fromJson(it.toString(), RideActivityResponse::class.java)
            rideActivityResponse?.data?.trips?.takeIf { it.isNotEmpty() }?.let {
                return it
            } ?: return emptyList<Trips>().toMutableList()
        } ?: return emptyList<Trips>().toMutableList()
    }
}