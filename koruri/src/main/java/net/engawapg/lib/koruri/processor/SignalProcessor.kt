package net.engawapg.lib.koruri.processor

import net.engawapg.lib.koruri.KoruriNode

/**
 * Interface for audio signal processors.
 *
 * @param input The input audio data.
 * @param childrenNode The child nodes to process.
 * @return The processed audio data.
 */
internal interface SignalProcessor {
    fun process(input: FloatArray, childrenNode: List<KoruriNode>): FloatArray
}