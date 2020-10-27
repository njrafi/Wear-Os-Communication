package com.example.communicationdemo

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.communicationdemo.databinding.ActivityMainBinding

class MainActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: com.example.communicationdemo.databinding.ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main)

        var dummyNumber = 0
        val messagePath = "/message_path"

        binding.button.setOnClickListener{
            Log.i("WearableMainActivity", "Send Message Button Clicked")
            dummyNumber += 1
            binding.text.text = dummyNumber.toString()
            MessageSender.sendMessage(messagePath,"Message from Wearable $dummyNumber",this)
        }

        // Enables Always-on
        setAmbientEnabled()
    }
}
