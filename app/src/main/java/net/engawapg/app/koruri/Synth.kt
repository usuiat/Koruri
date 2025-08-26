package net.engawapg.app.koruri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.SquareWave

@Composable
internal fun SynthScreen(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var frequency by remember { mutableFloatStateOf(1000f) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Button(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (isPlaying) "Stop" else "Play")
            }
            Text("Frequency: ${"%.1f".format(frequency)} Hz")
            Slider(
                value = frequency,
                onValueChange = { frequency = it },
                valueRange = 200f..2000f,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }

    KoruriContent {
        if (isPlaying) {
            SquareWave { frequency }
        }
    }
}