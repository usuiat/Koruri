package net.engawapg.lib.koruri.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.engawapg.lib.koruri.KoruriApplier
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.processor.SignalProcessor

@Composable
internal fun Block(signalProcessor: SignalProcessor) {
    ComposeNode<KoruriNode, KoruriApplier>(
        factory = ::KoruriNode,
        update = {
            set(signalProcessor) { setProcessor(signalProcessor) }
        }
    )
}