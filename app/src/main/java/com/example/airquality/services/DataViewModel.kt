package com.example.airquality.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.airquality.services.sensors.SensorResponse

class DataViewModel(application: Application): AndroidViewModel(application) {
    val wifiNetworks = MutableLiveData<List<String>>(null)

//    val index = MutableLiveData<Int>(0)

    val sensorData = MutableLiveData<SensorResponse>(null)
}