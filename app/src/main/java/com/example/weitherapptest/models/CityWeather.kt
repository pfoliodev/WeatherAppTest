package com.example.weitherapptest.models

data class CityWeather(
    val cityName: String,
    val temperature: String,
    val cloudCover: String,
    val humidity: String? = null
)
