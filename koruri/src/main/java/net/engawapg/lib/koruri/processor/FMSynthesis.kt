package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FMSynthesis(
    carrierFrequency: Float,
    modulator: FMSynthesisModulator,
) {
    Block(
        signalProcessor = FMSynthesiser(carrierFrequency, modulator)
    )
}

data class FMSynthesisModulator(
    val ratio: Float,
    val index: Float,
)

private const val PIx2 = PI.toFloat() * 2.0f
private const val SAMPLE_RATE = 48000


private class FMSynthesiser(
    private val carrierFrequency: Float,
    private val modulator: FMSynthesisModulator,
) : SignalProcessor {
    private var carrierPhase = 0.0f
    private var modulatorPhase = 0.0f

    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        val numSamples = input.size / 2
        val output = FloatArray(numSamples * 2)
        if (carrierFrequency == 0f) {
            carrierPhase = 0.0f
            modulatorPhase = 0.0f
            return output
        }

        val phaseDelta = PIx2 * carrierFrequency / SAMPLE_RATE
        val modulatorPhaseDelta = PIx2 * carrierFrequency * modulator.ratio / SAMPLE_RATE
        for (i in 0 until numSamples) {
            val sample = sin(carrierPhase + modulator.index * sin(modulatorPhase))
            output[i * 2] = sample
            output[i * 2 + 1] = sample

            carrierPhase += phaseDelta
            // phase を 2π の範囲内に正規化
            if (carrierPhase >= PIx2) {
                carrierPhase -= PIx2
            }

            modulatorPhase += modulatorPhaseDelta
            if (modulatorPhase >= PIx2) {
                modulatorPhase -= PIx2
            }
        }

        return output
    }
}