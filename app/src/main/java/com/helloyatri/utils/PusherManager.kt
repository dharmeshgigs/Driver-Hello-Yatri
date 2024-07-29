package com.helloyatri.utils

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpChannelAuthorizer
import java.lang.Exception

class PusherManager {
    private var pusher: Pusher? = null
    private var channel: Channel? = null
    val YOUR_APP_KEY = "11288b3d484789c2c83d"
    val YOUR_APP_CLUSTER = "ap2"
    val YOUR_CHANNEL_NAME = "private-driver."
    val YOUR_EVENT_NAME = "NewRideRequest"
    private var event = arrayOf(YOUR_EVENT_NAME)


    fun initializePusher(userId: String, userToken: String) {
        val map = mutableMapOf<String, String>()
        map["Accept"] = "application/json"
        map["Authorization"] = "Bearer ".plus(userToken)
        val channelAuthorizer = HttpChannelAuthorizer("http://3.111.159.32/api/pusher/auth")
        channelAuthorizer.setHeaders(map)
        val options = PusherOptions().setCluster(YOUR_APP_CLUSTER).setUseTLS(true)
            .setChannelAuthorizer(channelAuthorizer)
        pusher = Pusher(YOUR_APP_KEY, options)
        channel = pusher?.subscribePrivate(YOUR_CHANNEL_NAME.plus(userId))
        pusher?.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) {

                println("onConnectionStateChange:${change?.currentState}")
                println("onConnectionStateChange:${change?.previousState}")
                Log.i("TAG", "onConnectionStateChange: " + change?.currentState)
                Log.i("TAG", "onConnectionStateChange: " + change?.previousState)

            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                Log.i("TAG", "onError: "+message)
            }
        })
    }

    fun subscribeToEvent(eventListener: PrivateChannelEventListener) {
        event.forEach {
            channel?.bind(it, eventListener)
        }
    }

    fun disconnectPusher() {
        pusher?.disconnect()
    }

}