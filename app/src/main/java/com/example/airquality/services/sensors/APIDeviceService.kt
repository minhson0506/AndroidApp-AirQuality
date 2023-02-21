package com.example.airquality.services.sensors

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT

interface APIDeviceService {
    @GET("getLatest")
    fun getLatest(): Call<SensorResponse>

    @GET("getAll")
    fun getAll(): Call<List<SensorResponse>>

    @GET("getLatest?count=10000")
    fun getNew(): Call<List<SensorResponse>>

    @Headers("Content-Type: application/json")
    @PUT("setName")
    fun changeName(@Body name: Name): Call<String>
}

data class Name(val name: String)

object ApiDevice {
    fun apiInstance(): APIDeviceService {
        return Retrofit.Builder().baseUrl("http://192.168.1.1/api/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(APIDeviceService::class.java)
    }
}