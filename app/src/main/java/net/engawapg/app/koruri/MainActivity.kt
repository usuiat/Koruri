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
import net.engawapg.lib.koruri.processor.FMSynthesis
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
    val frequency by produceState(0f) {
        val C5 = 523.251f // C5 frequency
        val D5 = 587.330f // D5 frequency
        val E5 = 659.255f // E5 frequency
        val F5 = 698.456f // F5 frequency
        val G5 = 783.991f // G5 frequency
        val A5 = 880.000f // A5 frequency
        val B5 = 987.767f // B5 frequency

        val tones = listOf(
            C5, C5, G5, G5, A5, A5, G5, 0f,
            F5, F5, E5, E5, D5, D5, C5, 0f,
            G5, G5, F5, F5, E5, E5, D5, 0f,
            G5, G5, F5, F5, E5, E5, D5, 0f,
        )
        var index = 0
        while (true) {
            value = tones[index]
            delay(500L)
            value = 0f
            delay(50L)
            index = (index + 1) % tones.size
        }
    }
    FMSynthesis(
        carrierFrequency = frequency,
        modulatorRatio = 4.0f,
        modulationIndex = 1.5f,
    )
}

