package com.example.airquality.services.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(val location: LocationData, val current: Current) {
}

data class LocationData(
    val name: String,
    val region: String,
    val lat: Double,
    val lon: Double,
    @SerializedName("tz_id")
    val id: String,
    @SerializedName("localtime_epoch")
    val timeInt: Long,
    @SerializedName("localtime")
    val localTime: String
)

data class Current(
    @SerializedName("last_updated_epoch")
    val timeInt: Long,
    @SerializedName("last_updated")
    val localTime: String,
    @SerializedName("temp_c")
    val temp: Double,
    @SerializedName("temp_f")
    val tempf: Double,
    val condition: ConditionWeather,
    @SerializedName("pressure_mb")
    val pressure: Double,
    val humidity: Int,
    @SerializedName("air_quality")
    val airQuality: AirQuality
)

data class ConditionWeather(
    val text: String,
    val icon: String,
    val code: Int
)

data class AirQuality(
    val co: Double,
    val pm2_5: Double,
    val pm10: Double
)