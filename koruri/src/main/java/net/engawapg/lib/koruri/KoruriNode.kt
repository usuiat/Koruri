package net.engawapg.lib.koruri

internal class KoruriNode {
    val children = ArrayList<KoruriNode>()

    private var processor: SignalProcessor = BypassProcessor()

    fun setProcessor(processor: SignalProcessor) {
        this.processor = processor
    }

    fun getNextSamples(numSamples: Int): ShortArray {
        var signal = ShortArray(numSamples * 2)
        for (child in children) {
            signal = child.processor.process(signal)
        }
        signal = processor.process(signal)
        return signal
    }
}