package com.example.airquality.components

import android.app.Notification
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airquality.MainActivity
import com.example.airquality.R
import com.example.airquality.libraryComponent.SampleSlider
import com.example.airquality.libraryComponent.TextTitle
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.sensors.ApiDevice
import com.example.airquality.services.sensors.Name
import com.example.airquality.ui.theme.AirQualityTheme
import com.example.airquality.ui.theme.LightBlue
import com.example.airquality.ui.theme.LightGray
import com.example.airquality.ui.theme.bold
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun Settings(navController: NavController, model: DataViewModel) {
    val context = LocalContext.current

    val sensorData: SensorModel? by model.getLatest().observeAsState()

//    val deviceName by model.deviceName.observeAsState("")
    if (sensorData != null)
    {
        var text by remember { mutableStateOf(sensorData?.deviceName?.split("ISD")?.get(1)) }

        Log.d(MainActivity.tag, "Settings: device name $text")
        val checkedState by model.enableNoti.observeAsState(false)

        // get size of phone's screen
        val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
        val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
        val cardSize = dpValue * 0.9

        val data = listOf(
            SliderData(0, "Pm10", 0.01f, 0.2f, 1.0f),
            SliderData(1, "Pm2.5", 0.01f, 0.2f, 2.0f),
            SliderData(2, "Pm1", 0.01f, 0.2f, 1.0f),
            SliderData(3, "Pm4", 0.01f, 0.2f, 1.0f),
            SliderData(4, "CO2", 10f, 100f, 800f),
            SliderData(5, "Humidity", 1f, 1f, 30f),
            SliderData(6, "Light", 10f, 10f, 60f),
            SliderData(7, "Noise", 10f, 10f, 150f),
            SliderData(8, "Pressure", 10f, 900f, 1100f),
            SliderData(9, "Temperature", 1f, 10f, 55f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBlue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .width(cardSize.dp)
                    .padding(top = 20.dp)
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    TextTitle(id = R.string.change)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 20.dp)
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                .padding(start = 10.dp)
                                .background(LightGray)
                                .width((cardSize * 0.8).dp)
                        ) {
                            Text(
                                text = "ISD",
                                fontFamily = bold,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            text?.let {
                                TextField(
                                    value = it,
                                    onValueChange = { newText ->
                                        text = newText
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = LightGray,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                )
                            }
                        }
                        IconButton(onClick = {
                            // post change device name and navigator to landing page
//                        model.deviceName.postValue(text)
                            if (text != null) {
                                ApiDevice.apiInstance().changeName(Name(text!!)).enqueue(object : Callback<String> {
                                    override fun onResponse(
                                        call: Call<String>,
                                        response: Response<String>
                                    ) {
                                        if (response.isSuccessful) {
                                            Log.d(MainActivity.tag, "onResponse: ${response.body()}")
                                            // wait to restart hardware
                                            Thread.sleep(2000)
                                            // navigate to landing page
                                            navController.navigate("landingPage")
                                        }
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                        Log.d(MainActivity.tag, "onFailure: change device name fail with ${t.message}")
                                        // wait to restart hardware
                                        Thread.sleep(2000)
                                        // navigate to landing page
//                                        navController.navigate("landingPage")
                                    }
                                })
                            }

                        }) {
                            Image(
                                painterResource(id = R.drawable.checkbox),
                                contentDescription = "Favorite",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .width(cardSize.dp)
                    .padding(top = 20.dp)
            ) {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextTitle(id = R.string.alert)
                        Switch(
                            checked = checkedState,
                            onCheckedChange = {
                                model.enableNoti.postValue(it)
                                if (it) {
                                    Toast.makeText(context, "Notification is on!", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(context, "Notification is off!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        )
                    }

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        for (item in data) {
                            SampleSlider(
                                id = item.id,
                                text = item.name,
                                min = item.min,
                                max = item.max,
                                step = item.step,
                                model = model
                            )
                        }
                    }

                }

            }
        }
    }


}

data class SliderData(
    val id: Int,
    val name: String,
    val step: Float,
    val min: Float,
    val max: Float
)
