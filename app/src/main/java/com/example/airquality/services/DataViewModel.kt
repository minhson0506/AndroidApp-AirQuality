package com.example.airquality.services

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import coil.compose.ImagePainter
import com.example.airquality.services.sensors.SensorResponse
import com.example.airquality.services.weather.WeatherResponse

class DataViewModel(application: Application): AndroidViewModel(application) {
    val wifiNetworks = MutableLiveData<List<String>>(null)

//    val index = MutableLiveData<Int>(0)

    val weather = MutableLiveData<WeatherResponse?>(null)

    val sensorData = MutableLiveData<SensorResponse>(null)

    val image = MutableLiveData<ImagePainter?>(null)

    val lat = MutableLiveData<Double?>(null)

    val lon = MutableLiveData<Double?>(null)
}