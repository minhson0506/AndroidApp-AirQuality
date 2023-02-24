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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.airquality.MainActivity
import com.example.airquality.R
import com.example.airquality.services.component.NumberText
import com.example.airquality.services.component.UnitText
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.dataclass.ValueDisplay
import com.example.airquality.services.maxValueInit
import com.example.airquality.services.minValueInit
import com.example.airquality.services.notification.Notification
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.weather.WeatherResponse
import com.example.airquality.services.workManager.UpdateData
import com.example.airquality.ui.theme.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Dashboard(model: DataViewModel) {
    val context = LocalContext.current
    val sensorData: SensorModel? by model.getLatest().observeAsState(null)
    val weather: WeatherResponse? by model.weather.observeAsState(null)
    val image: String? by model.image.observeAsState(null)
    val minValue by model.minArray.observeAsState(minValueInit)
    val maxValue by model.maxArray.observeAsState(maxValueInit)

    Log.d(MainActivity.tag, "time of record" + sensorData?.time)
    val dashboardArray = listOf(
        null, null,
        ValueDisplay(
            0, "Pm10", R.drawable.wind, sensorData?.pm10, "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 10.0µm in µg/m3",
            stringResource(id = R.string.outside) + ((weather?.current?.airQuality?.pm10?.times(10))?.roundToInt()
                ?.div(10.0) ?: 0)
        ),
        ValueDisplay(
            1, "Pm2.5", R.drawable.wind,
            sensorData?.pm2,
            "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 2.5µm in µg/m3",
            stringResource(id = R.string.outside) + ((weather?.current?.airQuality?.pm2_5?.times(10))?.roundToInt()
                ?.div(10.0)
                ?: 0)
        ),
        ValueDisplay(
            2,
            "Pm1",
            R.drawable.wind,
            sensorData?.pm1,
            "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 1.0µm in µg/m3",
            ""
        ),
        ValueDisplay(
            3,
            "Pm4",
            R.drawable.wind,
            sensorData?.pm4,
            "µg/m3",
            "Particle density of particulate Matter(PM) in size range 0.3µm to 4.0µm in µg/m3",
            ""
        ),
        ValueDisplay(
            4,
            "CO2", R.drawable.co2,
            sensorData?.co2, "ppm", "Carbon dioxide in ppm",
            stringResource(id = R.string.outside) + ((weather?.current?.airQuality?.co?.times(10))?.roundToInt()
                ?.div(10.0) ?: 0)
        ),
        ValueDisplay(
            5,
            "Humidity",
            R.drawable.humidity,
            sensorData?.hum,
            "RH",
            "Humidity in %RH",
            stringResource(id = R.string.outside) + weather?.current?.humidity
        ),
        ValueDisplay(6, "Light", R.drawable.light, sensorData?.lux, "lux", "Lighting in lux", ""),
        ValueDisplay(
            7, "Noise", R.drawable.sound,
            sensorData?.noise,
            "dB", "Loudness in dB", ""
        ),
        ValueDisplay(
            8,
            "Pressure",
            R.drawable.pressure,
            sensorData?.pres,
            "hPa",
            "Pressure in hPa",
            stringResource(id = R.string.outside) + ((weather?.current?.pressure?.times(10))?.roundToInt()
                ?.div(10.0) ?: 0)
        ),
        ValueDisplay(
            9,
            "Temp", R.drawable.temp,
            sensorData?.temp,
            "°C", "Temperature in °C",
            stringResource(id = R.string.outside) + ((weather?.current?.temp?.times(10))?.roundToInt()
                ?.div(10.0) ?: 0)
        )
    )

    // init notification
    val enableNotification by model.enableNotification.observeAsState(false)

    if (enableNotification) {
        dashboardArray.forEach {
            if (it?.data != null) {
                val notification =
                    Notification(context, "Warning: " + it.name + " is out of safe range", "")
                if (it.data > maxValue[it.id] || it.data < minValue[it.id]) {
                    Log.d(MainActivity.tag, "Dashboard: start notification service")
                    notification.notification(it.id)
                } else {
                    Log.d(MainActivity.tag, "Dashboard: stop notification service")
                    notification.cancel(it.id)
                }
            }
        }
    }

    // set device + room name
    var title by remember { mutableStateOf("") }

    title = if (sensorData?.deviceName != null && sensorData?.deviceName != "") {
        sensorData!!.deviceName.toString()
    } else {
        "ISD"
    }

    val scheduledExecutorService: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()

    // auto update data with task manager
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
                    text = if (time?.get(0) != null) "Date: ${time[0]}" else "",
                    fontFamily = medium,
                    fontSize = 16.sp,
                    color = DarkGray
                )
                Text(
                    text = if (time?.get(1) != null) "Time: ${time[1].trim()}" else "",
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
                        text = if (weather?.location?.name != null && weather?.location?.name != "null") weather!!.location.name else "No location",
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
                    Text(
                        if (weather?.current?.temp != null)
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
            items(dashboardArray) {
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
                                .size(300.dp, 150.dp)
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
                                if (it != null) {
                                    Column() {
                                        Text(
                                            text = it.desc, color = Color.Black,
                                            modifier = Modifier.padding(vertical = 5.dp),
                                            fontFamily = medium
                                        )
                                        if (it.outside != "") {
                                            Text(
                                                text = it.outside + " " + it.unit,
                                                color = Color.Black,
                                                modifier = Modifier.padding(vertical = 5.dp),
                                                fontFamily = medium
                                            )
                                        }
                                    }
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
                        modifier = Modifier.padding(all = 10.dp),
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
                                NumberText(text = if (it.data != null) it.data.toString() else "")
                                UnitText(text = it.unit)
                            }
                        }
                    }
                }
            }
        }
    }
}

