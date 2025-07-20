package net.engawapg.lib.koruri.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.engawapg.lib.koruri.KoruriApplier
import net.engawapg.lib.koruri.KoruriNode
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SineWave(frequency: Float) {
    ComposeNode<KoruriNode, KoruriApplier>(
        factory = ::KoruriNode,
        update = {
            set(frequency) { this.frequency = it }
        }
    )
}

internal class SineWaveGenerator() {
    private val sampleRate = 48000
    var frequency: Float = 0f
    val amp = Short.MAX_VALUE / 2
    private var phase = 0.0

    fun getNextSamples(numSamples: Int): ShortArray {
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
