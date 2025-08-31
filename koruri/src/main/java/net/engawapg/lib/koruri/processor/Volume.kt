package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block

@Composable
public fun Volume(volume: Float) {
    Block(VolumeProcessor(volume))
}

@Composable
public fun Volume(volume: () -> Float) {
    Block(VolumeProcessor(volume))
}

private class VolumeProcessor(private val volume: () -> Float) : SignalProcessor {
    constructor(volumeValue: Float) : this({ volumeValue })
    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        val vol = volume()
        for (i in input.indices) {
            input[i] *= vol
        }
        return input
    }
}