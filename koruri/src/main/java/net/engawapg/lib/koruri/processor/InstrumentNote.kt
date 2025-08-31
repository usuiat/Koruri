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
public fun InstrumentNote(
    note: Note,
    instrument: Instrument,
    amplitude: Float = 0.5f,
) {
    Chain {
        FMSynthesis(
            carrierFrequency = note.pitch.frequency,
            modulator = instrument.modulator,
        )

        val volume = remember { Animatable(amplitude) }
        LaunchedEffect(note.id) {
            volume.snapTo(amplitude)
            volume.animateTo(
                targetValue = 0f,
                animationSpec = instrument.envelopeSpec,
            )
        }
        Volume { volume.value }
    }
}

public class Note(
    public val pitch: Pitch,
) {
    public val id: Uuid = Uuid.random()
    public companion object {
        public val Silence: Note = Note(Pitch.Silence)
    }
}

public data class Instrument(
    val modulator: FMSynthesisModulator,
    val envelopeSpec: AnimationSpec<Float>,
) {
    public companion object {
        public val Celesta: Instrument = Instrument(
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
        public val ElectricPiano: Instrument = Instrument(
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
        public val SoftPad: Instrument = Instrument(
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
        public val CrystalBell: Instrument = Instrument(
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
