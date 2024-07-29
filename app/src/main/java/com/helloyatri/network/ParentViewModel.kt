package com.helloyatri.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class ParentViewModel : ViewModel() {
    fun run(part : suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            part(this)
        }
    }
}