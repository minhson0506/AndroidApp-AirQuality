package com.example.airquality.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

@SuppressLint("MissingPermission")
@Composable
fun GetLocation(context: Context, activity: Activity, model: DataViewModel) {
    val tag = "venues"

    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

        fusedLocationClient.lastLocation.addOnSuccessListener {
            Log.d(
                tag,
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
