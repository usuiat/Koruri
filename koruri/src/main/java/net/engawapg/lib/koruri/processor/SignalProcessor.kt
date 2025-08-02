package net.engawapg.lib.koruri.processor

internal interface SignalProcessor {
    fun process(signal: FloatArray)
}

internal class BypassProcessor : SignalProcessor {
    override fun process(signal: FloatArray) {}
}