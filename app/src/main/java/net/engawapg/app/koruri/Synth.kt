/*
 * Copyright 2025 usuiat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.engawapg.app.koruri

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Chain
import net.engawapg.lib.koruri.processor.LowPassFilter
import net.engawapg.lib.koruri.processor.SquareWave
import net.engawapg.lib.koruri.processor.VolumeEnvelope
import net.engawapg.lib.koruri.processor.produceLfo

@Composable
internal fun SynthScreen(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    val baseFrequency = 65.41f /* C2 */
    val bpm = 120f

    var gate by remember { mutableStateOf(false) }
    var frequency by remember { mutableFloatStateOf(65.41f) }

    val arpeggioPattern = listOf(
        1.0f,    // C
        1.5f,    // G
        1.681f,  // Am
        1.26f,   // Em
        1.334f,  // F
        1.0f,    // C
        1.334f,  // F
        1.5f     // G
    )
    var currentStep by remember { mutableIntStateOf(0) }

    // ADSR parameters
    var attack by remember { mutableFloatStateOf(0.01f) } // 非常に短いアタック
    var decay by remember { mutableFloatStateOf(0.15f) }  // 短いディケイ
    var sustain by remember { mutableFloatStateOf(0.4f) } // 低めのサスティン
    var release by remember { mutableFloatStateOf(0.01f) } // 短いリリース

    // LPF parameters
    var cutoff by remember { mutableFloatStateOf(20000f) }  // 少し高めのカットオフ
    var resonance by remember { mutableFloatStateOf(0.1f) }  // 高めのレゾナンス

    // Pulse Width Modulation parameters
    var pulseWidth by remember { mutableFloatStateOf(0f) }      // ベースのPulse Width
    var pwmAmount by remember { mutableFloatStateOf(0.0f) }         // モジュレーション量

    // LFO frequency parameter
    var lfoFreq by remember { mutableFloatStateOf(1.0f) } // デフォルト1Hz

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            currentStep = 0
            // アルペジオパターンをループ
            while (true) {
                val beatDuration = (60000 / bpm).toLong() // BPMからミリ秒計算
                val gateDuration = (beatDuration * 0.6).toLong() // 拍の60%をゲートオン（よりパンチのある音）

                // 現在のステップに対応する周波数を設定
                frequency = baseFrequency * arpeggioPattern[currentStep]
                gate = true
                delay(gateDuration)
                gate = false
                delay(beatDuration - gateDuration)

                // 次のステップに進む
                currentStep = (currentStep + 1) % arpeggioPattern.size
            }
        } else {
            gate = false
        }
    }

    KoruriContent {
        val lfo by produceLfo(frequency = lfoFreq, gate = gate)
        Chain {
            SquareWave(
                frequency = { frequency },
                pulseWidth = { pulseWidth + lfo * pwmAmount * 0.5f }
            )
            VolumeEnvelope(
                attack = { attack },
                decay = { decay },
                sustain = { sustain },
                release = { release },
                gate = { gate }
            )
            LowPassFilter(
                cutoff = { cutoff },
                resonance = { resonance }
            )
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row {
                // Large touch area for playing note
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    frequency = baseFrequency
                                    gate = true
                                    try {
                                        awaitRelease()
                                    } finally {
                                        gate = false
                                    }
                                }
                            )
                        }
                )

                // Play/Stop button
                Button(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(if (isPlaying) "Stop" else "Play")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

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

                // Pulse Width Modulation controls
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Pulse Width Modulation",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Base Pulse Width
                        Text("Base Pulse Width: ${"%.2f".format(pulseWidth)}")
                        Slider(
                            value = pulseWidth,
                            onValueChange = { pulseWidth = it },
                            valueRange = -1f..1f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // PWM Amount
                        Text("PWM Amount: ${"%.2f".format(pwmAmount)}")
                        Slider(
                            value = pwmAmount,
                            onValueChange = { pwmAmount = it },
                            valueRange = 0f..1f,
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

                // LFO frequency control
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "LFO Frequency",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text("LFO: ${"%.2f".format(lfoFreq)} Hz")
                        Slider(
                            value = lfoFreq,
                            onValueChange = { lfoFreq = it },
                            valueRange = 0.1f..10f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}