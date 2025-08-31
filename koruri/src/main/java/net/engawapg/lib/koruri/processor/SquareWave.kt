package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block
import kotlin.math.PI

@Composable
public fun SquareWave(
    amplitude: Float = 0.5f,
    frequency: Float,
    pulseWidth: Float = 0f
) {
    Block(signalProcessor = SquareWaveGenerator(amplitude, frequency, pulseWidth))
}

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