package com.example.communicationdemo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ExecutionException


object ImageSenderService {
    private val tag = "ImageSenderService"
    private const val CHANNEL_MSG = "com.example.communicationdemo.data.test"
    private const val FILE_IMAGE = "ImageFromMobile.png"
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun sendImageToWearable(image: Bitmap?, context: Context) {
        coroutineScope.launch {
            sendImageToWearableInBackground(context, image)
        }
    }

    private fun getNodes(context: Context): Collection<String>? {
        val results = HashSet<String>()
        val nodeListTask = Wearable.getNodeClient(context).connectedNodes
        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            val nodes = Tasks.await(nodeListTask)
            for (node in nodes) {
                results.add(node.id)
            }
        } catch (exception: ExecutionException) {
            Log.e(tag, "Task failed: $exception")
        } catch (exception: InterruptedException) {
            Log.e(tag, "Interrupt occurred: $exception")
        }
        return results
    }

    private fun sendImageToWearableInBackground(context: Context, imageBitmap: Bitmap?) {
        val nodes = getNodes(context)
        Log.i(
            tag, "Nodes: " + nodes!!.size
        )
        for (node in nodes) {
            val channelTask = Wearable.getChannelClient(context).openChannel(
                node, CHANNEL_MSG
            )
            channelTask.addOnSuccessListener { channel ->
                Log.i(
                    tag, "onSuccess " + channel.nodeId
                )
                var fileUri: Uri? = null
                var outFile: File? = null
                //File textFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/given_message.txt");
                try {

                    outFile = File(context.getFileStreamPath(FILE_IMAGE).path)

                    fileUri = Uri.fromFile(outFile)
                    //Files.deleteIfExists(Paths.get(fileUri.path))
                    val out = FileOutputStream(fileUri.path)
                    imageBitmap?.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        out
                    ) // PNG is a lossless format, the compression factor (100) is ignored

                    out.flush()
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (fileUri != null)
                    Wearable.getChannelClient(context).sendFile(channel, fileUri)
            }
        }
    }
}