package com.helloyatri.ui.usecases

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.data.model.RideActivityResponse
import com.helloyatri.data.model.Trips
import com.helloyatri.network.Resource

class ScheduleTripUseCases {
    fun getScheduleTrips(resource: Resource<JsonObject>): MutableList<Trips> {
        resource.data?.let {
            val rideActivityResponse =
                Gson().fromJson(it.toString(), RideActivityResponse::class.java)
            rideActivityResponse?.data?.trips?.takeIf { it.isNotEmpty() }?.let {
                return it
            } ?: return emptyList<Trips>().toMutableList()
        } ?: return emptyList<Trips>().toMutableList()
    }
}