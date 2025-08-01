package net.engawapg.lib.koruri

import net.engawapg.lib.koruri.modifier.AudioModifier

internal interface AudioBlock {
    val next: AudioBlock?
    fun getNextSamples(numSamples: Int): ShortArray
}

internal class AudioBlockImpl(
    private val element: AudioModifier,
    override val next: AudioBlock
) : AudioBlock {
    override fun getNextSamples(numSamples: Int): ShortArray {
        return element.getNextSamples(numSamples)
    }
}

internal class EndBlock(private val node: KoruriNode) : AudioBlock {
    override val next: AudioBlock? = null
    override fun getNextSamples(numSamples: Int): ShortArray {
        return node.children.firstOrNull()?.startBlock?.getNextSamples(numSamples) ?: ShortArray(numSamples)
    }
}