package net.engawapg.lib.koruri.modifier

interface AudioModifier : KoruriModifier.Element {
    fun getNextSamples(numSamples: Int): ShortArray
}