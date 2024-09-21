package com.helloyatri.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.helloyatri.R
import com.helloyatri.core.AppPreferences
import com.helloyatri.di.App
import com.helloyatri.ui.home.HomeActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle the received message
        remoteMessage.data.isNotEmpty().let {
            // Handle data payload
            val jsonObject = JsonParser.parseString(Gson().toJson(remoteMessage.data)).asJsonObject
            remoteMessage.notification?.let {
                // Handle notification payload
                val appPreferences: AppPreferences = AppPreferences(this)
                val isAppInForeground = appPreferences.getBoolean("isAppInForeground")
                if (!isAppInForeground) {
                    sendNotification(it.title, it.body, jsonObject)
                }
            }
        }


    }

    private fun sendNotification(title: String?, messageBody: String?, jsonObject: JsonObject) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("data", Gson().toJson(jsonObject))
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = NotificationCompat.Builder(this, "Driver_hello_yatri")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For Android 8.0 and above, create a NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Driver_hello_yatri",
                "Driver_hello_yatri",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
