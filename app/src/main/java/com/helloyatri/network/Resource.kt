package com.gamingyards.sms.app.utils

import androidx.annotation.IdRes

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(val status: Status,
                           val data: T?,
                           val message: String?,
                           val throwable: Throwable? = null,
                           @IdRes val resId: Int? = null) {
    companion object {
        inline fun <reified T> success(data: T): Resource<T> {
            return Resource(status = Status.SUCCESS, data = data, message = null)
        }


        inline fun <reified T> error(@IdRes resId: Int? = 0, message: String? = null, throwable: Throwable? = null): Resource<T> {
            return Resource(status = Status.ERROR, data = null, message = message, throwable = throwable, resId = resId)
        }

        fun loading(): Resource<Nothing> =
            Resource(status = Status.LOADING, null, null)
    }
}

