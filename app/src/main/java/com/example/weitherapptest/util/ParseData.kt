package com.example.weitherapptest.util

import com.example.weitherapptest.viewmodel.WeatherViewModel
import com.google.gson.Gson

object ParseData {
    fun parseWeatherData(json: String): WeatherViewModel.CityWeather {
        val gson = Gson()
        val weatherResponse = gson.fromJson(json, WeatherViewModel.WeatherResponse::class.java)

        val cityName = weatherResponse.name
        val temperature = (weatherResponse.main.temp - 273.15f).toString()  // Conversion de Kelvin à Celsius
        val cloudCover = weatherResponse.clouds.all.toString()
        val humidity = weatherResponse.main.humidity.toString()

        return WeatherViewModel.CityWeather(cityName, temperature, cloudCover, humidity)
    }
}