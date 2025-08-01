package net.engawapg.lib.koruri

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.engawapg.lib.koruri.audio.KoruriAudio
import kotlin.coroutines.CoroutineContext

fun runKoruri(content: @Composable () -> Unit) {
    val clock = BroadcastFrameClock()
    val job = Job()
    val koruri = Koruri(clock + job)
    koruri.setContent(content)

    val audio = KoruriAudio().apply { start() }

    val scope = CoroutineScope(clock + job)
    scope.launch {
        launch {
            while (true) {
                val nano = System.nanoTime()
                clock.sendFrame(nano)
                delay(10)
            }
        }
        launch {
            withContext(Dispatchers.IO) {
                val bufferSize = 1024
                while (true) {
                    val buffer = koruri.getNextSamples(bufferSize)
                    audio.write(buffer, bufferSize * 2)
                }
            }
        }
    }
}

private class Koruri(coroutineContext: CoroutineContext) {
    private val coroutineScope = CoroutineScope(coroutineContext)
    private val clock = checkNotNull(coroutineContext[MonotonicFrameClock]) {
        "MonotonicFrameClock is required in the coroutine context"
    }
    private var updated = true
    val applier = KoruriApplier(
        root = KoruriNode(),
        onChange = { updated = true },
    )
    val recomposer = Recomposer(coroutineContext)
    val composition = Composition(applier, recomposer)

    init {
        Snapshot.registerApplyObserver { _, _ ->
            updated = true
        }

        coroutineScope.launch {
            while (true) {
                clock.withFrameNanos {
                    if (updated) {
                        updated = false
                    }
                }
            }
        }
        coroutineScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
    }

    fun setContent(content: @Composable () -> Unit) {
        composition.setContent(content)
    }

    fun getNextSamples(numSamples: Int): ShortArray {
        return applier.root.getNextSamples(numSamples)
    }
}
