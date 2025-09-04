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

internal class EnvelopeModulator(
) {
    private var envelope = 0f
    private var attack = 0f
    private var decay = 0f
    private var sustain = 1f
    private var release = 0f
    private var gate = false

    fun setParameters(
        attack: Float,
        decay: Float,
        sustain: Float,
        release: Float,
        gate: Boolean,
    ) {
        this.attack = attack
        this.decay = decay
        this.sustain = sustain
        this.release = release
        this.gate = gate
    }

    private enum class Phase {
        Attack, Decay, Sustain, Release, Idle
    }
    private var phase = Phase.Idle

    private var phaseTime = 0f
    private var releaseLevel = 0f
    private val deltaTime = 1f / 48000f
    private var previousGate = false

    fun calculateValue(): Float {
        if (gate && !previousGate) {
            // Gate on - start attack phase
            phase = Phase.Attack
            phaseTime = 0f
            previousGate = true
        } else if (!gate && previousGate) {
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
                if (attack > 0f) {
                    envelope = phaseTime / attack
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
                if (decay > 0f) {
                    envelope = 1f - (1f - sustain) * (phaseTime / decay)
                    if (envelope <= sustain) {
                        envelope = sustain
                        phase = Phase.Sustain
                        phaseTime = 0f
                    }
                } else {
                    envelope = sustain
                    phase = Phase.Sustain
                    phaseTime = 0f
                }
            }

            Phase.Sustain -> {
                envelope = sustain
            }

            Phase.Release -> {
                if (release > 0f) {
                    envelope = releaseLevel * (1f - phaseTime / release)
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

        phaseTime += deltaTime

        return envelope.coerceIn(0f, 1f)
    }
}
