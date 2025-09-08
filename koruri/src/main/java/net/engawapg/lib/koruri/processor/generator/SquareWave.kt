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
import net.engawapg.lib.koruri.Block
import net.engawapg.lib.koruri.processor.SignalProcessor
import kotlin.math.PI

/**
 * Composable for generating a square wave.
 * Produces a square wave signal with specified amplitude, frequency, and pulse width.
 *
 * @param amplitude The amplitude of the square wave. The value should be between 0.0 and 1.0.
 * @param frequency The frequency of the square wave in Hz.
 * @param pulseWidth The pulse width of the square wave. The value should be between -1.0 and 1.0.
 * -1.0 means the output is always -amplitude.
 * 0.0 means a 50% duty cycle.
 * 1.0 means the output is always +amplitude.
 */
@Composable
public fun SquareWave(
    amplitude: Float = 0.5f,
    frequency: Float,
    pulseWidth: Float = 0f
) {
    Block(signalProcessor = SquareWaveGenerator(amplitude, frequency, pulseWidth))
}

/**
 * Composable for generating a square wave.
 * Produces a square wave signal with specified amplitude, dynamic frequency, and dynamic pulse width.
 *
 * @param amplitude The amplitude of the square wave. The value should be between 0.0 and 1.0.
 * @param frequency A lambda returning the current frequency of the square wave in Hz.
 * @param pulseWidth A lambda returning the current pulse width of the square wave. The value should be between -1.0 and 1.0.
 * -1.0 means the output is always -amplitude.
 * 0.0 means a 50% duty cycle.
 * 1.0 means the output is always +amplitude.
 */
@Composable
public fun SquareWave(
    amplitude: Float = 0.5f,
    frequency: () -> Float,
    pulseWidth: () -> Float = { 0f }
) {
    Block(signalProcessor = SquareWaveGenerator(amplitude, frequency, pulseWidth))
}

private const val PIx2 = PI.toFloat() * 2.0f

private class SquareWaveGenerator(
    private val amplitude: Float,
    private val frequency: () -> Float,
    private val pulseWidth: () -> Float
) : SignalProcessor {
    constructor(
        amplitude: Float = 0.5f,
        frequencyValue: Float,
        pulseWidthValue: Float = 0f
    ) : this(amplitude, { frequencyValue }, { pulseWidthValue })

    private var phase = 0.0f

    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        val frequency = frequency()
        val output = FloatArray(input.size)
        if (frequency == 0f) {
            phase = 0.0f
            return output
        }

        val phaseDelta = PIx2 * frequency / SAMPLE_RATE
        for (i in input.indices) {
            // pulseWidth: -1=すべて負, 0=50%デューティ, +1=すべて正
            val normalizedPhase = phase / PIx2 // 0.0 to 1.0
            val pw = pulseWidth()
            val threshold = 0.5f + pw * 0.5f // -1→0, 0→0.5, +1→1

            output[i] = if (normalizedPhase < threshold) amplitude else -amplitude

            phase += phaseDelta
            // phase を 2π の範囲内に正規化
            if (phase >= PIx2) {
                phase -= PIx2
            }
        }

        return output
    }
}