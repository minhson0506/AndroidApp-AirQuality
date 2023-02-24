package com.example.airquality.services.sensors

import android.util.Log
import com.example.airquality.MainActivity
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.room.SensorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getAllData(model: DataViewModel, sensorData: List<SensorModel>?) {
    ApiDevice.apiInstance().getAll()
    .enqueue(object : Callback<List<SensorResponse>> {
        override fun onResponse(
            call: Call<List<SensorResponse>>,
            response: Response<List<SensorResponse>>,
        ) {
            Log.d(MainActivity.tag, "get all data with url: " + call.request().url())
            if (response.isSuccessful) {
                Log.d(MainActivity.tag, "got all data")
                response.body()?.forEach {
                    if (sensorData == null) {
                        model.insert(
                            SensorModel(
                                id = 0,
                                alt = it.alt,
                                co2 = it.co2,
                                deviceId = it.deviceId,
                                deviceName = it.deviceName,
                                hum = it.hum,
                                lux = it.lux,
                                noise = it.noise,
                                pm1 = it.pm1,
                                pm10 = it.pm10,
                                pm2 = it.pm2,
                                pm4 = it.pm4,
                                pres = it.pres,
                                temp = it.temp,
                                time = it.time,
                            )
                        )
                    } else if (!sensorData?.map { item -> item.time }
                            ?.contains(it.time)!!) {
                        model.insert(
                            SensorModel(
                                id = 0,
                                alt = it.alt,
                                co2 = it.co2,
                                deviceId = it.deviceId,
                                deviceName = it.deviceName,
                                hum = it.hum,
                                lux = it.lux,
                                noise = it.noise,
                                pm1 = it.pm1,
                                pm10 = it.pm10,
                                pm2 = it.pm2,
                                pm4 = it.pm4,
                                pres = it.pres,
                                temp = it.temp,
                                time = it.time,
                            )
                        )
                    }
                }
                model.isUpdated.postValue(true)
            }
        }

        override fun onFailure(call: Call<List<SensorResponse>>, t: Throwable) {
            Log.d(MainActivity.tag, "get all data fail with url: " + call.request().url())
            Log.d(
                MainActivity.tag,
                "onFailure: when get all data, ${t.message}"
            )
        }
    })
}

fun getNewData(model: DataViewModel, sensorData: SensorModel?) {

    ApiDevice.apiInstance().getLatest().enqueue(object : Callback<SensorResponse> {
        override fun onResponse(
            call: Call<SensorResponse>,
            response: Response<SensorResponse>,
        ) {
            Log.d(MainActivity.tag, "onResponse: " + response.body())
            if (response.isSuccessful) {
                if (sensorData == null) {
                    model.insert(
                        SensorModel(
                            id = 0,
                            alt = response.body()?.alt,
                            co2 = response.body()?.co2,
                            deviceId = response.body()?.deviceId,
                            deviceName = response.body()?.deviceName,
                            hum = response.body()?.hum,
                            lux = response.body()?.lux,
                            noise = response.body()?.noise,
                            pm1 = response.body()?.pm1,
                            pm10 = response.body()?.pm10,
                            pm2 = response.body()?.pm2,
                            pm4 = response.body()?.pm4,
                            pres = response.body()?.pres,
                            temp = response.body()?.temp,
                            time = response.body()?.time,
                        )
                    )

                } else {
                    if (sensorData?.time != response.body()?.time) {
                        Log.d(
                            MainActivity.tag,
                            "onResponse: insert new data with time ${response.body()?.time}"
                        )
                        model.insert(
                            SensorModel(
                                id = 0,
                                alt = response.body()?.alt,
                                co2 = response.body()?.co2,
                                deviceId = response.body()?.deviceId,
                                deviceName = response.body()?.deviceName,
                                hum = response.body()?.hum,
                                lux = response.body()?.lux,
                                noise = response.body()?.noise,
                                pm1 = response.body()?.pm1,
                                pm10 = response.body()?.pm10,
                                pm2 = response.body()?.pm2,
                                pm4 = response.body()?.pm4,
                                pres = response.body()?.pres,
                                temp = response.body()?.temp,
                                time = response.body()?.time,
                            )
                        )
                    }
                }
            }
        }

        override fun onFailure(call: Call<SensorResponse>, t: Throwable) {
            Log.d(MainActivity.tag, "onFailure: when update " + t.message)
        }
    })
}