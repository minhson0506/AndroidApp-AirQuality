package com.example.airquality.services.sensors

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface APIDeviceService {
    @GET("getLatest")
    fun getLatest(): Call<SensorResponse>
}

object ApiDevice {
    fun apiInstance(): APIDeviceService {
        return Retrofit.Builder().baseUrl("http://192.168.1.1/api/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(APIDeviceService::class.java)
    }
}