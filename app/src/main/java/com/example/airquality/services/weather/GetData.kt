package com.example.airquality.services.weather

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class GetData {

}

private suspend fun getImg(context: Context, url: URL): Bitmap? = withContext(Dispatchers.IO) {
//    if (isNetworkAvailable(context = context)) {
        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            return@withContext BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//    } else Log.d(TAG, "Network is not available")
    return@withContext null
}