package net.engawapg.lib.koruri

internal interface SignalProcessor {
    fun process(input: ShortArray): ShortArray
}

internal class BypassProcessor : SignalProcessor {
    override fun process(input: ShortArray): ShortArray {
        return input
    }
}