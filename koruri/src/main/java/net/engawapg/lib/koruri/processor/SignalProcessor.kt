package net.engawapg.lib.koruri.processor

import net.engawapg.lib.koruri.KoruriNode

internal interface SignalProcessor {
    fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray
}