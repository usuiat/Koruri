package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SineWave(amplitude: Float = 0.5f, frequency: Float) {
    Block(signalProcessor = SineWaveGenerator(amplitude, frequency))
}

@Composable
fun SineWave(amplitude: Float = 0.5f, frequency: () -> Float) {
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

    private val sampleRate = 48000
    private var phase = 0.0f

    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        val frequency = frequency()
        val output = FloatArray(input.size)
        if (frequency == 0f) {
            phase = 0.0f
            return output
        }

        val phaseDelta = PIx2 * frequency / sampleRate
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
