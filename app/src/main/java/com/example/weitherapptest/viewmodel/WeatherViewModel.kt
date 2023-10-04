package com.example.weitherapptest.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


class WeatherViewModel: ViewModel() {
    private val cities = listOf("Rennes", "Paris", "Nantes", "Bordeaux", "Lyon")
    private val _currentWeather = MutableStateFlow("")

    val currentWeather: StateFlow<String> = _currentWeather

    init {
        viewModelScope.launch {
            fetchWeatherSequentially()
        }
    }

    private suspend fun fetchWeatherSequentially() {
        viewModelScope.launch {
            cities.forEach { city ->
                val weather = fetchWeatherForCity(city)

                _currentWeather.value = "Météo pour $city : ${weather?.main?.temp ?: "Non disponible"}°C"
                delay(10000)
            }
        }
    }

    private suspend fun fetchWeatherForCity(city: String): WeatherResponse? {
        val apiKey = "eaf51f32040429711b384cd3f3285e59"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"

        return try {
            val response = withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val gson = Gson()
                gson.fromJson(responseBody, WeatherResponse::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Error fetching weather for $city", e)
            null
        }
    }

    data class WeatherResponse(
        val name: String,  // Nom de la ville
        val main: MainData,
        val clouds: CloudsData
    )

    data class MainData(
        val temp: Float,  // Température en Kelvin
        val humidity: Int  // Humidité en pourcentage
    )

    data class CloudsData(
        val all: Int  // Couverture nuageuse en pourcentage
    )

    data class CityWeather(
        val cityName: String,         // Nom de la ville
        val temperature: String,      // Température en Celsius
        val cloudCover: String,       // Couverture nuageuse en pourcentage
        val humidity: String? = null  // Humidité en pourcentage (optionnel)
    )
}