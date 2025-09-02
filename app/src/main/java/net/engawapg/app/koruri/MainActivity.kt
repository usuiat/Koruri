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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
                    composable<Sample.SineWave> {
                        SineWaveScreen()
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
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(R.drawable.koruri),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            val samples = listOf(
                "Sine Wave" to Sample.SineWave,
                "Instruments" to Sample.Instruments,
                "Twinkle Twinkle Little Star" to Sample.Twinkle,
                "Keyboard" to Sample.Keyboard,
                "Synthesizer" to Sample.Synth,
            )

            samples.forEach { (name, sample) ->
                Button(
                    onClick = { onSampleSelect(sample) },
                ) {
                    Text(name)
                }
            }
        }
    }
}

private sealed interface Sample {
    @Serializable
    data object SineWave: Sample
    @Serializable
    data object Instruments: Sample
    @Serializable
    data object Twinkle: Sample
    @Serializable
    data object Keyboard: Sample
    @Serializable
    data object Synth: Sample
}
