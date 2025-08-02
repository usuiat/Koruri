package net.engawapg.lib.koruri

import net.engawapg.lib.koruri.processor.ChainProcessor
import net.engawapg.lib.koruri.processor.CompositeProcessor
import net.engawapg.lib.koruri.processor.SignalProcessor
import net.engawapg.lib.koruri.processor.TransformProcessor

internal class KoruriNode {
    val children = ArrayList<KoruriNode>()

    private var processor: SignalProcessor = ChainProcessor()

    fun setProcessor(processor: SignalProcessor) {
        this.processor = processor
    }

    fun getNextSamples(numSamples: Int): FloatArray {
        val input = FloatArray(numSamples * 2)
        val output = process(input)
        return output
    }

    fun process(signal: FloatArray): FloatArray {
        return when (val currentProcessor = processor) {
            is CompositeProcessor -> {
                currentProcessor.process(signal, children)
            }
            is TransformProcessor -> {
                currentProcessor.process(signal)
            }
        }
    }
}