package net.engawapg.lib.koruri

import net.engawapg.lib.koruri.processor.ChainProcessor
import net.engawapg.lib.koruri.processor.SignalProcessor

internal class KoruriNode {
    val children = ArrayList<KoruriNode>()

    private var processor: SignalProcessor = ChainProcessor()

    fun setProcessor(processor: SignalProcessor) {
        this.processor = processor
    }

    fun getNextSamples(numSamples: Int): FloatArray {
        val emptyData = FloatArray(numSamples)
        val output = process(emptyData)
        return output
    }

    fun process(input: FloatArray): FloatArray =
        processor.process(input, children)
}