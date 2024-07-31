package com.helloyatri.utils

import android.util.Log
import com.helloyatri.pusher.CustomAuthorizer
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionStateChange
import java.lang.Exception

class PusherManager {
    private var pusher: Pusher? = null
    private var channel: Channel? = null
    private val YOUR_APP_KEY = "11288b3d484789c2c83d"
    private val YOUR_APP_CLUSTER = "ap2"
    private val YOUR_CHANNEL_NAME = "private-driver."
    private val YOUR_EVENT_NAME = "NewRideRequest"
    private var event = arrayOf(YOUR_EVENT_NAME)

    fun initializePusher(userId: String, userToken: String) {
        pusher?.connection?.state?.let {
            if(it === com.pusher.client.connection.ConnectionState.DISCONNECTED
                ) {
                initPusher(userToken, userId)
            }
        } ?: run {
            initPusher(userToken, userId)
        }
    }

    private fun initPusher(userToken: String, userId: String) {
        val channelAuthorizer = CustomAuthorizer("http://3.111.159.32/api/pusher/auth", userToken, userId)
        val options = PusherOptions().setCluster(YOUR_APP_CLUSTER).setUseTLS(true)
            .setAuthorizer(channelAuthorizer)
        pusher = Pusher(YOUR_APP_KEY, options)
        channel = pusher?.subscribePrivate(YOUR_CHANNEL_NAME.plus(userId))
        pusher?.connect(object : com.pusher.client.connection.ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) {
                println("onConnectionStateChange Previous:${change?.previousState}")
                println("onConnectionStateChange Current :${change?.currentState}")
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                Log.i("TAG", "onError: "+message)
            }
        })
        event.forEach {
            channel?.bind(it, eventListener)
        }
    }

    private val  eventListener = object : PrivateChannelEventListener {
        override fun onEvent(event: PusherEvent?) {

        }

        override fun onSubscriptionSucceeded(channelName: String?) {

        }

        override fun onAuthenticationFailure(message: String?, e: Exception?) {

        }

    }

    fun disconnectPusher() {
        pusher?.disconnect()
    }

}