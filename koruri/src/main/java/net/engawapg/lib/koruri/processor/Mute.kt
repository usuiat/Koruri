package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable

@Composable
fun Mute(muted: Boolean) {
    Volume(if (muted) 0f else 1f)
}
