package net.engawapg.app.koruri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.engawapg.app.koruri.ui.theme.KoruriTheme
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Chain
import net.engawapg.lib.koruri.processor.Envelope
import net.engawapg.lib.koruri.processor.LowPassFilter
import net.engawapg.lib.koruri.processor.SquareWave

@Composable
internal fun SynthScreen(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    val baseFrequency = 65.41f // C2の音に変更（C3の半分の周波数）
    var bpm by remember { mutableFloatStateOf(120f) }
    var pulseWidth by remember { mutableFloatStateOf(0f) }

    var gate by remember { mutableStateOf(false) }
    var frequency by remember { mutableFloatStateOf(65.41f) }

    // エンベロープ値を計算する関数
    var envelopeValue by remember { mutableFloatStateOf(0f) }

    // テクノらしいアルペジオパターン（ルート、オクターブ、5度、短3度、ルート高音域）
    val arpeggioPattern = listOf(
        1.0f,    // C
        1.5f,    // G
        1.681f,  // Am
        1.26f,   // Em
        1.334f,  // F
        1.0f,    // C
        1.334f,  // F
        1.5f     // G
    )
    var currentStep by remember { mutableIntStateOf(0) }

    // ADSR parameters - よりテクノらしい設定
    var attack by remember { mutableFloatStateOf(0.01f) } // 非常に短いアタック
    var decay by remember { mutableFloatStateOf(0.15f) }  // 短いディケイ
    var sustain by remember { mutableFloatStateOf(0.4f) } // 低めのサスティン
    var release by remember { mutableFloatStateOf(0.01f) } // 短いリリース

    // LPF parameters - よりアグレッシブな初期設定
    var cutoff by remember { mutableFloatStateOf(20000f) }  // 少し高めのカットオフ
    var resonance by remember { mutableFloatStateOf(0.1f) }  // 高めのレゾナンス

    // Pulse Width Modulation parameters
    var basePulseWidth by remember { mutableFloatStateOf(0f) }      // ベースのPulse Width
    var pwmAmount by remember { mutableFloatStateOf(0.0f) }         // モジュレーション量

    // エンベロープジェネレーターの状態
    var envelopePhase by remember { mutableStateOf("Idle") }
    var envelopeTime by remember { mutableFloatStateOf(0f) }
    var releaseLevel by remember { mutableFloatStateOf(0f) }
    var previousGate by remember { mutableStateOf(false) }

    // エンベロープ値をリアルタイムで更新
    LaunchedEffect(gate, attack, decay, sustain, release) {
        while (true) {
            val currentGate = gate
            val deltaTime = 0.016f // 約60FPS

            // ゲート状態の変化を検出
            if (currentGate && !previousGate) {
                // ゲートオン - アタックフェーズ開始
                envelopePhase = "Attack"
                envelopeTime = 0f
                previousGate = true
            } else if (!currentGate && previousGate) {
                // ゲートオフ - リリースフェーズ開始
                if (envelopePhase != "Idle") {
                    envelopePhase = "Release"
                    envelopeTime = 0f
                    releaseLevel = envelopeValue
                }
                previousGate = false
            }

            // エンベロープ値を計算
            when (envelopePhase) {
                "Attack" -> {
                    envelopeValue = envelopeTime / attack
                    envelopeTime += deltaTime
                    if (envelopeTime >= attack) {
                        envelopePhase = "Decay"
                        envelopeTime = 0f
                    }
                }
                "Decay" -> {
                    val decayProgress = envelopeTime / decay
                    envelopeValue = 1f - decayProgress * (1f - sustain)
                    envelopeTime += deltaTime
                    if (envelopeTime >= decay) {
                        envelopePhase = "Sustain"
                        envelopeValue = sustain
                    }
                }
                "Sustain" -> {
                    envelopeValue = sustain
                }
                "Release" -> {
                    val releaseProgress = envelopeTime / release
                    envelopeValue = releaseLevel * (1f - releaseProgress)
                    envelopeTime += deltaTime
                    if (envelopeTime >= release) {
                        envelopePhase = "Idle"
                        envelopeValue = 0f
                    }
                }
                else -> {
                    envelopeValue = 0f
                }
            }

            // Pulse Widthをモジュレート
            pulseWidth = basePulseWidth + (envelopeValue * pwmAmount * 2f - pwmAmount)
            pulseWidth = pulseWidth.coerceIn(-1f, 1f) // 範囲制限

            delay(16) // 約60FPS
        }
    }

    LaunchedEffect(isPlaying, bpm) {
        if (isPlaying) {
            if (bpm == 0f) {
                // BPM=0なら最初の音を常時再生
                frequency = baseFrequency * arpeggioPattern[0]
                gate = true
            } else {
                // アルペジオパターンをループ
                while (true) {
                    val beatDuration = (60000 / bpm).toLong() // BPMからミリ秒計算
                    val gateDuration = (beatDuration * 0.6).toLong() // 拍の60%をゲートオン（よりパンチのある音）

                    // 現在のステップに対応する周波数を設定
                    frequency = baseFrequency * arpeggioPattern[currentStep]
                    gate = true
                    delay(gateDuration)
                    gate = false
                    delay(beatDuration - gateDuration)

                    // 次のステップに進む
                    currentStep = (currentStep + 1) % arpeggioPattern.size
                }
            }
        } else {
            gate = false
        }
    }


    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Play/Stop button
            Button(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (isPlaying) "Stop" else "Play")
            }


