package com.example.communicationdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val messageBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Main Activity", "Broadcast Received")
            val message = intent?.getStringExtra(MessageConstants.message)
            val path = intent?.getStringExtra(MessageConstants.path)
            messageTextView.text = message
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val i = Intent(this, MessageListenerService::class.java)
        this.startService(i)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(MessageConstants.intentName)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageBroadcastReceiver, intentFilter)

    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(messageBroadcastReceiver)
    }
}