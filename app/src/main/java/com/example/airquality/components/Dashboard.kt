package com.example.airquality.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.airquality.MainActivity
import com.example.airquality.R
import com.example.airquality.libraryComponent.Headline
import com.example.airquality.libraryComponent.NumberText
import com.example.airquality.libraryComponent.UnitText
import com.example.airquality.services.DataViewModel
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
    val sensorData: SensorModel? by model.getLatest().observeAsState(null)
    val weather: WeatherResponse? by model.weather.observeAsState(null)
    val image: ImagePainter? by model.image.observeAsState(null)

    val array = listOf(
        null, null,
        Triple(
            Pair("Pm10", R.drawable.wind),
            sensorData?.pm10,
            Pair(
                "µg/m3",
                "Particle density of particulate Matter(PM) in size range 0.3µm to 10.0µm in µg/m3"
            )
        ),
        Triple(
            Pair("Pm2.5", R.drawable.wind),
            sensorData?.pm2,
            Pair(
                "µg/m3",
                "Particle density of particulate Matter(PM) in size range 0.3µm to 2.5µm in µg/m3"
            )
        ),
        Triple(
            Pair(
                "CO2", R.drawable.co2
            ),
            sensorData?.co2,
            Pair("ppm", "Carbon dioxide in ppm")
        ),
        Triple(Pair("Humidity", R.drawable.humidity),
            sensorData?.hum,
            Pair("RH", "Humidity in %RH")),
        Triple(Pair("Light", R.drawable.light), sensorData?.lux, Pair("lux", "Lighting in lux")),
        Triple(
            Pair("Noise", R.drawable.sound),
            sensorData?.noise,
            Pair("dB", "Loudness in dB")
        ),
        Triple(Pair("Pressure", R.drawable.pressure), sensorData?.pres, Pair("hPa", "Pressure in hPa")),
        Triple(
            Pair(
                "Temp", R.drawable.temp
            ),
            sensorData?.temp,
            Pair("°C", "Temperature in °C")
        ),
        Triple(
            Pair(
                "Pm1", R.drawable.wind
            ),
            sensorData?.pm1,
            Pair("µg/m3", "Particle density of particulate Matter(PM) in size range 0.3µm to 2.5µm in µg/m3")
        ),
        Triple(
            Pair(
                "Pm4", R.drawable.wind
            ),
            sensorData?.pm4,
            Pair("µg/m3", "Particle density of particulate Matter(PM) in size range 0.3µm to 2.5µm in µg/m3")
        ),
    )

    var scheduledExecutorService: ScheduledExecutorService =
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text("ISD420 KMC 752", fontFamily = medium, fontSize = 18.sp, color = DarkGray)
                Text("Time get data:")
                val time = sensorData?.time?.split(",")
                Text(text = "Date: ${time?.get(0)}")
                Text(text = "Time: ${time?.get(1)?.trim()}")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(id = R.drawable.location),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(15.dp),
                        colorFilter = ColorFilter.tint(color = Red)
                    )
                    weather?.location?.let { Text(text = it.name, fontFamily = bold, fontSize = 18.sp, color = Black) }

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Log.d(MainActivity.tag, "Dashboard: in image 1" + image)
                    if (image != null) {
                        Log.d(MainActivity.tag, "Dashboard: in image 2" + image)
                        Image(painter = image!!,
                            contentDescription = weather?.current?.condition?.text, modifier = Modifier
                                .size(50.dp)
                                .padding(end = 5.dp).background(Color.Red))
                    }
                    Text(weather?.current?.temp.toString() + "°C", fontFamily = medium, fontSize = 18.sp, color = DarkGray)
                }

            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            items(array) {

                var popupControl by remember { mutableStateOf(false) }

                Log.d(MainActivity.tag, "Dashboard: ${it?.first} & popup $popupControl")
                if (popupControl) {
                    Log.d(MainActivity.tag, "Dashboard: in popup ${it?.first}")
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
                                it?.third?.second?.let { it1 ->
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
                    Card(modifier = Modifier
                        .padding(bottom = 10.dp)
                        .height(1.dp),
                        backgroundColor = LightBlue) {
                    }
                } else {
                    Card(
                        modifier = Modifier.padding(
                            all = 10.dp
                        ),
                        backgroundColor = White,
                        onClick = {
                            popupControl = !popupControl
                            Log.d(MainActivity.tag, "Dashboard: ${it.first}")
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painterResource(id = it.first.second),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(Green)
                                        .padding(5.dp),
                                    colorFilter = ColorFilter.tint(color = White)
                                )
                                Text(
                                    text = it.first.first,
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
                                NumberText(text = it.second.toString())
                                UnitText(text = it.third.first)
                            }
                        }
                    }
                }
            }
        }
    }
}


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
                        model.insert(SensorModel(
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
                        ))
                    } else if (!sensorData?.map { item -> item.time }?.contains(response.body()?.time)!!) {
                        model.insert(SensorModel(
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
                        ))
//                            model.sensorData.postValue(SensorResponse(response.body()?.alt,
//                                response.body()?.co2,
//                                response.body()?.deviceId,
//                                response.body()?.deviceName,
//                                response.body()?.hum,
//                                response.body()?.lux,
//                                response.body()?.noise,
//                                response.body()?.pm1,
//                                response.body()?.pm10,
//                                response.body()?.pm2,
//                                response.body()?.pm4,
//                                response.body()?.pres,
//                                response.body()?.temp,
//                                response.body()?.time))
                    }
                }
            }

            override fun onFailure(call: Call<SensorResponse>, t: Throwable) {
                Log.d(MainActivity.tag, "onFailure: " + t.message)
            }
        })
    }, 0, 30, TimeUnit.SECONDS)
}
