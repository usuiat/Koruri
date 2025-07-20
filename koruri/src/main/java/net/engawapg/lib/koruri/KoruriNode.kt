package net.engawapg.lib.koruri

internal class KoruriNode {
    var frequency = 0.0f
    val children = ArrayList<KoruriNode>()

    fun calcFrequency(): Float = children.lastOrNull {
        it.calcFrequency() > 0.1f
    } ?.frequency ?: frequency
}