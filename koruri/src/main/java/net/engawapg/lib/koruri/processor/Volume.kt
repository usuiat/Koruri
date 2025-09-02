/*
 * Copyright 2025 usuiat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.AudioProcessorNode
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
    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        val vol = volume()
        for (i in input.indices) {
            input[i] *= vol
        }
        return input
    }
}