package com.example.airquality.services.sensors

import com.google.gson.annotations.SerializedName

data class SensorResponse(
    val alt: Double?,
    val co2: Double?,
    val deviceId: String?,
    val deviceName: String?,
    val hum: Double?,
    val lux: Double?,
    val noise: Double?,
    val pm1: Double?,
    val pm10: Double?,
    @SerializedName("pm2_5")
    val pm2: Double?,
    val pm4: Double?,
    val pres: Double?,
    val temp: Double?,
    val time: String?
)