//            // BPM control
//            Card(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = "BPM (Beats Per Minute)",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Text("${bpm.toInt()} BPM")
//                    Slider(
//                        value = bpm,
//                        onValueChange = { bpm = it },
//                        valueRange = 0f..240f,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }

            // ADSR controls
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ADSR Envelope",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Attack
                    Text("Attack: ${"%.2f".format(attack)}s")
                    Slider(
                        value = attack,
                        onValueChange = { attack = it },
                        valueRange = 0.01f..2.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Decay
                    Text("Decay: ${"%.2f".format(decay)}s")
                    Slider(
                        value = decay,
                        onValueChange = { decay = it },
                        valueRange = 0.01f..2.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Sustain
                    Text("Sustain: ${"%.2f".format(sustain)}")
                    Slider(
                        value = sustain,
                        onValueChange = { sustain = it },
                        valueRange = 0.0f..1.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Release
                    Text("Release: ${"%.2f".format(release)}s")
                    Slider(
                        value = release,
                        onValueChange = { release = it },
                        valueRange = 0.01f..3.0f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Pulse Width Modulation controls
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Pulse Width Modulation",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Base Pulse Width
                    Text("Base Pulse Width: ${"%.2f".format(basePulseWidth)}")
                    Slider(
                        value = basePulseWidth,
                        onValueChange = { basePulseWidth = it },
                        valueRange = -1f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // PWM Amount
                    Text("PWM Amount: ${"%.2f".format(pwmAmount)}")
                    Slider(
                        value = pwmAmount,
                        onValueChange = { pwmAmount = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Current modulated pulse width display
                    Text(
                        text = "Current PW: ${"%.2f".format(pulseWidth)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // LPF controls
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Low Pass Filter",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Cutoff
                    Text("Cutoff: ${"%.1f".format(cutoff)} Hz")
                    Slider(
                        value = cutoff,
                        onValueChange = { cutoff = it },
                        valueRange = 20f..20000f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Resonance
                    Text("Resonance: ${"%.2f".format(resonance)}")
                    Slider(
                        value = resonance,
                        onValueChange = { resonance = it },
                        valueRange = 0.1f..10f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    KoruriContent {
        Envelope(
            attack = { attack },
            decay = { decay },
            sustain = { sustain },
            release = { release },
            gate = { gate }
        ) {
            Chain {
                SquareWave(
                    frequency = { frequency },
                    pulseWidth = { pulseWidth }
                )
                LowPassFilter(
                    cutoff = { cutoff },
                    resonance = { resonance }
                )
            }
        }
    }
}