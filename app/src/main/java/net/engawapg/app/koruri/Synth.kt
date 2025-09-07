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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.engawapg.app.koruri.ui.theme.KoruriTheme
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Chain
import net.engawapg.lib.koruri.processor.LowPassFilter
import net.engawapg.lib.koruri.processor.Pitch
import net.engawapg.lib.koruri.processor.SquareWave
import net.engawapg.lib.koruri.processor.VolumeEnvelope
import net.engawapg.lib.koruri.processor.produceLfo

private data class SynthState(
    val isPlaying: Boolean = false,
    val gate: Boolean = false,
    val frequency: Float = Pitch.C4.frequency,
    // ADSR envelope parameters
    val attack: Float = 0.03f,
    val decay: Float = 0.20f,
    val sustain: Float = 0.50f,
    val release: Float = 0.30f,
    // LFO frequency
    val lfoFrequency: Float = 1.0f,
    // Pulse Width Modulation parameters
    val pulseWidth: Float = 0.0f,
    val pwmAmount: Float = 0.0f,
    // LPF parameters
    val lpfCutoff: Float = 3000f,
    val lpfResonance: Float = 0.1f,
)

private sealed interface SynthEvent {
    data class OnPlayingChange(val isPlaying: Boolean) : SynthEvent
    data class OnGateChange(val gate: Boolean) : SynthEvent
    data class OnFrequencyChange(val frequency: Float) : SynthEvent
    // ADSR envelope parameters
    data class OnAttackChange(val attack: Float) : SynthEvent
    data class OnDecayChange(val decay: Float) : SynthEvent
    data class OnSustainChange(val sustain: Float) : SynthEvent
    data class OnReleaseChange(val release: Float) : SynthEvent
    // LFO frequency
    data class OnLfoFrequencyChange(val lfoFrequency: Float) : SynthEvent
    // Pulse Width Modulation parameters
    data class OnPulseWidthChange(val pulseWidth: Float) : SynthEvent
    data class OnPwmAmountChange(val pwmAmount: Float) : SynthEvent
    // LPF parameters
    data class OnLpfCutoffChange(val cutoff: Float) : SynthEvent
    data class OnLpfResonanceChange(val resonance: Float) : SynthEvent
}

@Composable
internal fun SynthScreen(modifier: Modifier = Modifier) {
    var synthState by remember { mutableStateOf(SynthState()) }

    KoruriContent {
        ArpeggioLoop(
            pattern = listOf(
                Pitch.C4, Pitch.G4, Pitch.A4, Pitch.E4,
                Pitch.F4, Pitch.C4, Pitch.F4, Pitch.G4
            ),
            bpm = 120f,
            play = synthState.isPlaying
        ) { newPitch, newGate ->
            synthState = synthState.copy(
                gate = newGate,
                frequency = newPitch.frequency
            )
        }

        val lfo by produceLfo(frequency = synthState.lfoFrequency, gate = synthState.gate)

        Chain {
            SquareWave(
                frequency = { synthState.frequency },
                pulseWidth = { synthState.pulseWidth + lfo * synthState.pwmAmount * 0.5f }
            )
            VolumeEnvelope(
                attack = { synthState.attack },
                decay = { synthState.decay },
                sustain = { synthState.sustain },
                release = { synthState.release },
                gate = { synthState.gate }
            )
            LowPassFilter(
                cutoff = { synthState.lpfCutoff },
                resonance = { synthState.lpfResonance }
            )
        }
    }

    SynthUi(
        modifier = modifier,
        synthState = synthState,
        onEvent = { event ->
            synthState = when (event) {
                is SynthEvent.OnPlayingChange -> synthState.copy(isPlaying = event.isPlaying)
                is SynthEvent.OnGateChange -> synthState.copy(gate = event.gate)
                is SynthEvent.OnFrequencyChange -> synthState.copy(frequency = event.frequency)
                is SynthEvent.OnAttackChange -> synthState.copy(attack = event.attack)
                is SynthEvent.OnDecayChange -> synthState.copy(decay = event.decay)
                is SynthEvent.OnSustainChange -> synthState.copy(sustain = event.sustain)
                is SynthEvent.OnReleaseChange -> synthState.copy(release = event.release)
                is SynthEvent.OnLfoFrequencyChange -> synthState.copy(lfoFrequency = event.lfoFrequency)
                is SynthEvent.OnPulseWidthChange -> synthState.copy(pulseWidth = event.pulseWidth)
                is SynthEvent.OnPwmAmountChange -> synthState.copy(pwmAmount = event.pwmAmount)
                is SynthEvent.OnLpfCutoffChange -> synthState.copy(lpfCutoff = event.cutoff)
                is SynthEvent.OnLpfResonanceChange -> synthState.copy(lpfResonance = event.resonance)
            }
        }
    )
}

