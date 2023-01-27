package com.example.airquality.components

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.wifi.ScanResult
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.airquality.R
import com.example.airquality.services.sensors.Api
import com.example.airquality.services.sensors.SensorResponse
import com.example.airquality.ui.theme.*
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LandingPage(navController: NavController) {
    // get size of phone's screen
    val context = LocalContext.current
    val screenPixelDensity = context.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8

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
                    scanWifi(context)
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
                    connectDevice(context)
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

fun scanWifi(context: Context) {
    Log.d("airquality", "getScanResults: start")
    WifiUtils.withContext(context).scanWifi(::getScanResults).start()
}

fun getScanResults(result: List<ScanResult>) {
    val tag = "airquality"
    Log.d("airquality", "getScanResults: start 2")

    if (result.isEmpty()) {
        Log.d(tag, "getScanResults: empty")
        return
    }
    Log.d(tag, "getScanResults: " + result.size)

    Log.d(tag, "getScanResults: " + result )

    Log.d(tag, "getScanResults first item: " + result[0] )

    Log.d(tag, "getScanResults first item: " + result[0].toString().split(",")[0] )

}

fun connectDevice(context: Context) {
    val tag = "airquality"

    WifiUtils.withContext(context).enableWifi()
    WifiUtils.withContext(context).connectWith("ISDISD22", "sensor22").setTimeout(40000)
        .onConnectionResult(object : ConnectionSuccessListener {
            override fun success() {
                Log.d(tag, "success connect wifi: ")
                Api.apiInstance().getLatest().enqueue(object: Callback<SensorResponse> {
                    override fun onResponse(call: Call<SensorResponse>, response: Response<SensorResponse>) {
                        Log.d("airquality", "onResponse: " + response.body())
                    }

                    override fun onFailure(call: Call<SensorResponse>, t: Throwable) {
                        Log.d("airquality", "onFailure: " + t.message)
                    }
                })
            }

            override fun failed(errorCode: ConnectionErrorCode) {
                Log.d(tag, "connect failed: $errorCode")
            }
        }).start()


}



