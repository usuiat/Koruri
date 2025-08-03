package net.engawapg.app.koruri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import net.engawapg.app.koruri.ui.theme.KoruriTheme
import net.engawapg.lib.koruri.processor.Instrument
import net.engawapg.lib.koruri.processor.InstrumentNote
import net.engawapg.lib.koruri.processor.Note
import net.engawapg.lib.koruri.processor.Pitch.A5
import net.engawapg.lib.koruri.processor.Pitch.C5
import net.engawapg.lib.koruri.processor.Pitch.D5
import net.engawapg.lib.koruri.processor.Pitch.E5
import net.engawapg.lib.koruri.processor.Pitch.F5
import net.engawapg.lib.koruri.processor.Pitch.G5
import net.engawapg.lib.koruri.processor.Pitch.Silence
import net.engawapg.lib.koruri.runKoruri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoruriTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        runKoruri {
            KoruriSample()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun KoruriSample() {
    val note by melodyOfTwinkleTwinkleLittleStar()
    InstrumentNote(note = note, instrument = Instrument.Celesta)
}

@Composable
private fun melodyOfTwinkleTwinkleLittleStar() = produceState(Note(Silence)) {
    val pitch = listOf(
        C5, C5, G5, G5, A5, A5, G5, Silence,
        F5, F5, E5, E5, D5, D5, C5, Silence,
        G5, G5, F5, F5, E5, E5, D5, Silence,
        G5, G5, F5, F5, E5, E5, D5, Silence,
    )
    var index = 0
    while (true) {
        value = Note(pitch[index])
        delay(500L)
        index = (index + 1) % pitch.size
    }
}

