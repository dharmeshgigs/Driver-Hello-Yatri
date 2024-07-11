package com.helloyatri.network

import com.google.gson.JsonObject

data class Res<T>(
    val resBody: ResBody<T>?,
    val error : Throwable?
)

data class Res2(
    val resBody: JsonObject?,
    val error : Throwable?,
    var message: String?
)