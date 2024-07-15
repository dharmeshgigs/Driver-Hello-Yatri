package com.helloyatri.utils

import android.app.Activity
import com.helloyatri.R
import com.helloyatri.data.model.CancelRideReasons
import com.helloyatri.data.model.NearByLocation
import com.helloyatri.data.model.NotificationsData
import com.helloyatri.data.model.NotificationsSubData
import com.helloyatri.data.model.RidePickUps
import com.helloyatri.data.model.ScheduleRide
import com.helloyatri.data.model.Status

fun Activity.getCancelRideReasons(): ArrayList<CancelRideReasons> {
    return ArrayList<CancelRideReasons>().apply {
        add(
            CancelRideReasons(
                title = "Waiting for long time"
            )
        )
        add(
            CancelRideReasons(
                title = "The price is not reasonable"
            )
        )
        add(
            CancelRideReasons(
                title = "Wrong address"
            )
        )
        add(
            CancelRideReasons(
                title = "Can't contact the rider"
            )
        )
        add(
            CancelRideReasons(
                title = "Other"
            )
        )
    }
}

fun Activity.getRidePickUpList(): ArrayList<RidePickUps> {
    return ArrayList<RidePickUps>().apply {
        add(
            RidePickUps(
                customerName = "Aesha Mehta",
                customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                paymentType = "Online Payment"
            )
        )
        add(
            RidePickUps(
                customerName = "Rahul Mehta",
                customerProfileImage = "https://media.istockphoto.com/id/913094314/photo/young-businessman-portrait.jpg?s=1024x1024&w=is&k=20&c=_HrmGpEiRxW1AObLe_b2hVh0RJxVSJJ-fGgvgyIKs9U=",
                paymentType = "Offline Payment"
            )
        )
        add(
            RidePickUps(
                customerName = "Sagar Mehta",
                customerProfileImage = "https://images.unsplash.com/photo-1628499636754-3162d34ca39c?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjN8fG1hbGUlMjBwZXJzb258ZW58MHx8MHx8fDA%3D",
                paymentType = "Online Payment"
            )
        )
    }
}

fun Activity.getNearByLocationList(): ArrayList<NearByLocation> {
    return ArrayList<NearByLocation>().apply {
        add(
            NearByLocation(
                placeName = "Railway Station, CA",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "85%"
            )
        )
        add(
            NearByLocation(
                placeName = "Air Port",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "99%"
            )
        )
        add(
            NearByLocation(
                placeName = "Bus Stand",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "75%"
            )
        )
        add(
            NearByLocation(
                placeName = "Sunflower Park",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "60%"
            )
        )
        add(
            NearByLocation(
                placeName = "Cinemax",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "65%"
            )
        )
        add(
            NearByLocation(
                placeName = "R World",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "70%"
            )
        )
        add(
            NearByLocation(
                placeName = "Sunflower Park",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "60%"
            )
        )
        add(
            NearByLocation(
                placeName = "Cinemax",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                acceptanceRatio = "65%"
            )
        )
    }
}

fun Activity.getLocationList(): ArrayList<NearByLocation> {
    return ArrayList<NearByLocation>().apply {
        add(
            NearByLocation(
                placeName = "Railway Station, CA",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_white_location
            )
        )
        add(
            NearByLocation(
                placeName = "Air Port",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_white_location
            )
        )
        add(
            NearByLocation(
                placeName = "Bus Stand",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_white_location
            )
        )
        add(
            NearByLocation(
                placeName = "Sunflower Park",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_hotel
            )
        )
        add(
            NearByLocation(
                placeName = "Cinemax",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_movies
            )
        )
        add(
            NearByLocation(
                placeName = "R World",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_movies
            )
        )
        add(
            NearByLocation(
                placeName = "Cinemax WonderMaII",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_white_location
            )
        )
        add(
            NearByLocation(
                placeName = "Park Avenue",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_hotel
            )
        )
        add(
            NearByLocation(
                placeName = "Park Hotel",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_movies
            )
        )
        add(
            NearByLocation(
                placeName = "Magenta Park",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_white_location
            )
        )
        add(
            NearByLocation(
                placeName = "Seven Star Club",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_movies
            )
        )
        add(
            NearByLocation(
                placeName = "Stadium",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_white_location
            )
        )
        add(
            NearByLocation(
                placeName = "Iscon Park",
                subPlaceName = "101 National Dr. San Bruno, CA 94580",
                icon = R.drawable.ic_white_location
            )
        )
    }
}




