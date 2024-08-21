package com.helloyatri.ui.rideactivity

import android.content.Context
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.RideActivityResponse
import com.helloyatri.data.model.RideActivityTabs
import com.helloyatri.data.model.Status
import com.helloyatri.data.model.Trips

class RideActivityUseCases {
    fun getDefaultTabs() = arrayListOf(
        RideActivityTabs(
            strResTitle = R.string.label_active,
            paramType = "ACTIVE"
        ), RideActivityTabs(
            strResTitle = R.string.label_completed,
            paramType = "COMPLETED"
        ), RideActivityTabs(
            strResTitle = R.string.label_cancelled,
            paramType = "CANCELLED"
        )
    )

    fun getActiveTrips(data: String) : MutableList<Trips> {
        val rideActivityResponse =
            Gson().fromJson(data, RideActivityResponse::class.java)
        val completedTrips = mutableListOf<Trips>()
        rideActivityResponse?.data?.trips?.forEach {
            if(it.status == "ACTIVE") {
                completedTrips.add(it)
            }
        }
        return completedTrips
    }

    fun getCompletedTrips(data: String) : MutableList<Trips> {
        val rideActivityResponse =
            Gson().fromJson(data, RideActivityResponse::class.java)
        val completedTrips = mutableListOf<Trips>()
        rideActivityResponse?.data?.trips?.forEach {
            if(it.status == "COMPLETED") {
                completedTrips.add(it)
            }
        }
        return completedTrips
    }
    fun getCancelledTrips(data: String) : MutableList<Trips> {
        val rideActivityResponse =
            Gson().fromJson(data, RideActivityResponse::class.java)
        val completedTrips = mutableListOf<Trips>()
        rideActivityResponse?.data?.trips?.forEach {
            if(it.status == "CANCELLED") {
                completedTrips.add(it)
            }
        }
        return completedTrips
    }

}