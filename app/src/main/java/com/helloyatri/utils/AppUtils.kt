package com.helloyatri.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.helloyatri.network.ResBody
import java.util.Calendar
import java.util.Date

object AppUtils {

    fun getJsonString(t: Any?): String {
        val gson = Gson()
        return gson.toJson(t).toString()
    }

    fun <T> getJsonObject(data: String, clazz: Class<T>): Class<T> {
        val gson = Gson()
        return gson.fromJson(data, clazz::class.java)
    }

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

//fun <T> getJsonObject(data : JsonObject,clazz: Any): TypeToken<T> {
//    val gson = Gson()
//   return gson.fromJson(data.toString(), clazz as Type)
//}

    fun updateTimerUIMin(millisUntilFinished: Double): String {
        val seconds = millisUntilFinished / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return minutes.toInt().toString()
    }

    fun Double?.fareAmount() = this?.toString() ?: "0"
    fun Int?.fareAmount() = this?.toString() ?: "0"

    fun Context.openCallDialer(number: String?) {
        try {
            number?.let {
                val intent = Intent(Intent.ACTION_DIAL)
                // Set the data to the phone number
                intent.data = Uri.parse("tel:$number")
                // Start the activity with the intent
                this.startActivity(intent)
            }
        } catch (e: Exception) {
        }
    }

    fun Int?.fairValue(default: String) = this?.toString() ?: default

}