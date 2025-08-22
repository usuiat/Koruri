package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block

@Composable
fun Chain(content: @Composable () -> Unit) {
    Block(content = content, signalProcessor = ChainProcessor())
}

internal class ChainProcessor : SignalProcessor {
    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        var signal = input
        for (node in childrenNode) {
            signal = node.process(signal)
        }
        return signal
    }
}