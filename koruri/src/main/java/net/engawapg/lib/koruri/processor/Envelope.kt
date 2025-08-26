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
    content: @Composable () -> Unit
) {
    Block(
        signalProcessor = EnvelopeGenerator(attack, decay, sustain, release),
        content = content
    )
}

@Composable
fun Envelope(
    attack: Float,
    decay: Float,
    sustain: Float,
    release: Float,
    content: @Composable () -> Unit
) {
    Block(
        signalProcessor = EnvelopeGenerator({ attack }, { decay }, { sustain }, { release }),
        content = content
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
    private var releaseLevel = 0f
    private val sampleRate = 48000
    private var wasInputActive = false
    private var storedSignal = 0f

    override fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray {
        val output = FloatArray(input.size)
        val deltaTime = 1f / sampleRate

        // Get child signal first
        val childOutput = if (childrenNode.isNotEmpty()) {
            childrenNode[0].process(input)
        } else {
            FloatArray(input.size) // Generate silence if no child
        }

        for (i in input.indices) {
            // Gate detection based on child signal
            val hasChildSignal = childOutput[i] != 0f

            if (hasChildSignal && !wasInputActive) {
                // Gate on - start attack phase
                phase = Phase.Attack
                phaseTime = 0f
                wasInputActive = true
                storedSignal = childOutput[i] // Store the signal level
            } else if (!hasChildSignal && wasInputActive && phase != Phase.Release) {
                // Gate off - start release phase
                phase = Phase.Release
                phaseTime = 0f
                releaseLevel = envelope
                wasInputActive = false
                // Keep storedSignal for release phase
            }

            // Update stored signal while input is active
            if (hasChildSignal) {
                storedSignal = childOutput[i]
            }

            // Process envelope phases
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

            // Apply envelope - use stored signal during release phase
            if (phase == Phase.Release) {
                output[i] = storedSignal * envelope
            } else {
                output[i] = childOutput[i] * envelope
            }

            phaseTime += deltaTime
        }

        return output
    }
}
