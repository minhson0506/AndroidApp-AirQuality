package com.example.airquality.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import coil.compose.ImagePainter
import com.example.airquality.services.room.RoomDB
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.weather.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.max

class DataViewModel(application: Application): AndroidViewModel(application) {
    // store list of Wifi networks
    val wifiNetworks = MutableLiveData<List<String>>(null)

    // store data of weather in outside
    val weather = MutableLiveData<WeatherResponse?>(null)
    val image = MutableLiveData<String?>(null)

    // check connection
    val isOnline = MutableLiveData<Boolean>(true)
    // store data of sensor in inside
//    val sensorData = MutableLiveData<SensorResponse>(null)

    val indicator = MutableLiveData<String>("PM10")
    // data from Room
    private val roomDB = RoomDB.getInstance(application)
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Default)

    // get and set data of venues
//    fun getAllData(): LiveData<List<SensorModel>> = roomDB.sensorDao().getAll()

    fun getLatest(): LiveData<SensorModel> = roomDB.sensorDao().getLatest()

    fun getDataInDate(date: String): LiveData<List<SensorModel>> = roomDB.sensorDao().getDataInDate(date = date)

    fun getAllData(): LiveData<PagedList<SensorModel>> = roomDB.sensorDao().getAll().toLiveData(pageSize = 5000)

    fun insert(sensorModel: SensorModel) {
        coroutineScope.launch {
            roomDB.sensorDao().insert(sensorModel)
        }
    }

    fun update(sensorModel: SensorModel) {
        coroutineScope.launch {
            roomDB.sensorDao().update(sensorModel)
        }
    }

    fun delete(sensorModel: SensorModel) {
        coroutineScope.launch {
            roomDB.sensorDao().delete(sensorModel)
        }
    }

    // store data of location
    val lat = MutableLiveData<Double?>(null)
    val lon = MutableLiveData<Double?>(null)

    // data for slider
    val minArray = MutableLiveData(minValueInit)
    val maxArray = MutableLiveData(maxValueInit)

    // notification
    val enableNoti = MutableLiveData(false)

    // device name
    val deviceName = MutableLiveData("")

}