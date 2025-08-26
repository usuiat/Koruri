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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import net.engawapg.app.koruri.ui.theme.KoruriTheme

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
                    composable<Sample.Twinkle> {
                        TwinkleScreen()
                    }
                    composable<Sample.Keyboard> {
                        KeyboardScreen()
                    }
                    composable<Sample.Synth> {
                        SynthScreen()
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
            Button(
                onClick = { onSampleSelect(Sample.Twinkle) },
            ) {
                Text("Twinkle Twinkle Little Star")
            }
            Button(
                onClick = { onSampleSelect(Sample.Keyboard) },
            ) {
                Text("Keyboard")
            }
            Button(
                onClick = { onSampleSelect(Sample.Synth) },
            ) {
                Text("Synthesizer")
            }
        }
    }
}

private sealed interface Sample {
    @Serializable
    data object VariableSineWave: Sample
    @Serializable
    data object Instruments: Sample
    @Serializable
    data object Twinkle: Sample
    @Serializable
    data object Keyboard: Sample
    @Serializable
    data object Synth: Sample
}
