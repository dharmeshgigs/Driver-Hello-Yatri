package com.helloyatri.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(context: Context) {

    companion object {
        const val SHARED_PREF_NAME = "app_preference"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)


    @SuppressLint("CommitPrefEdits")
    fun putString(name: String, value: String) {
        val editor = sharedPreferences.edit()
        editor!!.putString(name, value)
        editor.apply()
    }


    @SuppressLint("CommitPrefEdits")
    fun putBoolean(name: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor!!.putBoolean(name, value)
        editor.apply()
    }

    fun getBoolean(name: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(name, default)
    }

    fun getString(name: String): String {
        return sharedPreferences.getString(name, "") ?: ""
    }

    fun getInt(name: String): Int {
        return sharedPreferences.getInt(name, 0)
    }


    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun putFloat(name: String, value: Float) {
        val editor = sharedPreferences.edit()
        editor!!.putFloat(name, value)
        editor.apply()
    }

    fun getFloat(name: String): Float {
        return sharedPreferences.getFloat(name, 0f)
    }

    fun <T> putObject(key: String, obj : T) {
        val editor = sharedPreferences.edit()
        editor!!.putString(key, Gson().toJson(obj))
        editor.apply()
    }

//    inline fun <reified T> getObject(key: String) : T? {
//        val data = sharedPreferences.getString(key, null)
//        data?.let {
//            return Gson().fromJson(it, T::class.java)
//        } ?: run {
//
//        }
//    }
}
