package com.example.communicationdemo

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MessageListenerService : WearableListenerService() {
    private val tag = "MessageListenerService"
    override fun onMessageReceived(messageEvent: MessageEvent?) {
        super.onMessageReceived(messageEvent)
        Log.i(tag,"Message received: ${messageEvent.toString()}")
    }
}