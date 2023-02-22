package com.example.airquality.components

import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.airquality.MainActivity
import com.example.airquality.R
import com.example.airquality.libraryComponent.SampleSlider
import com.example.airquality.libraryComponent.TextTitle
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.maxValueInit
import com.example.airquality.services.minValueInit
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.sensors.ApiDevice
import com.example.airquality.services.sensors.Name
import com.example.airquality.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun Settings(navController: NavController, model: DataViewModel) {
    val context = LocalContext.current

    val sensorData: SensorModel? by model.getLatest().observeAsState()

//    val deviceName by model.deviceName.observeAsState("")
    if (sensorData != null) {
        var text by remember { mutableStateOf(sensorData?.deviceName?.split("ISD")?.get(1)) }

        Log.d(MainActivity.tag, "Settings: device name $text")
        val checkedState by model.enableNoti.observeAsState(false)

        // get size of phone's screen
        val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
        val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
        val cardSize = dpValue * 0.9
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels / screenPixelDensity
        val cardHeight = screenHeight * 0.8

        val data = listOf(
            SliderData(0, "Pm10", 1f, 0.0f, 60.0f),
            SliderData(1, "Pm2.5", 0.1f, 0.0f, 20.0f),
            SliderData(2, "Pm1", 1f, 0.0f, 60.0f),
            SliderData(3, "Pm4", 1f, 0.0f, 60.0f),
            SliderData(4, "CO2", 10f, 0.0f, 6000f),
            SliderData(5, "Humidity", 1f, 30.0f, 80.0f),
            SliderData(6, "Light", 10f, 0.0f, 400.0f),
            SliderData(7, "Noise", 1f, 0.0f, 40.0f),
            SliderData(8, "Pressure", 10f, 900f, 1100f),
            SliderData(9, "Temperature", 1f, 10f, 55f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBlue),
            horizontalAlignment = CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .width(cardSize.dp)
                    .padding(top = 20.dp)
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    TextTitle(id = R.string.change)
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 20.dp)
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Row(
                            verticalAlignment = CenterVertically, modifier = Modifier
                                .padding(start = 10.dp)
                                .background(LightGray)
                                .width((cardSize * 0.8).dp)
                        ) {
                            Text(
                                text = "ISD",
                                fontFamily = bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 10.dp),
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
                                ApiDevice.apiInstance().changeName(Name(text!!))
                                    .enqueue(object : Callback<String> {
                                        override fun onResponse(
                                            call: Call<String>,
                                            response: Response<String>
                                        ) {
                                            if (response.isSuccessful) {
                                                Log.d(
                                                    MainActivity.tag,
                                                    "onResponse: ${response.body()}"
                                                )
                                                model.wifiNetworks.postValue(null)
                                                // navigate to landing page
                                                navController.navigate("landingPage")
                                                Toast.makeText(
                                                    context,
                                                    "Device name changed!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }
                                        }

                                        override fun onFailure(call: Call<String>, t: Throwable) {
                                            Log.d(
                                                MainActivity.tag,
                                                "onFailure: change device name fail with ${t.message}"
                                            )
                                            // navigate to landing page
                                            navController.navigate("landingPage")
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
                    .width(cardSize.dp).height(cardHeight.dp)
                    .padding(top = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextTitle(id = R.string.alert)
                        Switch(
                            checked = checkedState,
                            onCheckedChange = {
                                model.enableNoti.postValue(it)
                                if (it) {
                                    Toast.makeText(
                                        context,
                                        "Notification is on!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Notification is off!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .height((cardHeight * 0.7).dp)
                            .verticalScroll(rememberScrollState())
                    ) {
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

                    Button(
                        onClick = {
                            model.minArray.postValue(minValueInit)
                            model.maxArray.postValue(maxValueInit)
                        },
                        modifier = Modifier.align(CenterHorizontally).padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue)
                    ) {
                        Text(
                            text = stringResource(id = R.string.reset),
                            fontFamily = bold,
                            color = Red
                        )
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
