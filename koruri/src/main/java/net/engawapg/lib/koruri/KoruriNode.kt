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

package net.engawapg.lib.koruri

import net.engawapg.lib.koruri.processor.ChainProcessor
import net.engawapg.lib.koruri.processor.SignalProcessor

/**
 * Interface for nodes that can process audio signals.
 */
public interface AudioProcessorNode {
    /**
     * Processes the input audio signal and returns the processed result.
     *
     * @param input The input audio data
     * @return The processed audio data
     */
    public fun process(input: FloatArray): FloatArray
}

/**
 * Represents a node in the Koruri audio composition tree.
 * Each node can have child nodes and a signal processor.
 */
internal class KoruriNode: AudioProcessorNode {
    val children: ArrayList<KoruriNode> = ArrayList()

    private var processor: SignalProcessor = ChainProcessor()

    fun setProcessor(processor: SignalProcessor) {
        this.processor = processor
    }

    fun getNextSamples(numSamples: Int): FloatArray {
        val emptyData = FloatArray(numSamples)
        val output = process(emptyData)
        return output
    }

    override fun process(input: FloatArray): FloatArray =
        processor.process(input, children)
}