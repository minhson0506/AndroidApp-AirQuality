package com.example.airquality.services

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.airquality.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

@SuppressLint("MissingPermission", "SuspiciousIndentation")
@Composable
fun GetLocation(activity: Activity, model: DataViewModel) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fusedLocationClient.lastLocation.addOnSuccessListener {
        Log.d(
            MainActivity.tag,
            "last location latitude: ${it?.latitude} and longitude: ${it?.longitude}"
        )
    }

    // callback function
    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {
                model.lat.postValue(location.latitude)
                model.lon.postValue(location.longitude)
            }
        }
    }

    // start to get location
    val locationRequest =
        LocationRequest.create().setInterval(1000).setPriority(PRIORITY_HIGH_ACCURACY)
    fusedLocationClient.requestLocationUpdates(locationRequest,
        locationCallback,
        Looper.getMainLooper())

}
