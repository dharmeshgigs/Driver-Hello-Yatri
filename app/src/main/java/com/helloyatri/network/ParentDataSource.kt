package com.helloyatri.network

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response

open class ParentDataSource {
    suspend fun <T> run(getRes: suspend () -> ResBody<T>): Res<T> {
        var res1: ResBody<T>? = null
        return try {
            res1 = getRes()
            Res(res1, null)
        } catch (e: Throwable) {
            Res(res1, e)
        }
    }

    suspend fun run2(getRes: suspend () -> Response<JsonObject>): Resource<JsonObject> {
        return try {
            val res = getRes()
            when (res.code()) {
                APIFactory.ResponseCode.SUCCESS -> {
                    Resource.success(res.body()!!)
                }
                APIFactory.ResponseCode.UNAUTHORIZED -> {
//                    Resource.error(resCode = APIFactory.ResponseCode.UNAUTHORIZED)
                    Resource.error(message = res.errorBody()?.charStream()?.readText()?.let {
                        JSONObject(it).getString("message")
                    })
                }
                else -> {
                    Resource.error(message = res.errorBody()?.charStream()?.readText()?.let {
                        JSONObject(it).getString("message")
                    })
                }
            }
        } catch (e: Throwable) {
            Resource.error(message = e.message)
        }
    }
}