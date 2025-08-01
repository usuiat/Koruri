package net.engawapg.lib.koruri.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.engawapg.lib.koruri.KoruriApplier
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.modifier.KoruriModifier

@Composable
internal fun Block(modifier: KoruriModifier = KoruriModifier) {
    ComposeNode<KoruriNode, KoruriApplier>(
        factory = ::KoruriNode,
        update = {
            set(modifier) { setModifier(modifier) }
        }
    )
}