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
 * @param attack Lambda returning the attack time.
 * @param decay Lambda returning the decay time.
 * @param sustain Lambda returning the sustain level.
 * @param release Lambda returning the release time.
 * @param gate Lambda returning the gate signal (note on/off).
 * Changing gate resets the envelope.
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
 * @param attack The attack time.
 * @param decay The decay time.
 * @param sustain The sustain level.
 * @param release The release time.
 * @param gate The gate signal (note on/off).
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
    private val modulator = EnvelopeModulator()

    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        modulator.setParameters(
            attack = attack(),
            decay = decay(),
            sustain = sustain(),
            release = release(),
            gate = gate()
        )

        for (i in input.indices) {
            val envelope = modulator.calculateValue()
            input[i] = input[i] * envelope
        }

        return input
    }
}
