package com.example.weitherapptest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weitherapptest.models.CityWeather
import com.example.weitherapptest.viewmodel.WeatherViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    var currentScreen by remember { mutableStateOf("accueil") }
    val weatherViewModel = viewModel<WeatherViewModel>()

    when (currentScreen) {
        "accueil" -> AccueilScreen { currentScreen = "jauge" }
        "jauge" -> JaugeScreen(weatherViewModel) { currentScreen = "accueil" }
    }
}

@Composable
fun AccueilScreen(onNavigate: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Bienvenue à l'accueil!")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigate) {
                Text(text = "Aller à la jauge")
            }
        }
    }
}

@Composable
fun JaugeScreen(weatherViewModel: WeatherViewModel, onBack: () -> Unit) {
    var currentWeather by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }
    var currentMessageIndex by remember { mutableIntStateOf(0) }
    var showCityWeatherTable by remember { mutableStateOf(false) }

    val messages = listOf(
        "Nous téléchargeons les données…",
        "C’est presque fini…",
        "Plus que quelques secondes avant d’avoir le résultat…"
    )

    LaunchedEffect(key1 = true) {
        while (true) {
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
            delay(6000)
        }
    }

    LaunchedEffect(key1 = progress) {
        val totalTimeInSeconds = 60
        val updateInterval = 1000L

        val numUpdates = (totalTimeInSeconds * 1000) / updateInterval
        val progressIncrement = 1.0f / numUpdates

        while (progress < 1f) {
            delay(updateInterval)
            progress += progressIncrement
        }

        showCityWeatherTable = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(progress < 1f) {
        Text(text = messages[currentMessageIndex])

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "${(progress * 100).toInt()}%")

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = currentWeather)
        }else{
            Button(
                onClick = {
                    progress = 0f
                    currentMessageIndex = 0
                    currentWeather = ""

                    weatherViewModel.resetCityWeatherData()
                    weatherViewModel.startWeatherFetching()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Recommencer")
            }
        }
        if (showCityWeatherTable) CityWeatherTable(weatherViewModel)

    }
}

@Composable
fun CityWeatherTable(weatherViewModel: WeatherViewModel) {
    var cityWeatherList by remember { mutableStateOf(emptyList<CityWeather>()) }

    LaunchedEffect(weatherViewModel.cityWeatherList) {
        weatherViewModel.cityWeatherList.collect { newCityWeatherList ->
            cityWeatherList = newCityWeatherList
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Résultats par ville", fontWeight = FontWeight.Bold)

        cityWeatherList.forEach { cityWeather ->
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = cityWeather.cityName)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Température: ${cityWeather.temperature}°C")
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    val cloudIconResourceId = getCloudIconResourceId(cityWeather.cloudCover)

                    Image(
                        painter = painterResource(id = cloudIconResourceId),
                        contentDescription = "Cloud Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun getCloudIconResourceId(cloudCover: String): Int {
    val cloudCoverPercentage = cloudCover.replace("%", "").toIntOrNull() ?: 0

    return when {
        cloudCoverPercentage > 50 -> R.drawable.ic_cloud
        cloudCoverPercentage > 25 -> R.drawable.ic_partly_cloudy
        else -> R.drawable.ic_sunny
    }
}
