package com.helloyatri.utils

import com.google.gson.JsonObject

interface PushEventListener {
    fun onEvent(data: JsonObject, eventName: String)
}