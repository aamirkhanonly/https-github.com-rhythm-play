package com.example.player

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

/**
 * Generates rich musical synthesizer audio files (multi-chord harmonic acoustic/synth arrangements)
 * so every offline song plays vibrant musical audio even when no local mp3 file is imported.
 */
object AudioSynthesizer {

    fun generateMelodicAudioFile(context: Context, songId: String, genre: String, durationSeconds: Int = 180): File {
        val cacheDir = File(context.cacheDir, "synth_tracks")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val file = File(cacheDir, "track_${songId}.wav")
        if (file.exists() && file.length() > 44) {
            return file
        }

        val sampleRate = 22050
        val numSamples = sampleRate * durationSeconds
        val buffer = ShortArray(numSamples)

        // Musical scales based on genre
        val baseFreqs = when (genre.lowercase()) {
            "rock" -> listOf(110.0, 146.83, 164.81, 196.0, 220.0) // A minor / power chords
            "jazz" -> listOf(130.81, 164.81, 196.0, 246.94, 293.66, 349.23) // Cmaj7/Am7
            "classic", "classical" -> listOf(261.63, 329.63, 392.0, 523.25, 659.25) // C Major arpeggios
            "concert", "hall" -> listOf(146.83, 220.0, 293.66, 369.99, 440.0) // D Major spatial
            else -> listOf(220.0, 261.63, 329.63, 392.0, 440.0, 523.25) // Pop / Melody
        }

        val beatDuration = sampleRate / 2 // 120 BPM = 0.5s per beat
        val barDuration = beatDuration * 4

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val currentBeat = (i / beatDuration) % baseFreqs.size
            val baseFreq = baseFreqs[currentBeat]

            // Chord structure (Root + 3rd + 5th)
            val freq1 = baseFreq
            val freq2 = baseFreq * 1.25 // Major/minor third approx
            val freq3 = baseFreq * 1.5  // Fifth

            // Rhythm envelope (attack & decay pulse)
            val posInBeat = (i % beatDuration).toDouble() / beatDuration
            val envelope = (1.0 - posInBeat) * (1.0 - posInBeat)

            // Bassline rhythm
            val bassFreq = baseFreq / 2.0
            val bassWave = sin(2.0 * Math.PI * bassFreq * t) * 0.4

            // Melody wave with subtle chorus vibrato
            val vibrato = sin(2.0 * Math.PI * 4.5 * t) * 2.0
            val melodyWave = (
                sin(2.0 * Math.PI * (freq1 + vibrato) * t) * 0.35 +
                sin(2.0 * Math.PI * freq2 * t) * 0.2 +
                sin(2.0 * Math.PI * freq3 * t) * 0.15
            ) * envelope

            // Drum/Percussion click on beat start
            val beatClick = if (i % beatDuration < 800) {
                (Math.random() * 2.0 - 1.0) * (1.0 - (i % beatDuration) / 800.0) * 0.25
            } else 0.0

            val mixed = ((melodyWave + bassWave + beatClick) * 0.75).coerceIn(-1.0, 1.0)
            buffer[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }

        writeWavFile(file, sampleRate, buffer)
        return file
    }

    private fun writeWavFile(file: File, sampleRate: Int, shortBuffer: ShortArray) {
        val totalAudioLen = shortBuffer.size * 2
        val totalDataLen = totalAudioLen + 36
        val channels = 1
        val byteRate = sampleRate * channels * 2

        val header = ByteArray(44)
        val byteBuffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        byteBuffer.put("RIFF".toByteArray())
        byteBuffer.putInt(totalDataLen)
        byteBuffer.put("WAVE".toByteArray())

        // Format chunk
        byteBuffer.put("fmt ".toByteArray())
        byteBuffer.putInt(16) // Subchunk1Size (16 for PCM)
        byteBuffer.putShort(1.toShort()) // AudioFormat (1 for PCM)
        byteBuffer.putShort(channels.toShort())
        byteBuffer.putInt(sampleRate)
        byteBuffer.putInt(byteRate)
        byteBuffer.putShort((channels * 2).toShort()) // BlockAlign
        byteBuffer.putShort(16.toShort()) // BitsPerSample

        // Data chunk
        byteBuffer.put("data".toByteArray())
        byteBuffer.putInt(totalAudioLen)

        FileOutputStream(file).use { fos ->
            fos.write(header)
            val pcmBytes = ByteArray(shortBuffer.size * 2)
            ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortBuffer)
            fos.write(pcmBytes)
        }
    }
}
