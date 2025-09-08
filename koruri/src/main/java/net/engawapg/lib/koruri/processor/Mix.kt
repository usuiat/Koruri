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
import net.engawapg.lib.koruri.Block

/**
 * Composable for mixing multiple audio signals.
 * It sums the audio signals from all child composables.
 * Note that clipping may occur if the mixed signal exceeds the range of -1.0 to 1.0.
 * To prevent clipping, consider using the `Volume` composable to adjust the level of the children or the mixed output.
 *
 * @param content The child composables to be mixed.
 */
@Composable
public fun Mix(content: @Composable () -> Unit) {
    Block(content = content, signalProcessor = MixProcessor())
}

private class MixProcessor : SignalProcessor {
    override fun process(input: FloatArray, children: List<AudioProcessorNode>): FloatArray {
        val size = input.size
        val mixedSignal = FloatArray(size)
        for (child in children) {
            val output = child.process(input)
            for (i in 0 until size) {
                mixedSignal[i] += output[i]
            }
        }
        return mixedSignal
    }
}