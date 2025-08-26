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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Chain
import net.engawapg.lib.koruri.processor.Envelope
import net.engawapg.lib.koruri.processor.LowPassFilter
import net.engawapg.lib.koruri.processor.SquareWave

@Composable
internal fun SynthScreen(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    val baseFrequency = 130.81f // C3の音に固定
    var bpm by remember { mutableFloatStateOf(120f) }
    var pulseWidth by remember { mutableFloatStateOf(0f) }

    var gate by remember { mutableStateOf(false) }
    var frequency by remember { mutableFloatStateOf(130.81f) }

    // アルペジオパターン（ルート、3度、5度、オクターブ）
    val arpeggioPattern = listOf(1f, 1.25f, 1.5f, 2f)
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPlaying, bpm) {
        if (isPlaying) {
            if (bpm == 0f) {
                // BPM=0なら最初の音を常時再生
                frequency = baseFrequency * arpeggioPattern[0]
                gate = true
            } else {
                // アルペジオパターンをループ
                while (true) {
                    val beatDuration = (60000 / bpm).toLong() // BPMからミリ秒計算
                    val gateDuration = (beatDuration * 0.8).toLong() // 拍の80%�����ゲートオン

                    // 現在のステ��プに対応する周波数を設定
                    frequency = baseFrequency * arpeggioPattern[currentStep]
                    gate = true
                    delay(gateDuration)
                    gate = false
                    delay(beatDuration - gateDuration)

                    // 次のステップに進む
                    currentStep = (currentStep + 1) % arpeggioPattern.size
                }
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

    // LPF parameters
    var cutoff by remember { mutableFloatStateOf(1000f) }
    var resonance by remember { mutableFloatStateOf(1f) }

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


            // BPM control
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "BPM (Beats Per Minute)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("${bpm.toInt()} BPM")
                    Slider(
                        value = bpm,
                        onValueChange = { bpm = it },
                        valueRange = 0f..240f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Pulse Width control
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Pulse Width",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("${"%.2f".format(pulseWidth)}")
                    Slider(
                        value = pulseWidth,
                        onValueChange = { pulseWidth = it },
                        valueRange = -1f..1f,
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

            // LPF controls
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Low Pass Filter",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Cutoff
                    Text("Cutoff: ${"%.1f".format(cutoff)} Hz")
                    Slider(
                        value = cutoff,
                        onValueChange = { cutoff = it },
                        valueRange = 20f..20000f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Resonance
                    Text("Resonance: ${"%.2f".format(resonance)}")
                    Slider(
                        value = resonance,
                        onValueChange = { resonance = it },
                        valueRange = 0.1f..10f,
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
            Chain {
                SquareWave(
                    frequency = { frequency },
                    pulseWidth = { pulseWidth }
                )
                LowPassFilter(
                    cutoff = { cutoff },
                    resonance = { resonance }
                )
            }
        }
    }
}