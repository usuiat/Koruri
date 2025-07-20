package net.engawapg.lib.koruri

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.engawapg.lib.koruri.audio.KoruriAudio
import net.engawapg.lib.koruri.audio.SineWaveGenerator

fun runKoruri(content: @Composable () -> Unit) {
    var updated = true
    val applier = KoruriApplier(
        root = KoruriNode(),
        onChange = { updated = true },
    )
    val clock = BroadcastFrameClock()
    val job = Job()
    val recomposer = Recomposer(clock + job)
    val composition = Composition(applier, recomposer)
    composition.setContent(content)

    Snapshot.registerApplyObserver { _, _ ->
        updated = true
    }

    val audio = KoruriAudio().apply { start() }
    val sineWaveGenerator = SineWaveGenerator()

    val scope = CoroutineScope(clock + job)
    scope.launch {
        launch {
            recomposer.runRecomposeAndApplyChanges()
        }
        launch {
            while (true) {
                val nano = System.nanoTime()
                clock.sendFrame(nano)
                delay(10)
            }
        }
        launch {
            while (true) {
                clock.withFrameNanos {
                    if (updated) {
                        updated = false
                        sineWaveGenerator.frequency = applier.root.calcFrequency()
                    }
                }
            }
        }
        launch {
            withContext(Dispatchers.IO) {
                val bufferSize = 1024
                while (true) {
                    val buffer = sineWaveGenerator.getNextSamples(bufferSize)
                    audio.write(buffer, bufferSize * 2)
                }
            }
        }
    }
}