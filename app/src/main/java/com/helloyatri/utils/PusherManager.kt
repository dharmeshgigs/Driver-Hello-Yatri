package com.helloyatri.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.helloyatri.network.APIFactory
import com.helloyatri.pusher.CustomAuthorizer
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionStateChange


class PusherManager(private val context: Context) {
    private var pusher: Pusher? = null
    private var channel: PrivateChannel? = null
    private val YOUR_APP_KEY = "11288b3d484789c2c83d"
    private val YOUR_APP_CLUSTER = "ap2"
    private val YOUR_CHANNEL_NAME = "private-driver."
    companion object {
         val YOUR_EVENT_NAME : String = "NewRideRequest"
         val PAYMENT_COMPLETED: String = "PaymentCompleted"
         val TRIP_UPDATED: String = "TripUpdated"
         val TRIP_STATUS_UPDATED: String = "TripStatusUpdated"
    }
    private var event = arrayOf(YOUR_EVENT_NAME, PAYMENT_COMPLETED,TRIP_UPDATED, TRIP_STATUS_UPDATED)
    var data = MutableLiveData<JsonObject>()
    private var pushEventListener: PushEventListener? = null

    fun initializePusher(userId: String, userToken: String) {
        pusher?.connection?.state?.let {
            if (it === com.pusher.client.connection.ConnectionState.DISCONNECTED) {
                initPusher(userToken, userId)
            }
        } ?: run {
            initPusher(userToken, userId)
        }
    }

    private fun initPusher(userToken: String, userId: String) {
        val channelAuthorizer =
            CustomAuthorizer(APIFactory.AuthApi.PUSHER_AUTH_URL, userToken, userId)
        val options =
            PusherOptions().setCluster(YOUR_APP_CLUSTER).setUseTLS(true).setEncrypted(true)
                .setAuthorizer(channelAuthorizer)
        pusher = Pusher(YOUR_APP_KEY, options)
        channel = pusher?.subscribePrivate(YOUR_CHANNEL_NAME.plus(userId))
        pusher?.connect(object : com.pusher.client.connection.ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) {
//                println("onConnectionStateChange Previous:${change?.previousState}")
//                println("onConnectionStateChange Current :${change?.currentState}")
                val handler: Handler = Handler(Looper.getMainLooper())

                handler.post(Runnable {
                    Toast.makeText(context, "" + change?.currentState, Toast.LENGTH_LONG).show()
                })
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                val handler: Handler = Handler(Looper.getMainLooper())

                handler.post(Runnable {
                    Toast.makeText(context, "error:" + message, Toast.LENGTH_LONG).show()
                })

            }
        })
        event.forEach {
            channel?.bind(it, eventListener)
        }
    }

    private val eventListener = object : PrivateChannelEventListener {
        override fun onEvent(event: PusherEvent?) {
            val jsonObject = JsonParser.parseString(event?.data).asJsonObject
            val eventName = event?.eventName ?: ""
            data.postValue(jsonObject)
            pushEventListener?.onEvent(jsonObject, eventName)
        }

        override fun onSubscriptionSucceeded(channelName: String?) {

        }

        override fun onAuthenticationFailure(message: String?, e: Exception?) {

        }

    }

    fun disconnectPusher() {
        pusher?.disconnect()
    }

    fun setPushEventListener(pushEventListener: PushEventListener) {
        this.pushEventListener = pushEventListener
    }

    fun triggerDriverLocationEvent() {
        if (channel?.isSubscribed == true) {
            channel?.trigger(
                "client-event",
                "Hello"
            ); }
    }

}