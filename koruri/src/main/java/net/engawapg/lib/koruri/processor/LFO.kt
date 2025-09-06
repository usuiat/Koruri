package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.withFrameMillis
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlin.math.sin

/**
 * A composable function that produces a Low-Frequency Oscillator (LFO) value as a [State].
 * The LFO generates a sine wave value that oscillates between -1.0 and 1.0.
 *
 * @param frequency The frequency of the LFO in Hertz (Hz).
 * @param gate When the gate transitions from `false` to `true`, the LFO's phase is reset to 0,
 *             causing the sine wave to start from the beginning.
 * @return A [State] object that holds the current LFO value as a [Float]. The value is updated on every frame.
 */
@Composable
public fun produceLfo(frequency: Float, gate: Boolean): State<Float> {
    val currentGate by rememberUpdatedState(gate)
    val currentFrequency by rememberUpdatedState(frequency)
    return produceState(initialValue = 0f) {
        var phase = 0.0
        var lastTime = System.currentTimeMillis()
        var lastGate = false
        while (true) {
            withFrameMillis { t ->
                if (currentGate && !lastGate) {
                    phase = 0.0
                }
                lastGate = currentGate
                val deltaTime = (t - lastTime) / 1000.0
                lastTime = t
                phase += 2 * Math.PI * currentFrequency * deltaTime
                if (phase >= 2 * Math.PI) {
                    phase -= 2 * Math.PI
                }
                value = sin(phase).toFloat()
            }
        }
    }
}