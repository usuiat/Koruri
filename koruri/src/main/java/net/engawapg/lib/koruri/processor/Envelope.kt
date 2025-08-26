package net.engawapg.lib.koruri.processor

import androidx.compose.runtime.Composable
import net.engawapg.lib.koruri.KoruriNode
import net.engawapg.lib.koruri.audio.Block

@Composable
fun Envelope(
    attack: () -> Float,
    decay: () -> Float,
    sustain: () -> Float,
    release: () -> Float,
) {
    Block(
        signalProcessor = EnvelopeGenerator(attack, decay, sustain, release),
    )
}

@Composable
fun Envelope(
    attack: Float,
    decay: Float,
    sustain: Float,
    release: Float,
) {
    Block(
        signalProcessor = EnvelopeGenerator({ attack }, { decay }, { sustain }, { release }),
    )
}

private class EnvelopeGenerator(
    private val attack: () -> Float,
    private val decay: () -> Float,
    private val sustain: () -> Float,
    private val release: () -> Float
) : SignalProcessor {

    private enum class Phase {
        Attack, Decay, Sustain, Release, Idle
    }

    private var phase = Phase.Idle
    private var envelope = 0f
    private var phaseTime = 0f
    private var isGateOn = false
    private var releaseLevel = 0f
    private val sampleRate = 48000

    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        val output = FloatArray(input.size)
        val deltaTime = 1f / sampleRate

        for (i in input.indices) {
            // Gate detection (simple trigger based on input level)
            val currentGate = input[i] != 0f

            if (currentGate && !isGateOn) {
                // Gate on - start attack phase
                phase = Phase.Attack
                phaseTime = 0f
                isGateOn = true
            } else if (!currentGate && isGateOn) {
                // Gate off - start release phase
                phase = Phase.Release
                phaseTime = 0f
                releaseLevel = envelope
                isGateOn = false
            }

            // Process envelope
            when (phase) {
                Phase.Attack -> {
                    val attackTime = attack()
                    if (attackTime > 0f) {
                        envelope = phaseTime / attackTime
                        if (envelope >= 1f) {
                            envelope = 1f
                            phase = Phase.Decay
                            phaseTime = 0f
                        }
                    } else {
                        envelope = 1f
                        phase = Phase.Decay
                        phaseTime = 0f
                    }
                }

                Phase.Decay -> {
                    val decayTime = decay()
                    val sustainLevel = sustain()
                    if (decayTime > 0f) {
                        envelope = 1f - (1f - sustainLevel) * (phaseTime / decayTime)
                        if (envelope <= sustainLevel) {
                            envelope = sustainLevel
                            phase = Phase.Sustain
                            phaseTime = 0f
                        }
                    } else {
                        envelope = sustainLevel
                        phase = Phase.Sustain
                        phaseTime = 0f
                    }
                }

                Phase.Sustain -> {
                    envelope = sustain()
                }

                Phase.Release -> {
                    val releaseTime = release()
                    if (releaseTime > 0f) {
                        envelope = releaseLevel * (1f - phaseTime / releaseTime)
                        if (envelope <= 0f) {
                            envelope = 0f
                            phase = Phase.Idle
                            phaseTime = 0f
                        }
                    } else {
                        envelope = 0f
                        phase = Phase.Idle
                        phaseTime = 0f
                    }
                }

                Phase.Idle -> {
                    envelope = 0f
                }
            }

            output[i] = input[i] * envelope
            phaseTime += deltaTime
        }

        return output
    }
}
