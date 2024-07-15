package com.helloyatri.data.model

data class ScheduleRide(
    var title: String? = null,
    var date: String? = null,
    var subList: ArrayList<RidePickUps>? = null,
    var isExpanded: Boolean = true,
)
