package com.example.cpen321application.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

private const val CLICKS_REQUIRED = 37
private const val INITIAL_CLICKS = 50
private const val IMAGE_1_URL = "https://cataas.com/cat/meme/says/you%20bonked%20me"
private const val IMAGE_2_URL = "https://cataas.com/cat/menacing/says/full%20marks%20as%20compensation"
private const val DELAY_BETWEEN_IMAGES_MS = 6000L

private enum class SurpriseStage { CLICKING, IMAGE_1, WAITING, IMAGE_2 }

@Composable
fun NoJobScreen(modifier: Modifier = Modifier) {
    var stage by remember { mutableStateOf(SurpriseStage.CLICKING) }
    var remainingClicks by remember { mutableStateOf(INITIAL_CLICKS) }

    LaunchedEffect(stage) {
        if (stage == SurpriseStage.IMAGE_1) {
            delay(DELAY_BETWEEN_IMAGES_MS)
            stage = SurpriseStage.IMAGE_2
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (stage) {
            SurpriseStage.CLICKING -> {
                Text(
                    text = "Keep clicking...Surprise at 0!!",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = "$remainingClicks",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                Button(
                    onClick = {
                        remainingClicks--
                        if (remainingClicks <= CLICKS_REQUIRED) {
                            stage = SurpriseStage.IMAGE_1
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Click me")
                }
            }

            SurpriseStage.IMAGE_1 -> {
                AsyncImage(
                    model = IMAGE_1_URL,
                    contentDescription = "You bonked me",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SurpriseStage.WAITING -> {
                CircularProgressIndicator()
            }

            SurpriseStage.IMAGE_2 -> {
                AsyncImage(
                    model = IMAGE_2_URL,
                    contentDescription = "Full marks as compensation",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}