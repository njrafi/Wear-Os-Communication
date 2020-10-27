package com.example.communicationdemo

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

object ImageReceiver {
    private val tag = "ImageReceiver"
    private const val imageFileName = "ImageFromMobile.png"
    private val job = Job()
    val backgroundScope = CoroutineScope(Dispatchers.IO + job)

    private fun broadcastImagePathUri(imagePathUri: String?, context: Context) {
        if (imagePathUri == null) return
        val intent = Intent()
        intent.action = ImageMessageConstants.intentName
        intent.putExtra(ImageMessageConstants.imagePathUri, imagePathUri)
        Log.i(tag, "Image Broadcasts")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun registerChannel(context: Context) {
        Wearable.getChannelClient(context)
            .registerChannelCallback(object : ChannelClient.ChannelCallback() {
                override fun onChannelOpened(channel: ChannelClient.Channel) {
                    super.onChannelOpened(channel)
                    Log.d(tag, "onChannelOpened")
                    backgroundScope.launch {
                        var outFile: File? = null
                        outFile = File(context.getFileStreamPath(imageFileName).path)
                        val fileUri = Uri.fromFile(outFile)
                        Wearable.getChannelClient(context)
                            .receiveFile(channel, fileUri, false)
                        registerInputClosedCallback(context, fileUri)
                    }
                }
            })
    }

    private fun registerInputClosedCallback(context: Context, fileUri: Uri) {
        Wearable.getChannelClient(context)
            .registerChannelCallback(object : ChannelClient.ChannelCallback() {
                override fun onInputClosed(channel: ChannelClient.Channel, i: Int, i1: Int) {
                    super.onInputClosed(channel, i, i1)
                    Log.d(tag, "onInputClosed")
                    broadcastImagePathUri(fileUri.path, context)
                    Wearable.getChannelClient(context).close(channel)
                }
            })
    }
}