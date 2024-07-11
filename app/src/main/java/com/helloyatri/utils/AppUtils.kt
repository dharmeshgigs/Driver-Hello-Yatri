package com.helloyatri.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.helloyatri.network.ResBody
import java.util.Calendar
import java.util.Date


fun getJsonString(t : Any?): String {
    val gson = Gson()
    return gson.toJson(t).toString()
}

fun <T> getJsonObject(data : String,clazz: Class<T>): Class<T> {
    val gson = Gson()
    return gson.fromJson(data,clazz::class.java)
}

inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

//fun <T> getJsonObject(data : JsonObject,clazz: Any): TypeToken<T> {
//    val gson = Gson()
//   return gson.fromJson(data.toString(), clazz as Type)
//}
