package net.engawapg.app.koruri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.engawapg.app.koruri.ui.theme.KoruriTheme
import net.engawapg.lib.koruri.processor.Volume
import net.engawapg.lib.koruri.processor.Instrument
import net.engawapg.lib.koruri.processor.InstrumentNote
import net.engawapg.lib.koruri.processor.Mix
import net.engawapg.lib.koruri.processor.Note
import net.engawapg.lib.koruri.processor.Pitch.*
import net.engawapg.lib.koruri.processor.Pitch.A5
import net.engawapg.lib.koruri.processor.Pitch.C5
import net.engawapg.lib.koruri.processor.Pitch.D5
import net.engawapg.lib.koruri.processor.Pitch.E5
import net.engawapg.lib.koruri.processor.Pitch.F5
import net.engawapg.lib.koruri.processor.Pitch.G5
import net.engawapg.lib.koruri.processor.Pitch.Silence

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoruriTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(
                            onSampleSelect = { navController.navigate(it) }
                        )
                    }
                    composable<Sample.VariableSineWave> {
                        VariableSineWaveScreen()
                    }
                    composable<Sample.Instruments> {
                        InstrumentsScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    onSampleSelect: (Sample) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Koruri Samples")

            Button(
                onClick = { onSampleSelect(Sample.VariableSineWave) },
            ) {
                Text("Variable Sine Wave")
            }
            Button(
                onClick = { onSampleSelect(Sample.Instruments) },
            ) {
                Text("Instruments")
            }
        }
    }
}

private sealed interface Sample {
    @Serializable
    data object VariableSineWave: Sample
    @Serializable
    data object Instruments: Sample
}

@Composable
fun KoruriSample() {
    Mix {
        val note by melodyOfTwinkleTwinkleLittleStar()
        InstrumentNote(note = note, instrument = Instrument.ElectricPiano)
        val accompaniment by accompanimentOfTwinkleTwinkleLittleStar()
        InstrumentNote(note = accompaniment, instrument = Instrument.SoftPad)
    }
    Volume(0.5f)
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

