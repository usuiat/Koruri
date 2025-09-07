/*
 * Copyright 2025 usuiat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Composable for playing an instrument note.
 * Generates sound using FM synthesis and applies an envelope to the note.
 *
 * @param note The note to play.
 * @param instrument The instrument definition.
 * @param amplitude The initial amplitude of the note. The value should be between 0.0 and 1.0.
 */
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

/**
 * Musical note.
 *
 * @property pitch The pitch of the note.
 * @property id The unique identifier for the note. A new ID is generated for each Note instance.
 */
public class Note(
    public val pitch: Pitch,
) {
    public val id: Uuid = Uuid.random()
    public companion object {
        /** A special note representing silence. */
        public val Silence: Note = Note(Pitch.Silence)
    }
}

/**
 * Definition of instrument sound
 *
 * @property modulator The FM synthesis modulator.
 * @property envelopeSpec The envelope animation specification that controls the volume change over time.
 */
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
