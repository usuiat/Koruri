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
 * Composable for generating a sine wave.
 * Produces a sine wave signal with specified amplitude and frequency.
 *
 * @param amplitude The amplitude of the sine wave. The value should be between 0.0 and 1.0.
 * @param frequency The frequency of the sine wave in Hz.
 */
@Composable
public fun SineWave(amplitude: Float = 0.5f, frequency: Float) {
    Block(signalProcessor = SineWaveGenerator(amplitude, frequency))
}

/**
 * Composable for generating a sine wave.
 * Produces a sine wave signal with specified amplitude and dynamic frequency.
 *
 * @param amplitude The amplitude of the sine wave. The value should be between 0.0 and 1.0.
 * @param frequency A lambda returning the current frequency of the sine wave in Hz.
 */
@Composable
public fun SineWave(amplitude: Float = 0.5f, frequency: () -> Float) {
    Block(signalProcessor = SineWaveGenerator(amplitude, frequency))
}

private const val PIx2 = PI.toFloat() * 2.0f

private class SineWaveGenerator(
    private val amplitude: Float,
    private val frequency: () -> Float,
) : SignalProcessor {
    constructor(
        amplitude: Float = 0.5f,
        frequencyValue: Float,
    ) : this(amplitude, { frequencyValue })

    private var phase = 0.0f

    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        val frequency = frequency()
        val output = FloatArray(input.size)
        if (frequency == 0f) {
            phase = 0.0f
            return output
        }

        val phaseDelta = PIx2 * frequency / SAMPLE_RATE
        for (i in 0 until input.size) {
            output[i] = sin(phase) * amplitude
            phase += phaseDelta
            // phase を 2π の範囲内に正規化
            if (phase >= PIx2) {
                phase -= PIx2
            }
        }

        return output
    }
}
