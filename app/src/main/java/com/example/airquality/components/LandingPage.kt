package com.example.airquality.components

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
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
import com.example.airquality.ui.theme.Black
import com.example.airquality.ui.theme.LightBlue
import com.example.airquality.ui.theme.bold
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener


@Composable
fun LandingPage(model: DataViewModel, navController: NavController) {
    // get size of phone's screen
    val context = LocalContext.current
    val screenPixelDensity = context.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8

    val wifiNetworks: List<String>? by model.wifiNetworks.observeAsState(null)

    var displayNetwork by remember { mutableStateOf<Boolean>(false) }
    var tryConnect by remember { mutableStateOf<Boolean>(false) }
    var wifiName by remember { mutableStateOf<String?>(null) }


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

        if (displayNetwork) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)) {
                Text(text = "Wifi Networks", modifier = Modifier.align(CenterHorizontally))
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .height((dpValue * 0.3).dp)
                    .verticalScroll(rememberScrollState())) {
                    wifiNetworks?.forEach { item -> Text(text = item, modifier = Modifier
                        .height(20.dp)
                        .align(CenterHorizontally)
                        .clickable {
                            tryConnect = true
                            wifiName = item
                        }) }
                }
            }
        }

        if (tryConnect && wifiName != null) {
            connectDevice(ssid = wifiName!!, context = context, navController = navController)
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
                    disconnectWifi(context)
                    displayNetwork = true
                    scanWifi(model, context)
//                    navController.navigate("main")
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
//                    tryConnnect = true
//                    connectDevice(context)
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

fun disconnectWifi(context: Context) {
    WifiUtils.withContext(context)
        .disconnect(object : DisconnectionSuccessListener {
            override fun success() {
                Log.d("airquality", "disconnect success: ")
            }

            override fun failed(errorCode: DisconnectionErrorCode) {
                Log.d("airquality", "disconnect fail: ")

            }
        })
}

fun scanWifi(model: DataViewModel, context: Context) {
    Log.d("airquality", "getScanResults: start")
    WifiUtils.withContext(context).scanWifi { scanResults ->
        model.wifiNetworks.postValue(scanResults.map { item ->
            item.toString().split(",")[0].split(":")[1].trim()
        }
            .filter { item -> item.startsWith("ISD") }
    )
    }.start()

}

@Composable
fun connectDevice(ssid: String, context: Context, navController: NavController) {
    val tag = "airquality"

    WifiUtils.withContext(context).enableWifi()
    WifiUtils.withContext(context).connectWith(ssid, "sensor22").setTimeout(40000)
        .onConnectionResult(object : ConnectionSuccessListener {
            override fun success() {
                Log.d(tag, "success connect wifi: ")
                navController.navigate("main")
            }

            override fun failed(errorCode: ConnectionErrorCode) {
                Log.d(tag, "connect failed: $errorCode")
            }
        }).start()


}



