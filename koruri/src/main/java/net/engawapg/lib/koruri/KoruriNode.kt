package net.engawapg.lib.koruri

import net.engawapg.lib.koruri.processor.BypassProcessor
import net.engawapg.lib.koruri.processor.SignalProcessor

internal class KoruriNode {
    val children = ArrayList<KoruriNode>()

    private var processor: SignalProcessor = BypassProcessor()

    fun setProcessor(processor: SignalProcessor) {
        this.processor = processor
    }

    fun getNextSamples(numSamples: Int): FloatArray {
        var signal = FloatArray(numSamples * 2)
        for (child in children) {
            child.processor.process(signal)
        }
        processor.process(signal)
        return signal
    }
}