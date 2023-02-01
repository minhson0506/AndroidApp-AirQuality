package com.example.airquality.services

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import coil.compose.rememberImagePainter
import com.example.airquality.MainActivity
import com.example.airquality.services.weather.ApiWeather
import com.example.airquality.services.weather.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun GetWeather(model: DataViewModel) {
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
                    Log.d(MainActivity.tag, "onFailure: " + t.message)
                }
            })
        if (weather != null) {
            val image = rememberImagePainter(data = "https:" + weather!!.current.condition.icon)
            model.image.postValue(image)
        }
    }

    Log.d(MainActivity.tag, "GetWeather: Long: $lon lat: $lat")
}