package com.example.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.*
import java.util.Random
import kotlin.math.*

/**
 * High-performance sound effects and haptic feedback engine for Desk Pet.
 * Features:
 * - Purr haptics (rhythmic pulsing replicating a soothing cat's purr)
 * - Tactile clicks on drawer drags and navigation
 * - Snack crunch sounds (croissants, cookies, berries)
 * - Wooden clink sounds when tapping furniture
 * - Crystal coin chime sounds when buying or earning coins
 */
object DeskPetAudioHaptics {

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Pre-synthesized PCM buffers for instant zero-latency playback
    private val sampleRate = 22050
    private var crunchAudioData: ShortArray? = null
    private var woodTapAudioData: ShortArray? = null
    private var coinChimeAudioData: ShortArray? = null
    private var purrAudioData: ShortArray? = null

    init {
        audioScope.launch {
            crunchAudioData = generateCrunchPcm()
            woodTapAudioData = generateWoodTapPcm()
            coinChimeAudioData = generateCoinChimePcm()
            purrAudioData = generatePurrPcm()
        }
    }

    // ==========================================
    // HAPTICS ENGINE
    // ==========================================

    /**
     * Performs a warm rhythmic vibration pattern mimicking a purring pet.
     */
    fun performPurrHaptic(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Rhythmic purr pulses: [wait, purr, rest, purr, rest, purr, rest]
                val timings = longArrayOf(0, 45, 65, 50, 75, 40, 60, 35)
                val amplitudes = intArrayOf(0, 75, 0, 95, 0, 85, 0, 60)
                if (vibrator.hasAmplitudeControl()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 45, 65, 50, 75, 40), -1)
            }
        } catch (_: Exception) {
            // Graceful fallback on devices without vibration hardware
        }
    }

    /**
     * Subtle tactile tick when moving drawers or interacting with furniture.
     */
    fun performTactileTick(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(12, 60))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(12)
            }
        } catch (_: Exception) {}
    }

    /**
     * Crisp double pulse when coins are earned or an item is purchased.
     */
    fun performCoinHaptic(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 20, 40, 25)
                val amplitudes = intArrayOf(0, 110, 0, 140)
                if (vibrator.hasAmplitudeControl()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 20, 40, 25), -1)
            }
        } catch (_: Exception) {}
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // ==========================================
    // SOUND EFFECTS (PCM SYNTHESIS)
    // ==========================================

    fun playCrunchSound() {
        audioScope.launch {
            val pcm = crunchAudioData ?: generateCrunchPcm().also { crunchAudioData = it }
            playPcm(pcm)
        }
    }

    fun playWoodTapSound() {
        audioScope.launch {
            val pcm = woodTapAudioData ?: generateWoodTapPcm().also { woodTapAudioData = it }
            playPcm(pcm)
        }
    }

    fun playCoinChimeSound() {
        audioScope.launch {
            val pcm = coinChimeAudioData ?: generateCoinChimePcm().also { coinChimeAudioData = it }
            playPcm(pcm)
        }
    }

    fun playPurrSound() {
        audioScope.launch {
            val pcm = purrAudioData ?: generatePurrPcm().also { purrAudioData = it }
            playPcm(pcm)
        }
    }

    private fun playPcm(pcm: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, pcm.size)
            track.play()

            // Release after playback finishes
            val durationMs = (pcm.size * 1000L) / sampleRate + 150L
            audioScope.launch {
                delay(durationMs)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    // --- Sound Synthesis Generators ---

    /**
     * Synthesizes a crisp, appetizing snack crunch (double bite of flaky croissant/biscuit).
     */
    private fun generateCrunchPcm(): ShortArray {
        val totalSamples = (sampleRate * 0.28).toInt()
        val buffer = ShortArray(totalSamples)
        val random = Random()
        var filter = 0.0

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            // Two distinct crunch peaks at t=0.0 and t=0.12
            val env1 = if (t < 0.11) exp(-t * 38.0) else 0.0
            val env2 = if (t >= 0.11) exp(-(t - 0.11) * 32.0) else 0.0
            val env = env1 + env2 * 0.9

            // Filtered white noise with subtle mid-frequency crackle
            val white = random.nextDouble() * 2.0 - 1.0
            filter = 0.72 * filter + 0.28 * white
            val pop = if (random.nextInt(35) == 0) (random.nextDouble() * 2.0 - 1.0) * 0.6 else 0.0

            val sample = (filter * 0.65 + pop) * env * 32767.0 * 0.65
            buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    /**
     * Synthesizes a warm, resonant wooden block tap for furniture taps.
     */
    private fun generateWoodTapPcm(): ShortArray {
        val totalSamples = (sampleRate * 0.18).toInt()
        val buffer = ShortArray(totalSamples)

        val freq1 = 780.0
        val freq2 = 1240.0
        val freq3 = 410.0

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 32.0)
            val tone = sin(2.0 * Math.PI * freq1 * t) * 0.55 +
                       sin(2.0 * Math.PI * freq2 * t) * 0.25 +
                       sin(2.0 * Math.PI * freq3 * t) * 0.20
            val sample = tone * decay * 32767.0 * 0.7
            buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    /**
     * Synthesizes a crystalline two-tone ascending coin chime (C6 -> E6).
     */
    private fun generateCoinChimePcm(): ShortArray {
        val totalSamples = (sampleRate * 0.45).toInt()
        val buffer = ShortArray(totalSamples)

        val note1Freq = 1046.50 // C6
        val note2Freq = 1318.51 // E6
        val switchSample = (sampleRate * 0.10).toInt()

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val sampleVal: Double

            if (i < switchSample) {
                // First ding
                val decay1 = exp(-t * 22.0)
                val overtone = sin(2.0 * Math.PI * note1Freq * 2.0 * t) * 0.22
                sampleVal = (sin(2.0 * Math.PI * note1Freq * t) + overtone) * decay1 * 0.7
            } else {
                // Second higher ding
                val t2 = (i - switchSample).toDouble() / sampleRate
                val decay2 = exp(-t2 * 11.0)
                val overtone = sin(2.0 * Math.PI * note2Freq * 2.0 * t2) * 0.25
                val sparkle = sin(2.0 * Math.PI * note2Freq * 3.0 * t2) * 0.12
                sampleVal = (sin(2.0 * Math.PI * note2Freq * t2) + overtone + sparkle) * decay2 * 0.85
            }

            buffer[i] = (sampleVal * 32767.0 * 0.65).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    /**
     * Synthesizes a gentle feline purring rumble with rhythmic breath.
     */
    private fun generatePurrPcm(): ShortArray {
        val totalSamples = (sampleRate * 1.2).toInt()
        val buffer = ShortArray(totalSamples)
        val random = Random()
        var noiseFilter = 0.0

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            // 26 Hz purr vibration modulation
            val purrCarrier = sin(2.0 * Math.PI * 26.0 * t)
            // 1.8 Hz respiration envelope
            val breath = (sin(2.0 * Math.PI * 1.8 * t) + 1.0) * 0.5

            val white = random.nextDouble() * 2.0 - 1.0
            noiseFilter = 0.90 * noiseFilter + 0.10 * white

            val tone = (purrCarrier * 0.7 + noiseFilter * 0.3) * (0.3 + 0.7 * breath)
            val sample = tone * 32767.0 * 0.45
            buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }
}

