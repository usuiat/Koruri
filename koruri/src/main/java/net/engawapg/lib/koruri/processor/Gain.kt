package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.audio.Block

@Composable
fun Gain(gain: Float) {
    Block(GainProcessor(gain))
}

private class GainProcessor(private val gain: Float) : SignalProcessor {
    override fun process(signal: FloatArray) {
        for (i in signal.indices) {
            signal[i] *= gain
        }
    }
}