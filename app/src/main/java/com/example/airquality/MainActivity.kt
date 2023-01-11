package com.example.airquality

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.net.wifi.ScanResult
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.airquality.ui.theme.AirQualityTheme
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Executors.*


class MainActivity : ComponentActivity() {
    val tag = "airquality"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission(this)
        setContent {
            AirQualityTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

fun checkPermission(activity: Activity) {
    val tag = "airquality"
    if (
        (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
        (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    ) {
        Log.d(tag, "No permission")
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ), 1
        )
        while ((activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
            (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        ) {
        }
    }
    Log.i(tag, "permissions ok")
}

@Composable
fun Greeting(name: String) {
    val context = LocalContext.current
    var wifiName by remember { mutableStateOf("") }

    Column() {
        Button(onClick = { connectWifi(context) }) {
            Text(text = "Start Connect Wifi")
        }
        Button(onClick = { disconnectWifi() }) {
            Text(text = "Stop Connect Wifi")
        }

        Text(text = "Wifi is connect is $wifiName")
    }

//    Text(text = "Hello $name!"
}

fun connectWifi(context: Context) {
//    getWifi(context = context)
    Log.d("airquality", "connectWifi: start to try ")
    connect(context, "ssid", "password")

}


fun disconnectWifi() {

}

fun connect(context: Context, ssid:String, password: String) {
    val tag = "airquality"

    WifiUtils.withContext(context).enableWifi()
    WifiUtils.withContext(context).connectWith(ssid, password).setTimeout(5000)
        .onConnectionResult(object : ConnectionSuccessListener {
            override fun success() {
                Log.d(tag, "success connect wifi: ")
                checkInternetConnection()
            }

            override fun failed(errorCode: ConnectionErrorCode) {
                Log.d(tag, "connect failed: $errorCode")
            }
        }).start()

}

private fun checkInternetConnection() {
    val tag = "airquality"
    Log.d(tag, "checkInternetConnection: ")
    val executorService: ExecutorService = newFixedThreadPool(4)
    executorService.execute {
        try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockaddr, timeoutMs)
            sock.close()
            Log.d(tag, "checkInternetConnection: success")

        } catch (e: Exception) {
            Log.d(tag, "checkInternetConnection: fail")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AirQualityTheme {
        Greeting("Android")
    }
}

