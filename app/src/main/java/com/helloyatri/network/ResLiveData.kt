package com.helloyatri.network

import androidx.lifecycle.MutableLiveData
import com.helloyatri.ui.base.BaseFragment

class ResLiveData<T> : MutableLiveData<Res<T>>(){

    fun get(
        fragment : BaseFragment<*>,
        onNewValue : (ResBody<T>) -> Unit,
        onIssue : (Throwable) -> Boolean = { true }
    ) {
        super.observe(fragment) { res ->
            if(res?.error != null) {
                if(onIssue(res.error)) fragment.onError(res.error)
            } else if(res?.resBody != null) {
                onNewValue(res.resBody)
            }
        }
    }
}
