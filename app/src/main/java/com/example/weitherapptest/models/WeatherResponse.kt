package com.example.weitherapptest.models

data class WeatherResponse(
    val name: String,
    val main: MainData,
    val clouds: CloudsData
)

data class CloudsData(
    val all: Int
)