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

@Composable
fun KoruriContent(content: @Composable () -> Unit) {
    val koruri = remember { Koruri() }
    koruri.setContent(content)

    DisposableEffect(Unit) {
        onDispose {
            koruri.dispose()
        }
    }
}

fun runKoruri(content: @Composable () -> Unit) {
    val koruri = Koruri()
    koruri.setContent(content)
}

private class Koruri() {
    val clock = BroadcastFrameClock()
    private val coroutineScope = CoroutineScope(clock + Dispatchers.Main)
    val applier = KoruriApplier(
        root = KoruriNode(),
    )
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
            val numSamples = 48
            while (true) {
                val buffer = applier.root.getNextSamples(numSamples)
                withContext(Dispatchers.IO) {
                    audio.write(data = buffer, size = numSamples * 2)
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
