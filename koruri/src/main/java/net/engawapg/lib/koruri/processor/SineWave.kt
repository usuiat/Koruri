package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.audio.Block
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SineWave(frequency: Float) {
    Block(signalProcessor = SineWaveGenerator(frequency))
}

private class SineWaveGenerator(
    private val frequency: Float,
) : TransformProcessor {
    private val sampleRate = 48000
    val amp = 1.0f
    private var phase = 0.0f

    override fun process(input: FloatArray): FloatArray {
        val numSamples = input.size / 2
        val output = FloatArray(numSamples * 2)
        if (frequency == 0f) {
            phase = 0.0f
            return output
        }

        val phaseDelta = 2.0f * PI.toFloat() * frequency / sampleRate
        for (i in 0 until numSamples) {
            val sample = sin(phase) * amp
            output[i * 2] = sample
            output[i * 2 + 1] = sample
            phase += phaseDelta
        }

        return output
    }
}
