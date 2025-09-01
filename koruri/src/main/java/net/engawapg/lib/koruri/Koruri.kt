package net.engawapg.lib.koruri

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.engawapg.lib.koruri.audio.KoruriAudio

/**
 * Composable function to set up and manage Koruri audio content.
 * Initializes the Koruri engine and disposes it when no longer needed.
 * This is the entry point for using Koruri within a Compose UI.
 *
 * @param content The Koruri composable content to be processed for audio output.
 */
@Composable
public fun KoruriContent(content: @Composable () -> Unit) {
    val koruri = remember { Koruri() }
    koruri.setContent(content)

    DisposableEffect(Unit) {
        onDispose {
            koruri.dispose()
        }
    }
}

/**
 * Sets up Koruri audio content outside of Compose context.
 * Initializes the Koruri engine and applies the given composable content.
 * This is the entry point for using Koruri in non-Compose environments.
 *
 * @param content The Koruri composable content to be processed for audio output.
 */
public fun setKoruriContent(content: @Composable () -> Unit) {
    val koruri = Koruri()
    koruri.setContent(content)
}

private class Koruri() {
    val clock = BroadcastFrameClock()
    private val coroutineScope = CoroutineScope(clock + Dispatchers.Main)
    val rootNode = KoruriNode()
    val applier = KoruriApplier(rootNode)
    val recomposer = Recomposer(clock)
    val composition = Composition(applier, recomposer)
    val audio = KoruriAudio()

    init {
        audio.start()

        coroutineScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
        coroutineScope.launch {
            while (true) {
                clock.sendFrame(System.nanoTime())
                delay(1)
            }
        }
        coroutineScope.launch {
            val buffer = FloatArray(KoruriAudioConfig.BUFFER_SIZE * 2)
            while (true) {
                val samples = rootNode.getNextSamples(KoruriAudioConfig.BUFFER_SIZE)
                withContext(Dispatchers.IO) {
                    for (i in 0 until KoruriAudioConfig.BUFFER_SIZE) {
                        buffer[i * 2] = samples[i]
                        buffer[i * 2 + 1] = samples[i]
                    }
                    audio.write(data = buffer, size = buffer.size)
                }
            }
        }
    }

    fun setContent(content: @Composable () -> Unit) {
        composition.setContent(content)
    }

    fun dispose() {
        composition.dispose()
        recomposer.close()
        coroutineScope.cancel()
    }
}
