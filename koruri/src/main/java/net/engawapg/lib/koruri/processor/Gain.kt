package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.audio.Block

@Composable
fun Gain(gain: Float) {
    Block(GainProcessor(gain))
}

@Composable
fun Gain(gain: () -> Float) {
    Block(GainProcessor(gain))
}

private class GainProcessor(private val gain: () -> Float) : TransformProcessor {
    constructor(gainValue: Float) : this({ gainValue })
    override fun process(input: FloatArray): FloatArray {
        val g = gain()
        for (i in input.indices) {
            input[i] *= g
        }
        return input
    }
}