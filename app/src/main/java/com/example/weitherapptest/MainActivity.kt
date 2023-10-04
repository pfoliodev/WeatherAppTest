package com.example.weitherapptest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
        while (progress < 1f) {
            delay(500)  // Mettez à jour tous les 0.5 secondes
            progress += 0.0167f  // Doublez le taux pour atteindre 1.0 en 60 secondes
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            if (progress >= 1f) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text(text = "Retour")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = currentWeather)

            LaunchedEffect(weatherViewModel.currentWeather) {
                weatherViewModel.currentWeather.collect { newWeather ->
                    currentWeather = newWeather
                }
            }
        }
    }
}