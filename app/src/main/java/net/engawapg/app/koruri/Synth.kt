package net.engawapg.app.koruri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Envelope
import net.engawapg.lib.koruri.processor.SquareWave

@Composable
internal fun SynthScreen(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var frequency by remember { mutableFloatStateOf(100f) }

    var gate by remember { mutableStateOf(false) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                gate = !gate
                delay(250)
            }
        } else {
            gate = false
        }
    }

    // ADSR parameters
    var attack by remember { mutableFloatStateOf(0.1f) }
    var decay by remember { mutableFloatStateOf(0.2f) }
    var sustain by remember { mutableFloatStateOf(0.7f) }
    var release by remember { mutableFloatStateOf(0.3f) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Play/Stop button
            Button(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (isPlaying) "Stop" else "Play")
            }

            // Frequency control
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Frequency",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("${"%.1f".format(frequency)} Hz")
                    Slider(
                        value = frequency,
                        onValueChange = { frequency = it },
                        valueRange = 20f..300f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ADSR controls
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ADSR Envelope",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Attack
                    Text("Attack: ${"%.2f".format(attack)}s")
                    Slider(
                        value = attack,
                        onValueChange = { attack = it },
                        valueRange = 0.01f..2.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Decay
                    Text("Decay: ${"%.2f".format(decay)}s")
                    Slider(
                        value = decay,
                        onValueChange = { decay = it },
                        valueRange = 0.01f..2.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Sustain
                    Text("Sustain: ${"%.2f".format(sustain)}")
                    Slider(
                        value = sustain,
                        onValueChange = { sustain = it },
                        valueRange = 0.0f..1.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Release
                    Text("Release: ${"%.2f".format(release)}s")
                    Slider(
                        value = release,
                        onValueChange = { release = it },
                        valueRange = 0.01f..3.0f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    KoruriContent {
        Envelope(
            attack = { attack },
            decay = { decay },
            sustain = { sustain },
            release = { release },
            gate = { gate }
        ) {
            SquareWave { frequency }
        }
    }
}