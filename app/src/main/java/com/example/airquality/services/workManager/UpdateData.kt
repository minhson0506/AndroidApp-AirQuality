package com.example.airquality.services.workManager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.sensors.getAllData
import com.example.airquality.services.sensors.getNewData
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Composable
fun UpdateData(scheduledExecutorService: ScheduledExecutorService, model: DataViewModel) {
    val sensorData: SensorModel? by model.getLatest().observeAsState()
    val isUpdated: Boolean by model.isUpdated.observeAsState(false)

    val data: List<SensorModel>? by model.getAllData().observeAsState(null)

    val sensorDataList = data?.filter { it != null }
    scheduledExecutorService.scheduleAtFixedRate({
        // repeat task: update new data
        getNewData(model, sensorData)
        if (!isUpdated) thread { getAllData(model, sensorDataList) }
    }, 0, 30, TimeUnit.SECONDS)
}
