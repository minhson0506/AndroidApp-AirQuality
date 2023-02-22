package com.example.airquality.services

import com.example.airquality.R

// pm10, pm25, pm1, pm4, co2, humi, light, noise, pressure, temp
val minValueInit = listOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 40.0f, 0.0f, 0.0f, 950f, 20.0f)
val maxValueInit = listOf(54.0f, 12.0f, 54.0f, 54.0f, 5000.0f, 70.0f, 300.0f, 35.0f, 1050f, 30.0f)
val listItems = arrayOf(
    "PM10",
    "Pm2.5",
    "Pm1",
    "Pm4",
    "CO2",
    "Humidity",
    "Light",
    "Noise",
    "Pressure",
    "Temperature"
)
val itemImages = arrayOf(
    R.drawable.wind,
    R.drawable.wind,
    R.drawable.wind,
    R.drawable.wind,
    R.drawable.co2,
    R.drawable.humidity,
    R.drawable.light,
    R.drawable.sound,
    R.drawable.pressure,
    R.drawable.temp
)

