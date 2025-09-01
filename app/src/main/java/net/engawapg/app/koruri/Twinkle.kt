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

package net.engawapg.app.koruri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Instrument
import net.engawapg.lib.koruri.processor.InstrumentNote
import net.engawapg.lib.koruri.processor.Mix
import net.engawapg.lib.koruri.processor.Note
import net.engawapg.lib.koruri.processor.Pitch.A5
import net.engawapg.lib.koruri.processor.Pitch.C4
import net.engawapg.lib.koruri.processor.Pitch.C5
import net.engawapg.lib.koruri.processor.Pitch.D4
import net.engawapg.lib.koruri.processor.Pitch.D5
import net.engawapg.lib.koruri.processor.Pitch.E4
import net.engawapg.lib.koruri.processor.Pitch.E5
import net.engawapg.lib.koruri.processor.Pitch.F4
import net.engawapg.lib.koruri.processor.Pitch.F5
import net.engawapg.lib.koruri.processor.Pitch.G4
import net.engawapg.lib.koruri.processor.Pitch.G5
import net.engawapg.lib.koruri.processor.Pitch.Silence

@Composable
internal fun TwinkleScreen(
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Button(
                onClick = { isPlaying = !isPlaying },
            ) {
                Text(if (isPlaying) "Stop" else "Play")
            }
        }
    }

    KoruriContent {
        if (isPlaying) {
            val melody by melodyOfTwinkleTwinkleLittleStar()
            val accompaniment by accompanimentOfTwinkleTwinkleLittleStar()
            Mix {
                InstrumentNote(note = melody, instrument = Instrument.ElectricPiano)
                InstrumentNote(note = accompaniment, instrument = Instrument.SoftPad)
            }
        }
    }
}

@Composable
private fun melodyOfTwinkleTwinkleLittleStar() = produceState(Note(Silence)) {
    val pitch = listOf(
        C5, C5, G5, G5, A5, A5, G5, Silence,
        F5, F5, E5, E5, D5, D5, C5, Silence,
        G5, G5, F5, F5, E5, E5, D5, Silence,
        G5, G5, F5, F5, E5, E5, D5, Silence,
        C5, C5, G5, G5, A5, A5, G5, Silence,
        F5, F5, E5, E5, D5, D5, C5, Silence,
    )
    var index = 0
    while (true) {
        value = Note(pitch[index])
        delay(500L)
        index = (index + 1) % pitch.size
    }
}

@Composable
private fun accompanimentOfTwinkleTwinkleLittleStar() = produceState(Note(Silence)) {
    val pitch = listOf(
        E4, C4, E4, C4, F4, C4, E4, C4,
        D4, G4, C4, G4, F4, G4, E4, G4,
        E4, E4, D4, D4, G4, G4, F4, G4,
        E4, E4, D4, D4, G4, G4, F4, G4,
        E4, C4, E4, C4, F4, C4, E4, C4,
        D4, G4, C4, G4, F4, G4, C4, Silence,
    )
    var index = 0
    while (true) {
        value = Note(pitch[index])
        delay(500L)
        index = (index + 1) % pitch.size
    }
}
