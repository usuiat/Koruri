package net.engawapg.lib.koruri.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

internal class KoruriAudio {
    companion object {
        private const val SAMPLE_RATE = 48000
        private const val CHANNEL_OUT = AudioFormat.CHANNEL_OUT_STEREO
        private const val ENCODING = AudioFormat.ENCODING_PCM_FLOAT
    }

    private var audioTrack: AudioTrack? = null

    fun start() {
        val format = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(ENCODING)
            .setChannelMask(CHANNEL_OUT)
            .build()

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT, ENCODING)

        audioTrack = AudioTrack.Builder()
            .setAudioFormat(format)
            .setAudioAttributes(attributes)
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
            .apply { play() }
    }

    fun write(data: FloatArray, size: Int) {
        audioTrack?.write(data, 0, size, AudioTrack.WRITE_BLOCKING)
    }
}