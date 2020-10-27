package com.example.communicationdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : WearableActivity() {
    private val tag = "MainActivity"
    private val imageFileName = "ImageFromMobile.png"


    private val imageBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Main Activity", "Broadcast Received")
            val imagePathUri = intent?.getStringExtra(ImageMessageConstants.imagePathUri)
            val image = BitmapFactory.decodeFile(imagePathUri)
            if (image != null) {
                imageView.setImageBitmap(image)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var dummyNumber = 0
        val messagePath = "/message_path"

        button.setOnClickListener {
            Log.i("WearableMainActivity", "Send Message Button Clicked")
            dummyNumber += 1
            dummyNumberTextView.text = dummyNumber.toString()
            MessageSender.sendMessage(messagePath, "Message from Wearable $dummyNumber", this)
        }

        // Enables Always-on
        setAmbientEnabled()
    }

    override fun onResume() {
        super.onResume()
        ImageReceiver.registerChannel(this)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ImageMessageConstants.intentName)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(imageBroadcastReceiver, intentFilter)

    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(imageBroadcastReceiver)
    }

}
