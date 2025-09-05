package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.AudioProcessorNode
import net.engawapg.lib.koruri.audio.Block

@Composable
public fun VolumeModulation(
    volumeModulator: Modulator,
    volume: Float = 1f,
) {
    Block(
        signalProcessor = VolumeModulator(
            volume = volume,
            volumeModulator = volumeModulator,
        )
    )
}

private class VolumeModulator(
    private val volumeModulator: Modulator,
    private val volume: Float,
) : SignalProcessor {
    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        for (i in input.indices) {
            input[i] *= volume * volumeModulator.modulate()
        }
        return input
    }
}