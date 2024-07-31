package com.helloyatri.utils

import com.helloyatri.data.model.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EventReceiver {

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        println("Received message: ${event.message}")
    }

    fun unregister() {
        EventBus.getDefault().unregister(this)
    }
}
