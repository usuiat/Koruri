package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block

/**
 * Composable for controlling audio volume.
 * Applies a constant volume to the audio signal.
 *
 * @param volume The volume value to apply.
 */
@Composable
public fun Volume(volume: Float) {
    Block(VolumeProcessor(volume))
}

/**
 * Composable for controlling audio volume.
 * Applies a dynamic volume to the audio signal.
 *
 * @param volume A lambda that returns the current volume value.
 */
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