fun Activity.getActiveRideList(): ArrayList<RidePickUps> {
    return ArrayList<RidePickUps>().apply {
        add(
                RidePickUps(
                        customerName = "Aesha Mehta",
                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                        paymentType = "Online Payment",
                        rideStatus = Status.ACTIVE
                )
        )
    }
}


fun Activity.getCompletedRideList(): ArrayList<RidePickUps> {
    return ArrayList<RidePickUps>().apply {
        add(
                RidePickUps(
                        customerName = "Aesha Mehta",
                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                        paymentType = "Online Payment",
                        rideStatus = Status.COMPLETED
                )
        )
        add(
                RidePickUps(
                        customerName = "Aesha Mehta",
                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                        paymentType = "Online Payment",
                        rideStatus = Status.COMPLETED
                )
        )
    }
}

fun Activity.getCancelledRideList(): ArrayList<RidePickUps> {
    return ArrayList<RidePickUps>().apply {
        add(
                RidePickUps(
                        customerName = "Aesha Mehta",
                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                        paymentType = "Online Payment",
                        rideStatus = Status.CANCELLED
                )
        )
        add(
                RidePickUps(
                        customerName = "Aesha Mehta",
                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                        paymentType = "Online Payment",
                        rideStatus = Status.CANCELLED
                )
        )
    }
}

fun Activity.getScheduleRideList(): ArrayList<ScheduleRide> {
    return ArrayList<ScheduleRide>().apply {
        add(
                ScheduleRide(
                        title = "Today",
                        subList = arrayListOf(
                                RidePickUps(
                                        customerName = "Aesha Mehta",
                                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                                        paymentType = "Online Payment",
                                        rideStatus = Status.ACTIVE
                                )
                        )
                )
        )
        add(
                ScheduleRide(
                        title = "Tomorrow ",
                        subList = arrayListOf(
                                RidePickUps(
                                        customerName = "Aesha Mehta",
                                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                                        paymentType = "Online Payment",
                                        rideStatus = Status.ACTIVE,
                                        isShowWaitingTime = true
                                )
                        )
                )
        )
        add(
                ScheduleRide(
                        title = "This week",
                        subList = arrayListOf(
                                RidePickUps(
                                        customerName = "Aesha Mehta",
                                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                                        paymentType = "Online Payment",
                                        rideStatus = Status.ACTIVE
                                )
                        )
                )
        )
        add(
                ScheduleRide(
                        title = "This Month",
                        subList = arrayListOf(
                                RidePickUps(
                                        customerName = "Aesha Mehta",
                                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                                        paymentType = "Online Payment",
                                        rideStatus = Status.ACTIVE
                                )
                        )
                )
        )
        add(
                ScheduleRide(
                        title = "March, 2024",
                        subList = arrayListOf(
                                RidePickUps(
                                        customerName = "Aesha Mehta",
                                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                                        paymentType = "Online Payment",
                                        rideStatus = Status.ACTIVE
                                )
                        )
                )
        )
        add(
                ScheduleRide(
                        title = "April, 2024",
                        subList = arrayListOf(
                                RidePickUps(
                                        customerName = "Aesha Mehta",
                                        customerProfileImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                                        paymentType = "Online Payment",
                                        rideStatus = Status.ACTIVE
                                )
                        )
                )
        )
    }
}

fun Activity.getNotificationList(): ArrayList<NotificationsData> {
    return ArrayList<NotificationsData>().apply {
        add(
                NotificationsData(
                        title = "Today",
                        subList = arrayListOf(
                                NotificationsSubData(
                                        title = "Pooja send to note",
                                        isRead = false
                                ),
                                NotificationsSubData(
                                        title = "You have accept new ride",
                                        isRead = false
                                ),
                                NotificationsSubData(
                                        title = "Ride Request from Pooja",
                                ),
                                NotificationsSubData(
                                        title = "Josh Cancel Pre-Book Ride",
                                ),
                                NotificationsSubData(
                                        title = "Ride Cancel Successfully",
                                )
                        )
                )
        )
        add(
                NotificationsData(
                        title = "Yesterday",
                        subList = arrayListOf(
                                NotificationsSubData(
                                        title = "Pooja send to note",
                                        isRead = false
                                ),
                                NotificationsSubData(
                                        title = "You have accept new ride",
                                        isRead = false
                                ),
                                NotificationsSubData(
                                        title = "Ride Request from Pooja",
                                ),
                                NotificationsSubData(
                                        title = "Josh Cancel Pre-Book Ride",
                                ),
                                NotificationsSubData(
                                        title = "Ride Cancel Successfully",
                                )
                        )
                )
        )
    }
}
