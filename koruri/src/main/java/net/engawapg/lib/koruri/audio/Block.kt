package net.engawapg.lib.koruri.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableComposeNode
import net.engawapg.lib.koruri.KoruriApplier
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.processor.SignalProcessor

/**
 * Base composable for audio signal processing in Koruri.
 * Creates a node with the specified SignalProcessor.
 *
 * @param signalProcessor The processor to apply to the audio signal.
 */
@Composable
internal fun Block(signalProcessor: SignalProcessor) {
    ReusableComposeNode<KoruriNode, KoruriApplier>(
        factory = ::KoruriNode,
        update = {
            set(signalProcessor) { setProcessor(signalProcessor) }
        }
    )
}

/**
 * Base composable for audio signal processing in Koruri with child content.
 * Creates a node with the specified SignalProcessor and child composable content.
 *
 * @param content The child composable content.
 * @param signalProcessor The processor to apply to the audio signal.
 */
@Composable
internal fun Block(
    content: @Composable () -> Unit,
    signalProcessor: SignalProcessor,
) {
    ReusableComposeNode<KoruriNode, KoruriApplier>(
        factory = ::KoruriNode,
        update = {
            set(signalProcessor) { setProcessor(signalProcessor) }
        },
        content = content,
    )
}