@Composable
private fun ArpeggioLoop(
    pattern: List<Pitch>,
    bpm: Float,
    play: Boolean,
    gateDurationRatio: Float = 0.6f,
    onStepChange: (pitch: Pitch, gate: Boolean) -> Unit,
) {
    LaunchedEffect(play) {
        if (play) {
            var currentStep = 0
            // アルペジオパターンをループ
            while (true) {
                val beatDuration = (60000 / bpm).toLong() // BPMからミリ秒計算
                val gateDuration = (beatDuration * gateDurationRatio).toLong() // 拍の60%をゲートオン（よりパンチのある音）

                // 現在のステップに対応する周波数を設定
                onStepChange(pattern[currentStep], true)
                delay(gateDuration)
                onStepChange(pattern[currentStep], false)
                delay(beatDuration - gateDuration)

                // 次のステップに進む
                currentStep = (currentStep + 1) % pattern.size
            }
        } else {
            onStepChange(Pitch.Silence, false)
        }
    }
}

@Composable
private fun SynthUi(
    synthState: SynthState,
    onEvent: (SynthEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            PlayControl(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isPlaying = synthState.isPlaying,
                onPlayingChange = { onEvent(SynthEvent.OnPlayingChange(it)) },
                onGateChange = { onEvent(SynthEvent.OnGateChange(it)) },
                onFrequencyChange = { onEvent(SynthEvent.OnFrequencyChange(it)) }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // ADSR envelope controls
                ParameterCard("ADSR Parameters") {
                    // Attack
                    Text("Attack: ${"%.2f".format(synthState.attack)}s")
                    Slider(
                        value = synthState.attack,
                        onValueChange = { onEvent(SynthEvent.OnAttackChange(it)) },
                        valueRange = 0.01f..1.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Decay
                    Text("Decay: ${"%.2f".format(synthState.decay)}s")
                    Slider(
                        value = synthState.decay,
                        onValueChange = { onEvent(SynthEvent.OnDecayChange(it)) },
                        valueRange = 0.01f..1.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Sustain
                    Text("Sustain: ${"%.2f".format(synthState.sustain)}")
                    Slider(
                        value = synthState.sustain,
                        onValueChange = { onEvent(SynthEvent.OnSustainChange(it)) },
                        valueRange = 0.0f..1.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Release
                    Text("Release: ${"%.2f".format(synthState.release)}s")
                    Slider(
                        value = synthState.release,
                        onValueChange = { onEvent(SynthEvent.OnReleaseChange(it)) },
                        valueRange = 0.01f..2.0f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // LFO frequency control
                ParameterCard("LFO Frequency") {
                    Text("LFO: ${"%.2f".format(synthState.lfoFrequency)} Hz")
                    Slider(
                        value = synthState.lfoFrequency,
                        onValueChange = { onEvent(SynthEvent.OnLfoFrequencyChange(it)) },
                        valueRange = 0.1f..10f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Pulse Width Modulation controls
                ParameterCard("Pulse Width Modulation") {
                    // Base Pulse Width
                    Text("Base Pulse Width: ${"%.2f".format(synthState.pulseWidth)}")
                    Slider(
                        value = synthState.pulseWidth,
                        onValueChange = { onEvent(SynthEvent.OnPulseWidthChange(it)) },
                        valueRange = -1f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // PWM Amount
                    Text("PWM Amount: ${"%.2f".format(synthState.pwmAmount)}")
                    Slider(
                        value = synthState.pwmAmount,
                        onValueChange = { onEvent(SynthEvent.OnPwmAmountChange(it)) },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // LPF controls
                ParameterCard("Low-Pass Filter") {
                    // Cutoff Frequency
                    Text("Cutoff Frequency: ${synthState.lpfCutoff.toInt()} Hz")
                    Slider(
                        value = synthState.lpfCutoff,
                        onValueChange = { onEvent(SynthEvent.OnLpfCutoffChange(it)) },
                        valueRange = 100f..10000f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Resonance
                    Text("Resonance: ${"%.2f".format(synthState.lpfResonance)}")
                    Slider(
                        value = synthState.lpfResonance,
                        onValueChange = { onEvent(SynthEvent.OnLpfResonanceChange(it)) },
                        valueRange = 0.1f..5.0f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlayControl(
    isPlaying: Boolean,
    onPlayingChange: (Boolean) -> Unit,
    onGateChange: (Boolean) -> Unit,
    onFrequencyChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        FilledTonalIconButton (
            onClick = { onPlayingChange(!isPlaying) },
            modifier = Modifier
                .size(
                    IconButtonDefaults.largeContainerSize(
                        widthOption = IconButtonDefaults.IconButtonWidthOption.Uniform
                    )
                )
        ) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                contentDescription = if (isPlaying) "Pause" else "Play",
            )
        }

        Spacer(Modifier.width(8.dp))

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        LaunchedEffect(isPressed) {
            if (isPressed) {
                onFrequencyChange(Pitch.C4.frequency)
                onGateChange(true)
            } else {
                onGateChange(false)
            }
        }
        FilledIconButton(
            onClick = {},
            interactionSource = interactionSource,
            shapes = IconButtonShapes(
                shape = IconButtonDefaults.largeRoundShape,
                pressedShape = IconButtonDefaults.largePressedShape
            ),
            modifier = Modifier
                .size(IconButtonDefaults.largeContainerSize())
                .weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.touch),
                contentDescription = "Touch to play",
            )
        }
    }
}

@Composable
private fun ParameterCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Preview(heightDp = 1200)
@Composable
private fun SynthUiPreview() {
    KoruriTheme {
        SynthUi(
            synthState = SynthState(),
            onEvent = {}
        )
    }
}