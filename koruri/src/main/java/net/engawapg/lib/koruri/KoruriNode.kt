package net.engawapg.lib.koruri

import net.engawapg.lib.koruri.modifier.AudioModifier
import net.engawapg.lib.koruri.modifier.KoruriModifier

internal class KoruriNode {
    val children = ArrayList<KoruriNode>()

    private val endBlock: AudioBlock = EndBlock(this)
    var startBlock: AudioBlock = endBlock
        private set

    fun setModifier(modifier: KoruriModifier) {
        startBlock = modifier.foldOut(endBlock) { element, nextBlock ->
            var nextBlock = nextBlock
            if (element is AudioModifier) {
                nextBlock = AudioBlockImpl(element, nextBlock)
            }
            nextBlock
        }
    }

    fun getNextSamples(numSamples: Int): ShortArray {
        return startBlock.getNextSamples(numSamples)
    }
}