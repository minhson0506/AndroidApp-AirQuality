package com.example.airquality.services

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.airquality.MainActivity
import com.example.airquality.services.room.SensorModel
import com.example.airquality.services.sensors.ApiDevice
import com.example.airquality.services.sensors.SensorResponse
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiRemove.RemoveErrorCode
import com.thanosfisherman.wifiutils.wifiRemove.RemoveSuccessListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool

fun disconnectWifi(context: Context, wifiNetwork: String) {
    WifiUtils.withContext(context)
        .disconnect(object : DisconnectionSuccessListener {
            override fun success() {
                Log.d(MainActivity.tag, "disconnect success: ")
            }

            override fun failed(errorCode: DisconnectionErrorCode) {
                Log.d(MainActivity.tag, "disconnect fail: ")

            }
        })
}

fun removeWifi(context: Context, wifiNetwork: String) {
    WifiUtils.withContext(context).remove(wifiNetwork, object : RemoveSuccessListener {
        override fun success() {
            Log.d(MainActivity.tag, "disconnect success: ")
        }

        override fun failed(errorCode: RemoveErrorCode) {
            Log.d(MainActivity.tag, "disconnect fail: ")
        }
    })
}

fun scanWifi(model: DataViewModel, context: Context) {
    Log.d(MainActivity.tag, "getScanResults: start")
    Log.d(MainActivity.tag, "scanWifi: wifiNetworks ${model.wifiNetworks}")
    WifiUtils.withContext(context).enableWifi()
    WifiUtils.withContext(context).scanWifi { scanResults ->
        val listWifi = scanResults.map { item ->
            item.toString().split(",")[0].split(":")[1].trim()
        }
            .filter { item -> item.startsWith("ISD") || item.startsWith("\"ISD") }
            .map { item ->
                if (item.startsWith("\"ISD")) item.split("\"")[1] else item
            }
        if (!listWifi.isNullOrEmpty()) {
            model.wifiNetworks.postValue(listWifi)
        }
    }.start()
}

@Composable
fun connectDevice(
    ssid: String,
    context: Context,
    navController: NavController,
    model: DataViewModel,
) {
    val data: List<SensorModel>? by model.getAllData().observeAsState(null)

//    removeWifi(context, ssid)
//    Thread.sleep(100)

    val sensorData = data?.filter { it != null }
    WifiUtils.withContext(context).connectWith(ssid, "sensor22").setTimeout(40000)
        .onConnectionResult(object : ConnectionSuccessListener {
            override fun success() {
                Log.d(MainActivity.tag, "success connect wifi: ")
                model.isOnline.postValue(false)
                // get data from device and insert to Room
                ApiDevice.apiInstance().getAll().enqueue(object : Callback<List<SensorResponse>> {
                    override fun onResponse(
                        call: Call<List<SensorResponse>>,
                        response: Response<List<SensorResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.forEach {
                                if (sensorData == null) {
                                    model.insert(SensorModel(
                                        id = 0,
                                        alt = it.alt,
                                        co2 = it.co2,
                                        deviceId = it.deviceId,
                                        deviceName = it.deviceName,
                                        hum = it.hum,
                                        lux = it.lux,
                                        noise = it.noise,
                                        pm1 = it.pm1,
                                        pm10 = it.pm10,
                                        pm2 = it.pm2,
                                        pm4 = it.pm4,
                                        pres = it.pres,
                                        temp = it.temp,
                                        time = it.time,
                                    ))
                                } else if (!sensorData?.map { item -> item.time }?.contains(it.time)!!) {
                                    model.insert(SensorModel(
                                        id = 0,
                                        alt = it.alt,
                                        co2 = it.co2,
                                        deviceId = it.deviceId,
                                        deviceName = it.deviceName,
                                        hum = it.hum,
                                        lux = it.lux,
                                        noise = it.noise,
                                        pm1 = it.pm1,
                                        pm10 = it.pm10,
                                        pm2 = it.pm2,
                                        pm4 = it.pm4,
                                        pres = it.pres,
                                        temp = it.temp,
                                        time = it.time,
                                    ))
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<SensorResponse>>, t: Throwable) {
                        Log.d(MainActivity.tag, "onFailure: when get all data")
                    }
                })

                // navigate to Dashboard
                navController.navigate("main")
            }

            override fun failed(errorCode: ConnectionErrorCode) {
                Log.d(MainActivity.tag, "connect failed: $errorCode")
            }
        }).start()
}

fun checkInternetConnection() {
    Log.d(MainActivity.tag, "checkInternetConnection: ")
    val executorService: ExecutorService = newFixedThreadPool(4)
    executorService.execute {
        try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockaddr, timeoutMs)
            sock.close()
            Log.d(MainActivity.tag, "checkInternetConnection: success")
        } catch (e: Exception) {
            Log.d(MainActivity.tag, "checkInternetConnection: fail")
        }
    }
}
