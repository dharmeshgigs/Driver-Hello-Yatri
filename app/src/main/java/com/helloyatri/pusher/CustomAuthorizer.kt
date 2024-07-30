package com.helloyatri.pusher

import com.pusher.client.Authorizer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CustomAuthorizer(private val authEndpoint: String, private val token: String) : Authorizer {

    override fun authorize(socketId: String, channelName: String): String {
        val urlString = authEndpoint
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
//        val map = mutableMapOf<String, String>()
//        map["Accept"] = "application/json"
//        map["Authorization"] = "Bearer ".plus(userToken)
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer ".plus( token))

        return try {
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                response.toString()
            } else {
                throw Exception("Authorization failed with response code $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }
}