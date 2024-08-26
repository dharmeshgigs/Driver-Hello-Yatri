package com.helloyatri.ui.usecases

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.helloyatri.R
import com.helloyatri.data.model.GetHomeDataModel
import com.helloyatri.data.model.HomeDataModel
import com.helloyatri.data.model.RideActivityResponse
import com.helloyatri.data.model.RideActivityTabs
import com.helloyatri.data.model.Trips
import com.helloyatri.network.Resource

class HomeUseCases {
    fun getHomeData(resource: Resource<JsonObject>): HomeDataModel? {
        return resource.data?.let {
            val rideActivityResponse =
                Gson().fromJson(it.toString(), GetHomeDataModel::class.java)
            rideActivityResponse?.data?.let {
                return it
            }
        }
    }
}