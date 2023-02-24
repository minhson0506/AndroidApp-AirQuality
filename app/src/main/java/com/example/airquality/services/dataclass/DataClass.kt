package com.example.airquality.services.dataclass

data class ValueDisplay(
    val id: Int,
    val name: String,
    val image: Int,
    val data: Double?,
    val unit: String,
    val desc: String,
    val outside: String,
)

data class SliderData(
    val id: Int,
    val name: String,
    val step: Float,
    val min: Float,
    val max: Float
)

data class DataIndicator(val value: Double, val time: String)