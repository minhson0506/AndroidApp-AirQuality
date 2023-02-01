package com.example.airquality.services.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sensormodel")
data class SensorModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val alt: Double?,
    val co2: Double?,
    val deviceId: String?,
    val deviceName: String?,
    val hum: Double?,
    val lux: Double?,
    val noise: Double?,
    val pm1: Double?,
    val pm10: Double?,
    val pm2: Double?,
    val pm4: Double?,
    val pres: Double?,
    val temp: Double?,
    val time: String?,
    val cloud: Boolean = false
)