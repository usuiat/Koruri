package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.audio.Block

@Composable
fun Gain(gain: Float) {
    Block(GainProcessor(gain))
}

private class GainProcessor(private val gain: Float) : TransformProcessor {
    override fun process(input: FloatArray): FloatArray {
        for (i in input.indices) {
            input[i] *= gain
        }
        return input
    }
}