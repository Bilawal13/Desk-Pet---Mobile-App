package com.example.ui.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import java.util.Random
import kotlin.math.sin

data class SoundscapeItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val tag: String
)

val AVAILABLE_SOUNDSCAPES = listOf(
    SoundscapeItem("rain", "Gentle Rain", "Soft rain against a cozy window", "🌧️", "Deep Sleep"),
    SoundscapeItem("lofi", "Lo-Fi Study Cafe", "Vinyl crackle & warm cozy cafe hum", "☕", "Focus Flow"),
    SoundscapeItem("fire", "Cozy Fireplace", "Warm crackling hearth & amber glow", "🔥", "Warm Rest"),
    SoundscapeItem("wind", "Forest Breeze", "Gentle pine breeze & airy rustle", "🍃", "Nature Calm"),
    SoundscapeItem("purr", "Cozy Purr & Hearth", "Warm rhythmic cat purring & breath", "🐾", "Deep Comfort"),
    SoundscapeItem("ocean", "Starlight Ocean", "Gentle rhythmic ocean tides under stars", "🌊", "Anxiety Ease"),
    SoundscapeItem("night", "Midnight Chimes", "Deep peaceful chimes & serene night", "🌙", "Meditation")
)

class SoothingSoundscapeEngine {
    private var audioTrack: AudioTrack? = null
    private var soundJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Volatile
    private var isPlaying = false
    @Volatile
    private var currentSoundscapeId: String = "rain"
    @Volatile
    private var volumeMultiplier: Float = 0.7f

    fun playSoundscape(soundId: String) {
        stopSoundscape()
        currentSoundscapeId = soundId
        isPlaying = true

        soundJob = scope.launch {
            try {
                val sampleRate = 22050
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = minBufferSize.coerceAtLeast(4096)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()

                val buffer = ShortArray(bufferSize)
                val random = Random()
                var filterState = 0.0
                var phase = 0.0
                val dt = 2.0 * Math.PI / sampleRate

                while (isActive && isPlaying) {
                    val vol = volumeMultiplier.coerceIn(0f, 1f)
                    when (currentSoundscapeId) {
                        "rain" -> {
                            // Pink noise with subtle droplet spikes
                            for (i in buffer.indices) {
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterState = 0.94 * filterState + 0.06 * white
                                val droplet = if (random.nextInt(350) == 0) (random.nextDouble() * 0.4) else 0.0
                                val sample = (filterState * 0.8 + droplet) * 32767.0 * vol * 0.5
                                buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
                            }
                        }
                        "fire" -> {
                            // Warm filtered rumble + occasional crackle pops
                            for (i in buffer.indices) {
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterState = 0.96 * filterState + 0.04 * white
                                val pop = if (random.nextInt(400) == 0) ((random.nextDouble() * 2.0 - 1.0) * 0.7) else 0.0
                                val sample = (filterState * 0.6 + pop) * 32767.0 * vol * 0.5
                                buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
                            }
                        }
                        "ocean" -> {
                            // Undulating wave envelope
                            for (i in buffer.indices) {
                                phase += dt * 0.2 // Slow wave cycle ~0.2Hz
                                val envelope = (sin(phase) + 1.0) * 0.4 + 0.2
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterState = 0.95 * filterState + 0.05 * white
                                val sample = filterState * envelope * 32767.0 * vol * 0.6
                                buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
                            }
                        }
                        "lofi" -> {
                            // Vinyl crackle + warm 120Hz low hum
                            for (i in buffer.indices) {
                                phase += dt * 110.0
                                val hum = sin(phase) * 0.15
                                val crackle = if (random.nextInt(120) == 0) ((random.nextDouble() * 2.0 - 1.0) * 0.35) else 0.0
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterState = 0.92 * filterState + 0.08 * white
                                val sample = (filterState * 0.35 + hum + crackle) * 32767.0 * vol * 0.5
                                buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
                            }
                        }
                        "night" -> {
                            // Soft bell chimes harmonic + subtle cicada night texture
                            for (i in buffer.indices) {
                                phase += dt * 432.0 // Soothing 432Hz tuning
                                val tone = sin(phase) * 0.2
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterState = 0.97 * filterState + 0.03 * white
                                val sample = (filterState * 0.3 + tone) * 32767.0 * vol * 0.4
                                buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
                            }
                        }
                        "purr" -> {
                            // Warm rhythmic cat purr modulation with low frequency carrier
                            for (i in buffer.indices) {
                                phase += dt * 28.0
                                val breath = (sin(phase * (1.6 / 28.0)) + 1.0) * 0.5
                                val purrCarrier = sin(phase)
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterState = 0.92 * filterState + 0.08 * white
                                val sample = (purrCarrier * 0.7 + filterState * 0.3) * (0.35 + 0.65 * breath) * 32767.0 * vol * 0.55
                                buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
                            }
                        }
                        else -> { // "wind" / default
                            // Airy soft breeze
                            for (i in buffer.indices) {
                                phase += dt * 0.4
                                val gust = (sin(phase) + 1.0) * 0.3 + 0.4
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterState = 0.90 * filterState + 0.10 * white
                                val sample = filterState * gust * 32767.0 * vol * 0.45
                                buffer[i] = sample.coerceIn(-32767.0, 32767.0).toInt().toShort()
                            }
                        }
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (e: Throwable) {
                // Ignore audio generation interruption
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (ignored: Throwable) {}
                audioTrack = null
            }
        }
    }

    fun setVolume(vol: Float) {
        volumeMultiplier = vol.coerceIn(0f, 1f)
    }

    fun stopSoundscape() {
        isPlaying = false
        soundJob?.cancel()
        soundJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (ignored: Throwable) {}
        audioTrack = null
    }

    fun release() {
        stopSoundscape()
        scope.cancel()
    }
}
