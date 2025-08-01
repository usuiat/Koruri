package net.engawapg.lib.koruri.generator

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.SignalProcessor
import net.engawapg.lib.koruri.audio.Block
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SineWave(frequency: Float) {
    Block(signalProcessor = SineWaveGenerator(frequency))
}

private class SineWaveGenerator(
    private val frequency: Float,
) : SignalProcessor {
    private val sampleRate = 48000
    val amp = Short.MAX_VALUE / 2
    private var phase = 0.0

    override fun process(input: ShortArray): ShortArray {
        val numSamples = input.size / 2
        if (frequency == 0f) {
            phase = 0.0
            return ShortArray(numSamples * 2)
        }

        val phaseDelta = 2.0 * PI * frequency / sampleRate
        val buffer = ShortArray(numSamples * 2)
        for (i in 0 until numSamples) {
            val sample = sin(phase) * amp
            phase += phaseDelta
            val shortSample = sample
                .toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
            buffer[i * 2] = shortSample
            buffer[i * 2 + 1] = shortSample
        }
        return buffer
    }
}
