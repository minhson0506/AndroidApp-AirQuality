package com.example.airquality

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.core.app.ActivityCompat
import com.example.airquality.ui.theme.AirQualityTheme


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
    getWifi(context = context)
}

fun disconnectWifi() {

}

fun getWifi(context: Context) {
    val tag = "airquality"

//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        Log.d("TAG", "getWifi: start ")
//        val mWifiManager =
//            (context.getSystemService(Context.WIFI_SERVICE) as WifiManager)!!
//        val info = mWifiManager.scanResults
//        if (info != null)
//        for ( elelment in info)
//            Log.d("TAG", "getWifi: " + elelment.SSID)
//        else
//            Log.d("TAG", "getWifi: no wifi")
//    } else {
//        Log.d("TAG", "getWifi: android <= 10")
//
//    }

    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager


    fun connectWifi(context: Context, ssid: String?, password: String?) {
        val mConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkSpecifier: NetworkSpecifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiNetworkSpecifier.Builder()
                .setSsid(ssid!!)
                .setWpa2Passphrase(password!!)
                .setIsHiddenSsid(true) //specify if the network does not broadcast itself and OS must perform a forced scan in order to connect
                .build()
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(networkSpecifier)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        mConnectivityManager.requestNetwork(networkRequest, mNetworkCallback)
    }
    fun scanSuccess() {
        val results = if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        } else {
            wifiManager.scanResults
        }
        Log.d("TAG", "scanSuccess: ")
        if (results != null) {
            for (wifiNetwork in results) {
                var split = wifiNetwork.toString().split(",")
                Log.d("TAG", "scanSuccess: " + wifiNetwork.BSSID + split[0].split(":")[1].trim())
                // connect to wifi
                if (split[0].split(":")[1].trim() == "M&J") {
                    Log.d("TAG", "scanSuccess: start to try connect wifi")

//                    val networkSpecifier: NetworkSpecifier? =
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            WifiNetworkSpecifier.Builder()
//                                .setSsid(split[0].split(":")[1].trim())
//                                .setWpa2Passphrase("12ba45678")
//                                .setIsHiddenSsid(true) //specify if the network does not broadcast itself and OS must perform a forced scan in order to connect
//                                .build()
//                        } else {
////                        TODO("VERSION.SDK_INT < Q")
//                            null
//                        }
//                    val mNetworkCallback: NetworkCallback = object : NetworkCallback() {
//                        override fun onAvailable(network: Network) {
//                            super.onAvailable(network)
//                            Log.d(tag,
//                                "Wifi connected")
//                        }
//                    }
//                    val networkRequest = NetworkRequest.Builder()
//                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//                        .setNetworkSpecifier(networkSpecifier)
//                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                        .build()
//                    context.getSystemService(Context.CONNECTIVITY_SERVICE).requestNetwork(networkRequest, mNetworkCallback)

//                    val suggestion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        WifiNetworkSuggestion.Builder()
//                            .setSsid(split[0].split(":")[1].trim())
//                            .setWpa2Passphrase("12ba45678")
//                            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
//                            .build()
//                    } else {
//                        null
//                    };
//
//                    val suggestionsList = listOf(suggestion);
//                    if (suggestion != null) {
//                        val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            wifiManager.addNetworkSuggestions(suggestionsList)
//                        } else {
//                            null
//                        };
//                        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//                            // do error handling here
//                        }
//
//// Optional (Wait for post connection broadcast to one of your suggestions)
//                        val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
//
//                        val broadcastReceiver = object : BroadcastReceiver() {
//                            override fun onReceive(context: Context, intent: Intent) {
//                                if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
//                                    return;
//                                }
//                                // do post connect processing here
//                                Log.d("TAG", "onReceive: connected")
//                            }
//                        };
//                        context.registerReceiver(broadcastReceiver, intentFilter);
//                    }

//                    var  connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                    if (connectivityManager != null)
//                    val networkCallback = object : ConnectivityManager.NetworkCallback() {
//                        override fun onAvailable(network: Network) {
//                            super.onAvailable(network)
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                // To make sure that requests don't go over mobile data
//                                connectivityManager.bindProcessToNetwork(network)
//                            } else {
//                                connectivityManager.setProcessDefaultNetwork(network)
//                            }
//                        }
//
//                        override fun onLost(network: Network) {
//                            super.onLost(network)
//                            // This is to stop the looping request for OnePlus & Xiaomi models
//                            connectivityManager.bindProcessToNetwork(null)
//                            connectivityManager.unregisterNetworkCallback(networkCallback)
//                            // Here you can have a fallback option to show a 'Please connect manually' page with an Intent to the Wifi settings
//                        }
//                    }

                }
            }
        } else {
            Log.d("TAG", "scanSuccess: No wifi")
        }
//        ... use new scan results ...
    }

    fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        Log.d("TAG", "scanFailure: ")
        val results = wifiManager.scanResults
//        ... potentially use older scan results ...
    }

    val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    context.registerReceiver(wifiScanReceiver, intentFilter)

    // start to scan wifi network
    val success = wifiManager.startScan()
    if (!success) {
        // scan failure handling
        scanFailure()
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AirQualityTheme {
        Greeting("Android")
    }
}

