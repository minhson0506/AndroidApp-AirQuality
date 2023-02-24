package com.example.airquality.services.weather

import com.example.airquality.BuildConfig
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIWeatherService {
    @GET("current.json")
    fun getWeather(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("q") location: String,
        @Query("days") day: Int = 10,
        @Query("aqi") aqi: String = "yes",
    ): Call<WeatherResponse>
}

object ApiWeather {
    fun apiInstance(): APIWeatherService {
        return Retrofit.Builder().baseUrl("https://api.weatherapi.com/v1/").addConverterFactory(
            GsonConverterFactory.create()).build().create(APIWeatherService::class.java)
    }
}