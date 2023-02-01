package com.example.airquality.components

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.airquality.R
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.sensors.SensorResponse
import com.example.airquality.services.weather.ApiWeather
import com.example.airquality.services.weather.WeatherResponse
import com.example.airquality.ui.theme.Black
import com.example.airquality.ui.theme.LightBlue
import com.example.airquality.ui.theme.bold
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiRemove.RemoveErrorCode
import com.thanosfisherman.wifiutils.wifiRemove.RemoveSuccessListener
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.math.log


@Composable
fun LandingPage(model: DataViewModel, navController: NavController) {
    // get size of phone's screen
    val context = LocalContext.current
    val screenPixelDensity = context.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8

    val wifiNetworks: List<String>? by model.wifiNetworks.observeAsState(null)
    val weather: WeatherResponse? by model.weather.observeAsState(null)
    val image: ImagePainter? by model.image.observeAsState(null)


    var displayNetwork by remember { mutableStateOf<Boolean>(false) }
    var tryConnect by remember { mutableStateOf<Boolean>(false) }
    var wifiName by remember { mutableStateOf<String?>(null) }

    Log.d("airquality", "LandingPage: $wifiNetworks")
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE).toString()
    Log.d("airquality", "WifiManager: $wifiManager")

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
                        .height((dpValue * 0.3).dp)
                        .verticalScroll(rememberScrollState())
                ) {
//                    wifiNetworks?.forEach { item -> Text(text = item, modifier = Modifier
//                        .height(20.dp)
//                        .align(CenterHorizontally)
//                        .clickable {
//                            tryConnect = true
//                            wifiName = item
//                        }) }
                    wifiNetworks?.forEach {
//                        disconnectWifi(context = context, it)
                        Text(text = it, modifier = Modifier
                            .height(20.dp)
                            .align(CenterHorizontally)
                            .clickable {
                                //tryConnect = true
                                wifiName = it
                            })
                    }
                }
            }
        }

        if (wifiName != null) {
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
                    //model.wifiNetworks.postValue(null)
                    navController.navigate("main")

                    //cai nay dung
//                    scanWifi(model, context)
//                    Thread.sleep(100)
                    //den day

                    //displayNetwork = true
//                    wifiNetworks?.let { disconnectWifi(context, it) }
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

fun disconnectWifi(context: Context, wifiNetwork: String) {
/*    WifiUtils.withContext(context)
        .disconnect(object : DisconnectionSuccessListener {
            override fun success() {
                Log.d("airquality", "disconnect success: ")
            }

            override fun failed(errorCode: DisconnectionErrorCode) {
                Log.d("airquality", "disconnect fail: ")

            }
        })*/
    WifiUtils.withContext(context).remove(wifiNetwork, object : RemoveSuccessListener {
        override fun success() {
            Log.d("airquality", "disconnect success: ")
        }

        override fun failed(errorCode: RemoveErrorCode) {
            Log.d("airquality", "disconnect fail: ")
        }
    })

}

fun scanWifi(model: DataViewModel, context: Context) {
/*    WifiUtils.withContext(context).disableWifi()
    Thread.sleep(100)

    WifiUtils.withContext(context).enableWifi()
    Thread.sleep(100)*/

    Log.d("airquality", "getScanResults: start")
    Log.d("airquality", "scanWifi: wifiNetworks ${model.wifiNetworks}")
    WifiUtils.withContext(context).scanWifi { scanResults ->
        if (scanResults.isEmpty()) Log.d("airquality", "getScanResults: scan result null")
        val listWifi = scanResults.map { item ->
            item.toString().split(",")[0].split(":")[1].trim()
        }
            //.filter { item -> item.startsWith("ISD") || item.startsWith("\"ISD") }
            .map { item ->
                if (item.startsWith("\"ISD")) item.split("\"")[1] else item
            }
        if (!listWifi.isNullOrEmpty()) {
            model.wifiNetworks.postValue(listWifi)
            scanResults.map {
                Log.d("airquality", "getScanResults: $it")

            }
        }
    }.start()
}

@Composable
fun connectDevice(ssid: String, context: Context, navController: NavController) {
    val tag = "airquality"
/*    disconnectWifi(context, ssid)
    Thread.sleep(100)*/
//    WifiUtils.withContext(context).disableWifi()
    //  Thread.sleep(100)

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

@Composable
fun GetWeather(model: DataViewModel) {
    val lat: Double? by model.lat.observeAsState(null)
    val lon: Double? by model.lon.observeAsState(null)
    val weather: WeatherResponse? by model.weather.observeAsState(null)

    ApiWeather.apiInstance().getWeather(location = lat.toString() + "," + lon)
        .enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("airquality", "onResponse: name " + (response.body()?.current?.condition?.icon))
                    model.weather.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("airquality", "onFailure: " + t.message)
            }
        })
    if (weather != null) {
        model.image.postValue(rememberImagePainter(data = "https:" + weather!!.current.condition.icon))
    }
    Log.d("airquality", "GetWeather: Long: $lon lat: $lat")
}


