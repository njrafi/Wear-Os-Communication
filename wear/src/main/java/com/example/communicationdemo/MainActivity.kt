package com.example.communicationdemo

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.communicationdemo.databinding.ActivityMainBinding
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.ChannelClient.ChannelCallback
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class MainActivity : WearableActivity() {
    private val tag = "MainActivity"
    private val imageFileName = "ImageFromMobile.png"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: com.example.communicationdemo.databinding.ActivityMainBinding =
            DataBindingUtil.setContentView(
                this, R.layout.activity_main
            )

        var dummyNumber = 0
        val messagePath = "/message_path"

        binding.button.setOnClickListener {
            Log.i("WearableMainActivity", "Send Message Button Clicked")
            dummyNumber += 1
            binding.text.text = dummyNumber.toString()
            MessageSender.sendMessage(messagePath, "Message from Wearable $dummyNumber", this)
        }

        // Enables Always-on
        setAmbientEnabled()
    }

    val job = Job()
    val backgroundScope = CoroutineScope(Dispatchers.IO + job)

    override fun onResume() {
        super.onResume()

        Wearable.getChannelClient(applicationContext)
            .registerChannelCallback(object : ChannelCallback() {
                override fun onChannelOpened(channel: ChannelClient.Channel) {
                    super.onChannelOpened(channel)
                    Log.d(tag, "onChannelOpened")
                    backgroundScope.launch {
                        var outFile: File? = null
                        outFile = File(applicationContext.getFileStreamPath(imageFileName).path)
                        val fileUri = Uri.fromFile(outFile)
                        Wearable.getChannelClient(applicationContext)
                            .receiveFile(channel, fileUri, false)
                        Wearable.getChannelClient(applicationContext)
                            .registerChannelCallback(object : ChannelCallback() {
                                override fun onInputClosed(
                                    channel: ChannelClient.Channel,
                                    i: Int,
                                    i1: Int
                                ) {
                                    super.onInputClosed(channel, i, i1)
                                    Log.d(tag, "onInputClosed")

                                    runOnUiThread {
                                        val image = BitmapFactory.decodeFile(fileUri.path)
                                        if (image != null) {
                                            imageView.setImageBitmap(image)
                                        }
                                    }

                                    Wearable.getChannelClient(applicationContext).close(channel)
                                }
                            })
                    }
                }
            })
    }

}
