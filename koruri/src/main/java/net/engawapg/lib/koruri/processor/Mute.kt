package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable

/**
 * Composable for muting or unmuting the audio signal.
 *
 * @param muted If true, mutes the audio signal; if false, passes the signal through.
 */
@Composable
public fun Mute(muted: Boolean) {
    Volume(if (muted) 0f else 1f)
}
