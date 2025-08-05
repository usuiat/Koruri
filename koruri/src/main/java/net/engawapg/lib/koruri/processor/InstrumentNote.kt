@file:OptIn(ExperimentalUuidApi::class)

package net.engawapg.lib.koruri.processor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun InstrumentNote(
    note: Note,
    instrument: Instrument,
) {
    Chain {
        FMSynthesis(
            carrierFrequency = note.pitch.frequency,
            modulator = instrument.modulator,
        )

        var volume = remember { Animatable(1f) }
        LaunchedEffect(note.id) {
            volume.snapTo(1f)
            volume.animateTo(
                targetValue = 0f,
                animationSpec = instrument.envelopeSpec,
            )
        }
        Volume { volume.value }
    }
}

class Note(
    val pitch: Pitch,
) {
    val id = Uuid.random()
}

data class Instrument(
    val modulator: FMSynthesisModulator,
    val envelopeSpec: AnimationSpec<Float>,
) {
    companion object {
        val Celesta = Instrument(
            modulator = FMSynthesisModulator(
                ratio = 4.0f,
                index = 1.5f,
            ),
            envelopeSpec = tween(
                durationMillis = 4000,
                delayMillis = 200,
                easing = EaseOutExpo
            )
        )
        val ElectricPiano = Instrument(
            modulator = FMSynthesisModulator(
                ratio = 1.0f,
                index = 0.5f,
            ),
            envelopeSpec = tween(
                durationMillis = 2000,
                delayMillis = 200,
                easing = EaseOutExpo
            )
        )
        val SoftPad = Instrument(
            modulator = FMSynthesisModulator(
                ratio = 0.5f,
                index = 1.0f,
            ),
            envelopeSpec = tween(
                durationMillis = 2000,
                delayMillis = 200,
                easing = EaseOutExpo
            )
        )
        val CrystalBell = Instrument(
            modulator = FMSynthesisModulator(
                ratio = 7f,
                index = 1f,
            ),
            envelopeSpec = tween(
                durationMillis = 2000,
                delayMillis = 200,
                easing = EaseOutExpo
            )
        )
    }
}
