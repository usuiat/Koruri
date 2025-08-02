package net.engawapg.lib.koruri.processor

import net.engawapg.lib.koruri.KoruriNode

internal sealed interface SignalProcessor

internal interface TransformProcessor : SignalProcessor {
    fun process(input: FloatArray): FloatArray
}

internal interface CompositeProcessor : SignalProcessor {
    fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray
}