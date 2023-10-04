package com.example.weitherapptest.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weitherapptest.models.CityWeather
import com.example.weitherapptest.models.WeatherResponse
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

    private val _cityWeatherList = MutableStateFlow(emptyList<CityWeather>())
    val cityWeatherList: StateFlow<List<CityWeather>> = _cityWeatherList


    init {
        viewModelScope.launch {
            fetchWeatherSequentially()
        }
    }

    fun startWeatherFetching() {
        viewModelScope.launch {
            fetchWeatherSequentially()
        }
    }

    suspend fun fetchWeatherSequentially() {
        viewModelScope.launch {
            val newCityWeatherList = mutableListOf<CityWeather>()
            cities.forEach { city ->
                val weather = fetchWeatherForCity(city)
                val cityName = weather?.name ?: city
                val temperature = "${weather?.main?.temp ?: "Non disponible"}"
                val cloudCover = "${weather?.clouds?.all ?: "Non disponible"}%"
                newCityWeatherList.add(CityWeather(cityName, temperature, cloudCover))
                _currentWeather.value = "Météo pour $city : ${weather?.main?.temp ?: "Non disponible"}°C"
                delay(10000)
            }
            _cityWeatherList.value = newCityWeatherList
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

     fun resetCityWeatherData() {
        viewModelScope.launch {
            _cityWeatherList.value = emptyList()
        }
    }
}



