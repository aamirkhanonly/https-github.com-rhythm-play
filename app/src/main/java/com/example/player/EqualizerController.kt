package com.example.player

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.util.Log
import com.example.model.EqualizerPreset
import com.example.model.EqualizerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EqualizerController {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var presetReverb: PresetReverb? = null

    private val _settings = MutableStateFlow(EqualizerSettings())
    val settings: StateFlow<EqualizerSettings> = _settings.asStateFlow()

    private var currentSessionId: Int = 0

    fun bindAudioSession(sessionId: Int) {
        if (sessionId == 0 || sessionId == currentSessionId) return
        currentSessionId = sessionId
        releaseEffects()

        try {
            equalizer = Equalizer(0, sessionId).apply {
                enabled = _settings.value.isEnabled
            }
        } catch (e: Exception) {
            Log.e("EqualizerController", "Error init Equalizer", e)
        }

        try {
            bassBoost = BassBoost(0, sessionId).apply {
                enabled = _settings.value.isEnabled
                if (strengthSupported) {
                    setStrength(_settings.value.bassBoost.toShort())
                }
            }
        } catch (e: Exception) {
            Log.e("EqualizerController", "Error init BassBoost", e)
        }

        try {
            virtualizer = Virtualizer(0, sessionId).apply {
                enabled = _settings.value.isEnabled
                if (strengthSupported) {
                    setStrength(_settings.value.virtualizer.toShort())
                }
            }
        } catch (e: Exception) {
            Log.e("EqualizerController", "Error init Virtualizer", e)
        }

        try {
            presetReverb = PresetReverb(0, sessionId).apply {
                enabled = _settings.value.isEnabled
                preset = _settings.value.reverbPreset
            }
        } catch (e: Exception) {
            Log.e("EqualizerController", "Error init PresetReverb", e)
        }

        applyCurrentSettings()
    }

    fun setEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(isEnabled = enabled)
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
            virtualizer?.enabled = enabled
            presetReverb?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyPreset(preset: EqualizerPreset) {
        val bands = getPresetBandLevels(preset)
        val reverb = when (preset) {
            EqualizerPreset.HALL -> PresetReverb.PRESET_LARGEROOM
            EqualizerPreset.CONCERT -> PresetReverb.PRESET_LARGEHALL
            else -> PresetReverb.PRESET_NONE
        }
        val bb = when (preset) {
            EqualizerPreset.ROCK -> 650
            EqualizerPreset.POP -> 500
            EqualizerPreset.CONCERT -> 450
            else -> 200
        }
        val virt = when (preset) {
            EqualizerPreset.CONCERT, EqualizerPreset.HALL -> 700
            EqualizerPreset.JAZZ -> 400
            else -> 150
        }

        _settings.value = _settings.value.copy(
            currentPreset = preset,
            bandLevels = bands,
            bassBoost = bb,
            virtualizer = virt,
            reverbPreset = reverb
        )
        applyCurrentSettings()
    }

    fun setBandLevel(bandIndex: Int, levelPercentage: Int) {
        // levelPercentage: -100 to +100
        val currentBands = _settings.value.bandLevels.toMutableList()
        if (bandIndex in 0 until currentBands.size) {
            currentBands[bandIndex] = levelPercentage
            _settings.value = _settings.value.copy(
                currentPreset = EqualizerPreset.CUSTOM,
                bandLevels = currentBands
            )
            applyCurrentSettings()
        }
    }

    fun setBassBoost(strength: Int) { // 0 to 1000
        _settings.value = _settings.value.copy(
            bassBoost = strength.coerceIn(0, 1000),
            currentPreset = EqualizerPreset.CUSTOM
        )
        try {
            if (bassBoost?.strengthSupported == true) {
                bassBoost?.setStrength(strength.toShort())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVirtualizer(strength: Int) { // 0 to 1000
        _settings.value = _settings.value.copy(
            virtualizer = strength.coerceIn(0, 1000),
            currentPreset = EqualizerPreset.CUSTOM
        )
        try {
            if (virtualizer?.strengthSupported == true) {
                virtualizer?.setStrength(strength.toShort())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setReverbPreset(reverb: Short) {
        _settings.value = _settings.value.copy(
            reverbPreset = reverb,
            currentPreset = EqualizerPreset.CUSTOM
        )
        try {
            presetReverb?.preset = reverb
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyCurrentSettings() {
        val st = _settings.value
        try {
            equalizer?.let { eq ->
                val numBands = eq.numberOfBands.toInt()
                val minLevel = eq.bandLevelRange[0] // e.g. -1500 mB
                val maxLevel = eq.bandLevelRange[1] // e.g. +1500 mB
                val range = maxLevel - minLevel

                for (i in 0 until minOf(numBands, st.bandLevels.size)) {
                    val percent = st.bandLevels[i] // -100 to 100
                    val targetLevel = (minLevel + ((percent + 100) / 200.0 * range)).toInt().toShort()
                    eq.setBandLevel(i.toShort(), targetLevel)
                }
            }

            if (bassBoost?.strengthSupported == true) {
                bassBoost?.setStrength(st.bassBoost.toShort())
            }

            if (virtualizer?.strengthSupported == true) {
                virtualizer?.setStrength(st.virtualizer.toShort())
            }

            presetReverb?.preset = st.reverbPreset
        } catch (e: Exception) {
            Log.e("EqualizerController", "Error applying settings", e)
        }
    }

    private fun getPresetBandLevels(preset: EqualizerPreset): List<Int> {
        return when (preset) {
            EqualizerPreset.NORMAL -> listOf(0, 0, 0, 0, 0)
            EqualizerPreset.POP -> listOf(15, 30, 45, 20, -10)
            EqualizerPreset.CLASSIC -> listOf(35, 25, -15, 20, 30)
            EqualizerPreset.JAZZ -> listOf(25, 10, -10, 20, 35)
            EqualizerPreset.ROCK -> listOf(50, 25, -10, 30, 55)
            EqualizerPreset.HALL -> listOf(30, 20, 10, 40, 60)
            EqualizerPreset.CONCERT -> listOf(45, 30, 15, 35, 50)
            EqualizerPreset.CUSTOM -> _settings.value.bandLevels
        }
    }

    fun releaseEffects() {
        try { equalizer?.release() } catch (e: Exception) {}
        try { bassBoost?.release() } catch (e: Exception) {}
        try { virtualizer?.release() } catch (e: Exception) {}
        try { presetReverb?.release() } catch (e: Exception) {}
        equalizer = null
        bassBoost = null
        virtualizer = null
        presetReverb = null
    }
}
