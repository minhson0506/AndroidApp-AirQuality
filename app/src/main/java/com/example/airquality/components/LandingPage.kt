package com.example.airquality.components

import android.content.res.Resources
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.airquality.R
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.GetWeather
import com.example.airquality.services.connectDevice
import com.example.airquality.services.scanWifi
import com.example.airquality.ui.theme.Black
import com.example.airquality.ui.theme.LightBlue
import com.example.airquality.ui.theme.bold


@Composable
fun LandingPage(model: DataViewModel, navController: NavController) {
    // get size of phone's screen
    val context = LocalContext.current
    val screenPixelDensity = context.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8

    val wifiNetworks: List<String>? by model.wifiNetworks.observeAsState(null)

    var wifiName by remember { mutableStateOf<String?>(null) }

    GetWeather(model = model)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = CenterHorizontally
        ) {
            Image(
                painterResource(id = R.drawable.icon),
                "",
                modifier = Modifier.width(120.dp),
                alignment = Alignment.Center
            )
            Image(
                painterResource(id = R.drawable.landing),
                "",
                modifier = Modifier
                    .width(dpValue.dp)
                    .height(300.dp)
                    .padding(top = 10.dp),
                alignment = Alignment.Center
            )
        }

        if (!wifiNetworks.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            ) {
                Text(text = "Wifi Networks", modifier = Modifier.align(CenterHorizontally))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((dpValue * 0.35).dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    wifiNetworks?.forEach {
                        Text(text = it, modifier = Modifier
                            .height(30.dp)
                            .align(CenterHorizontally)
                            .clickable {
                                wifiName = it
                            })
                    }
                }
            }
        }

        if (wifiName != null) {
            connectDevice(ssid = wifiName!!,
                context = context,
                navController = navController,
                model = model)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue),
                modifier = Modifier
                    .align(CenterHorizontally)
                    .width(cardSize.dp)
                    .height(60.dp),
                onClick = {
                    scanWifi(model, context)
                }

            ) {
                Text(
                    stringResource(id = R.string.wifi),
                    textAlign = TextAlign.Center,
                    color = Black,
                    fontSize = 18.sp,
                    fontFamily = bold
                )
            }
            Button(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(top = 10.dp)
                    .width(cardSize.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue),
                onClick = {
                    // for cloud
                    navController.navigate("main")

                }
            ) {
                Text(
                    stringResource(id = R.string.cloud),
                    textAlign = TextAlign.Center,
                    color = Black,
                    fontFamily = bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}




