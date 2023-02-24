package com.example.airquality.services.workManager

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.airquality.MainActivity
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.sensors.ApiDevice
import com.example.airquality.services.sensors.SensorResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Composable
fun UpdateData(scheduledExecutorService: ScheduledExecutorService, model: DataViewModel) {
    val sensorData: SensorModel? by model.getLatest().observeAsState()

    scheduledExecutorService.scheduleAtFixedRate({
        // repeat task: update new data
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
    }, 0, 30, TimeUnit.SECONDS)
}
