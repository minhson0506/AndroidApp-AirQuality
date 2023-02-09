package com.example.airquality.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.airquality.MainActivity
import com.example.airquality.R
import com.example.airquality.libraryComponent.NumberText
import com.example.airquality.libraryComponent.UnitText
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.maxValueInit
import com.example.airquality.services.minValueInit
import com.example.airquality.services.notification.Notification
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.sensors.ApiDevice
import com.example.airquality.services.sensors.SensorResponse
import com.example.airquality.services.weather.WeatherResponse
import com.example.airquality.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Dashboard(model: DataViewModel) {
    val context = LocalContext.current
    val sensorData: SensorModel? by model.getLatest().observeAsState(null)
    val weather: WeatherResponse? by model.weather.observeAsState(null)
    val image: String? by model.image.observeAsState(null)

    val deviceName by model.deviceName.observeAsState("")

    val minValue by model.minArray.observeAsState(minValueInit)
    val maxValue by model.maxArray.observeAsState(maxValueInit)

    val array = listOf(
        null, null,
        Value(
            0, "Pm10", R.drawable.wind, sensorData?.pm10, "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 10.0µm in µg/m3"
        ),
        Value(
            1, "Pm2.5", R.drawable.wind,
            sensorData?.pm2,
            "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 2.5µm in µg/m3"
        ),
        Value(
            2,
            "Pm1",
            R.drawable.wind,
            sensorData?.pm1,
            "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 1.0µm in µg/m3"
        ),
        Value(
            3,
            "Pm4",
            R.drawable.wind,
            sensorData?.pm4,
            "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 4.0µm in µg/m3"
        ),
        Value(
            4,
            "CO2", R.drawable.co2,
            sensorData?.co2, "ppm", "Carbon dioxide in ppm"
        ),
        Value(5, "Humidity", R.drawable.humidity, sensorData?.hum, "RH", "Humidity in %RH"),
        Value(6, "Light", R.drawable.light, sensorData?.lux, "lux", "Lighting in lux"),
        Value(
            7, "Noise", R.drawable.sound,
            sensorData?.noise,
            "dB", "Loudness in dB"
        ),
        Value(8, "Pressure", R.drawable.pressure, sensorData?.pres, "hPa", "Pressure in hPa"),
        Value(
            9,
            "Temp", R.drawable.temp,
            sensorData?.temp,
            "°C", "Temperature in °C",
        )
    )

    // init noti
    val enableNoti by model.enableNoti.observeAsState(false)

    if (enableNoti) {
        array.forEach {
            if (it?.data != null) {
                val notification =
                    Notification(context, "Warning: " + it.name + " is out of safe range", "")
                if (it.data > maxValue[it.id] || it.data < minValue[it.id]) {
                    Log.d(MainActivity.tag, "Dashboard: start noti")
                    notification.notification(it.id)
                } else {
                    Log.d(MainActivity.tag, "Dashboard: stop noti")
                    notification.cancel(it.id)
                }
            }
        }
    }

    // set device + room name
    var title by remember { mutableStateOf("") }

    if(sensorData?.deviceName != null && deviceName == "") {
        title = sensorData!!.deviceName.toString()
    } else if(sensorData?.deviceName != null) {
        title = sensorData!!.deviceName + "-" + deviceName
    } else {
        title = "ISD"
    }

    val scheduledExecutorService: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()

    UpdateData(scheduledExecutorService = scheduledExecutorService, model = model)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {

                    Text(
                        text = title,
                        fontFamily = bold,
                        fontSize = 18.sp,
                        color = Black
                    )

                val time = sensorData?.time?.split(",")
                Text(
                    text = if(time?.get(0) != null) "Date: ${time[0]}" else "",
                    fontFamily = medium,
                    fontSize = 16.sp,
                    color = DarkGray
                )
                Text(
                    text = if(time?.get(1) != null) "Time: ${time[1].trim()}" else "",
                    fontFamily = medium,
                    fontSize = 16.sp,
                    color = DarkGray
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(id = R.drawable.location),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(15.dp),
                        colorFilter = ColorFilter.tint(color = Red)
                    )

                        Text(
                            text = if(weather?.location?.name != null) weather!!.location.name else "No location",
                            fontFamily = bold,
                            fontSize = 18.sp,
                            color = Black
                        )


                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (image != null) {
                        val myBitmap = BitmapFactory.decodeFile(image)
                        Image(
                            bitmap = myBitmap.asImageBitmap(),
                            contentDescription = weather?.current?.condition?.text,
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 5.dp)
                        )
                    }
                    Text(if(weather?.current?.temp != null)
                        weather?.current?.temp?.toInt().toString() + "°C" else "",
                        fontFamily = medium,
                        fontSize = 16.sp,
                        color = DarkGray
                    )
                }

            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            items(array) {

                var popupControl by remember { mutableStateOf(false) }

                Log.d(MainActivity.tag, "Dashboard: ${it?.name} & popup $popupControl")
                if (popupControl) {
                    Log.d(MainActivity.tag, "Dashboard: in popup ${it?.name}")
                    Popup(
                        alignment = Alignment.BottomCenter,
                        properties = PopupProperties(dismissOnClickOutside = true),
                        onDismissRequest = { popupControl = false }) {
                        Box(
                            Modifier
                                .size(300.dp, 100.dp)
                                .padding(top = 5.dp)
                                .background(LightBlue, RoundedCornerShape(10.dp))
                                .border(1.dp, color = Color.Black, RoundedCornerShape(10.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                it?.desc?.let { it1 ->
                                    Text(
                                        text = it1,
                                        color = Color.Black,
                                        modifier = Modifier.padding(vertical = 5.dp),
                                        fontFamily = medium
                                    )
                                }
                            }
                        }
                    }
                }
                if (it == null) {
                    Card(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height(1.dp),
                        backgroundColor = LightBlue
                    ) {
                    }
                } else {
                    Card(
                        modifier = Modifier.padding(
                            all = 10.dp
                        ),
                        backgroundColor = White,
                        onClick = {
                            popupControl = !popupControl
                            Log.d(MainActivity.tag, "Dashboard: ${it.name}")
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painterResource(id = it.image),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(
                                            if (((it.data ?: 0.0) < minValue[it.id]) || ((it.data
                                                    ?: 0.0) > maxValue[it.id])
                                            ) Red else Green
                                        )
                                        .padding(5.dp),
                                    colorFilter = ColorFilter.tint(color = White)
                                )
                                Text(
                                    text = it.name,
                                    color = Black,
                                    fontSize = 20.sp,
                                    fontFamily = bold,
                                    modifier = Modifier.padding(top = 15.dp, start = 5.dp)
                                )

                            }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NumberText(text = if(it.data != null) it.data.toString() else "")
                                UnitText(text = it.unit)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Value(
    val id: Int,
    val name: String,
    val image: Int,
    val data: Double?,
    val unit: String,
    val desc: String
)

@Composable
fun UpdateData(scheduledExecutorService: ScheduledExecutorService, model: DataViewModel) {
    val sensorData: List<SensorModel>? by model.getAllData().observeAsState(null)

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
                    } else if (!sensorData?.map { item -> item.time }
                            ?.contains(response.body()?.time)!!) {
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

            override fun onFailure(call: Call<SensorResponse>, t: Throwable) {
                Log.d(MainActivity.tag, "onFailure: " + t.message)
            }
        })
    }, 0, 30, TimeUnit.SECONDS)
}
