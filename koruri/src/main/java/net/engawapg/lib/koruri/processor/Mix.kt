package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block

@Composable
fun Mix(content: @Composable () -> Unit) {
    Block(content = content, signalProcessor = MixProcessor())
}

private class MixProcessor : SignalProcessor {
    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        val size = input.size
        val mixedSignal = FloatArray(size)
        for (node in childrenNode) {
            val output = node.process(input)
            for (i in 0 until size) {
                mixedSignal[i] += output[i]
            }
        }
        return mixedSignal
    }
}