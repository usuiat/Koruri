package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Composable for applying a low-pass filter to the audio signal.
 *
 * @param cutoff The cutoff frequency of the filter.
 * @param resonance The resonance (Q factor) of the filter.
 */
@Composable
public fun LowPassFilter(
    cutoff: Float,
    resonance: Float = 1f
) {
    Block(
        signalProcessor = LowPassFilterProcessor({ cutoff }, { resonance })
    )
}

/**
 * Composable for applying a low-pass filter to the audio signal.
 *
 * @param cutoff A lambda returning the current cutoff frequency.
 * @param resonance A lambda returning the current resonance (Q factor).
 */
@Composable
public fun LowPassFilter(
    cutoff: () -> Float,
    resonance: () -> Float = { 1f }
) {
    Block(
        signalProcessor = LowPassFilterProcessor(cutoff, resonance)
    )
}

private class LowPassFilterProcessor(
    private val cutoff: () -> Float,
    private val resonance: () -> Float
) : SignalProcessor {

    private val sampleRate = 48000
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f

    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        // inputをコピーしてフィルター処理
        val signal = input.copyOf()

        // Low Pass Filterを適用
        val cutoffFreq = cutoff()
        val q = resonance()

        // バイクワッドフィルタの係数計算
        val omega = 2.0 * PI * cutoffFreq / sampleRate
        val sinOmega = sin(omega).toFloat()
        val cosOmega = cos(omega).toFloat()
        val alpha = sinOmega / (2.0f * q)

        val b0 = (1.0f - cosOmega) / 2.0f
        val b1 = 1.0f - cosOmega
        val b2 = (1.0f - cosOmega) / 2.0f
        val a0 = 1.0f + alpha
        val a1 = -2.0f * cosOmega
        val a2 = 1.0f - alpha

        // 係数を正規化
        val b0Norm = b0 / a0
        val b1Norm = b1 / a0
        val b2Norm = b2 / a0
        val a1Norm = a1 / a0
        val a2Norm = a2 / a0

        // フィルタ処理
        for (i in signal.indices) {
            val input = signal[i]
            val output = b0Norm * input + b1Norm * x1 + b2Norm * x2 - a1Norm * y1 - a2Norm * y2

            // 状態更新
            x2 = x1
            x1 = input
            y2 = y1
            y1 = output

            signal[i] = output
        }

        return signal
    }
}
