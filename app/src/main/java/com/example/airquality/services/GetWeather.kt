package com.example.airquality.services

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.airquality.MainActivity
import com.example.airquality.services.weather.ApiWeather
import com.example.airquality.services.weather.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import kotlin.concurrent.thread


@Composable
fun GetWeather(model: DataViewModel) {
    val context = LocalContext.current
    val lat: Double? by model.lat.observeAsState(null)
    val lon: Double? by model.lon.observeAsState(null)
    val weather: WeatherResponse? by model.weather.observeAsState(null)
    val isOnline: Boolean by model.isOnline.observeAsState(true)

    if (isOnline) {
        ApiWeather.apiInstance().getWeather(location = lat.toString() + "," + lon)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>,
                ) {
                    if (response.isSuccessful) {
                        Log.d(MainActivity.tag,
                            "onResponse: name " + (response.body()))
                        if (response.body()?.location?.name != null) {
                            model.weather.postValue(response.body())
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.d(MainActivity.tag, "onFailure: when get weather info" + t.message)
                }
            })
        Log.d(MainActivity.tag, "GetWeather: $weather")
        if (weather != null) {
            val destinationFile = "image.jpg"
            val url = URL("https:" + weather!!.current.condition.icon)
            val cw = ContextWrapper(context)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val path = File(directory, destinationFile)
            Log.d(MainActivity.tag, "GetWeather: $path")
            thread {
                try {
                    val inputStream = url.openStream()
                    val os: OutputStream = FileOutputStream(path)
                    val buffer = ByteArray(4 * 1024) // buffer size
                    while (true) {
                        val byteCount = inputStream.read(buffer)
                        if (byteCount < 0) break
                        os.write(buffer, 0, byteCount)
                    }
                    os.flush()
                    inputStream.close()
                    os.close()
                } catch (error: IOException) {
                    Log.d(MainActivity.tag, "GetWeather: $error")
                }
            }
//            val image = rememberImagePainter(data = "https:" + weather!!.current.condition.icon)
                model.image.postValue(path.absolutePath)
        }
    }

    Log.d(MainActivity.tag, "GetWeather: Long: $lon lat: $lat")
}