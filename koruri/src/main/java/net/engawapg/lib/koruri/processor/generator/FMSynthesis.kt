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

package net.engawapg.lib.koruri.processor.generator

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.AudioProcessorNode
import net.engawapg.lib.koruri.KoruriAudioConfig.SAMPLE_RATE
import net.engawapg.lib.koruri.audio.Block
import net.engawapg.lib.koruri.processor.SignalProcessor
import kotlin.math.PI
import kotlin.math.sin

/**
 * Composable for FM synthesis.
 * Generates sound using frequency modulation synthesis.
 *
 * @param carrierFrequency The carrier frequency in Hz.
 * @param modulator The FM synthesis modulator parameters.
 */
@Composable
public fun FMSynthesis(
    carrierFrequency: Float,
    modulator: FMSynthesisModulator,
) {
    Block(
        signalProcessor = FMSynthesiser(carrierFrequency, modulator)
    )
}

/**
 * FM synthesis modulator parameters.
 *
 * @property ratio The frequency ratio of the modulator to the carrier.
 * @property index The modulation index, which controls the amount of frequency deviation.
 */
public data class FMSynthesisModulator(
    val ratio: Float,
    val index: Float,
)

private const val PIx2 = PI.toFloat() * 2.0f


private class FMSynthesiser(
    private val carrierFrequency: Float,
    private val modulator: FMSynthesisModulator,
) : SignalProcessor {
    private var carrierPhase = 0.0f
    private var modulatorPhase = 0.0f

    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        val output = FloatArray(input.size)
        if (carrierFrequency == 0f) {
            carrierPhase = 0.0f
            modulatorPhase = 0.0f
            return output
        }

        val phaseDelta = PIx2 * carrierFrequency / SAMPLE_RATE
        val modulatorPhaseDelta = PIx2 * carrierFrequency * modulator.ratio / SAMPLE_RATE
        for (i in 0 until input.size) {
            output[i] = sin(carrierPhase + modulator.index * sin(modulatorPhase))

            carrierPhase += phaseDelta
            // phase を 2π の範囲内に正規化
            if (carrierPhase >= PIx2) {
                carrierPhase -= PIx2
            }

            modulatorPhase += modulatorPhaseDelta
            if (modulatorPhase >= PIx2) {
                modulatorPhase -= PIx2
            }
        }

        return output
    }
}