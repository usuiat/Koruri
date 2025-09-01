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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Instrument
import net.engawapg.lib.koruri.processor.InstrumentNote
import net.engawapg.lib.koruri.processor.Note
import net.engawapg.lib.koruri.processor.Pitch

@Composable
internal fun InstrumentsScreen(
    modifier: Modifier = Modifier
) {
    var instrument by remember { mutableStateOf<Instrument?>(null) }
    var note by remember { mutableStateOf(Note.Silence) }

    val instruments = mapOf(
        "Electric Piano" to Instrument.ElectricPiano,
        "Celesta" to Instrument.Celesta,
        "Soft Pad" to Instrument.SoftPad,
        "Crystal Bell" to Instrument.CrystalBell
    )

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            for ((name, inst) in instruments) {
                Button(
                    onClick = {
                        instrument = inst
                        note = Note(Pitch.A5)
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(name)
                }
            }
        }
    }

    KoruriContent {
        instrument?.let { instrument ->
            InstrumentNote(
                note = note,
                instrument = instrument
            )
        }
    }
}