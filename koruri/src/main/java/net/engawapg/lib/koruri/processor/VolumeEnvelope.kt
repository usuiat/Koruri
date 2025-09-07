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

package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.AudioProcessorNode
import net.engawapg.lib.koruri.audio.Block

/**
 * Composable for applying an ADSR envelope to the audio signal.
 * Envelope parameters can be provided as lambdas for dynamic control.
 *
 * @param attack Lambda returning the attack time in seconds.
 * @param decay Lambda returning the decay time in seconds.
 * @param sustain Lambda returning the sustain level. The value should be between 0.0 and 1.0.
 * @param release Lambda returning the release time in seconds.
 * @param gate Lambda returning the gate signal. `true` triggers the attack/decay/sustain phases, `false` triggers the release phase.
 */
@Composable
public fun VolumeEnvelope(
    attack: () -> Float,
    decay: () -> Float,
    sustain: () -> Float,
    release: () -> Float,
    gate: () -> Boolean
) {
    Block(
        signalProcessor = VolumeEnvelopeProcessor(attack, decay, sustain, release, gate)
    )
}

/**
 * Composable for applying an ADSR envelope to the audio signal.
 *
 * @param attack The attack time in seconds.
 * @param decay The decay time in seconds.
 * @param sustain The sustain level. The value should be between 0.0 and 1.0.
 * @param release The release time in seconds.
 * @param gate The gate signal. `true` triggers the attack/decay/sustain phases, `false` triggers the release phase.
 */
@Composable
public fun VolumeEnvelope(
    attack: Float,
    decay: Float,
    sustain: Float,
    release: Float,
    gate: Boolean
) {
    Block(
        signalProcessor = VolumeEnvelopeProcessor({ attack }, { decay }, { sustain }, { release }, { gate })
    )
}

private class VolumeEnvelopeProcessor(
    private val attack: () -> Float,
    private val decay: () -> Float,
    private val sustain: () -> Float,
    private val release: () -> Float,
    private val gate: () -> Boolean
) : SignalProcessor {

    private enum class Phase {
        Attack, Decay, Sustain, Release, Idle
    }

    private var phase = Phase.Idle
    private var envelope = 0f
    private var phaseTime = 0f
    private var releaseLevel = 0f
    private val sampleRate = 48000
    private var previousGate = false

    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        // inputをコピーしてエンベロープ処理
        val output = input.copyOf()
        val deltaTime = 1f / sampleRate

        for (i in input.indices) {
            // Check gate state changes
            val currentGate = gate()

            if (currentGate && !previousGate) {
                // Gate on - start attack phase
                phase = Phase.Attack
                phaseTime = 0f
                previousGate = true
            } else if (!currentGate && previousGate) {
                // Gate off - start release phase
                if (phase != Phase.Idle) {
                    phase = Phase.Release
                    phaseTime = 0f
                    releaseLevel = envelope
                }
                previousGate = false
            }

            // Process envelope phases
            when (phase) {
                Phase.Attack -> {
                    val attackTime = attack()
                    if (attackTime > 0f) {
                        envelope = phaseTime / attackTime
                        if (envelope >= 1f) {
                            envelope = 1f
                            phase = Phase.Decay
                            phaseTime = 0f
                        }
                    } else {
                        envelope = 1f
                        phase = Phase.Decay
                        phaseTime = 0f
                    }
                }

                Phase.Decay -> {
                    val decayTime = decay()
                    val sustainLevel = sustain()
                    if (decayTime > 0f) {
                        envelope = 1f - (1f - sustainLevel) * (phaseTime / decayTime)
                        if (envelope <= sustainLevel) {
                            envelope = sustainLevel
                            phase = Phase.Sustain
                            phaseTime = 0f
                        }
                    } else {
                        envelope = sustainLevel
                        phase = Phase.Sustain
                        phaseTime = 0f
                    }
                }

                Phase.Sustain -> {
                    envelope = sustain()
                }

                Phase.Release -> {
                    val releaseTime = release()
                    if (releaseTime > 0f) {
                        envelope = releaseLevel * (1f - phaseTime / releaseTime)
                        if (envelope <= 0f) {
                            envelope = 0f
                            phase = Phase.Idle
                            phaseTime = 0f
                        }
                    } else {
                        envelope = 0f
                        phase = Phase.Idle
                        phaseTime = 0f
                    }
                }

                Phase.Idle -> {
                    envelope = 0f
                }
            }

            // Apply envelope to input signal
            output[i] = input[i] * envelope
            phaseTime += deltaTime
        }

        return output
    }
}
