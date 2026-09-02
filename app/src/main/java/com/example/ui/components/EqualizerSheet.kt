package com.example.ui.components

import android.media.audiofx.PresetReverb
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EqualizerPreset
import com.example.model.EqualizerSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerSheet(
    settings: EqualizerSettings,
    onEnabledChange: (Boolean) -> Unit,
    onPresetSelect: (EqualizerPreset) -> Unit,
    onBandLevelChange: (bandIndex: Int, level: Int) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onReverbChange: (Short) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val frequencyLabels = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("equalizer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(scrollState)
        ) {
            // Title & Master Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Audio Equalizer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Acoustic Tuning & Spatial Sound",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = settings.isEnabled,
                    onCheckedChange = onEnabledChange,
                    modifier = Modifier.testTag("equalizer_master_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Genre Presets Horizontal Row
            Text(
                text = "Genre Presets",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EqualizerPreset.values().forEach { preset ->
                    val isSelected = settings.currentPreset == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (settings.isEnabled) onPresetSelect(preset)
                        },
                        label = {
                            Text(
                                text = preset.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        enabled = settings.isEnabled,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("preset_${preset.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5-Band Equalizer Frequency Sliders Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "5-Band Frequency Response",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (settings.isEnabled) "ACTIVE" else "BYPASS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5 Sliders Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        settings.bandLevels.forEachIndexed { index, level ->
                            val freqLabel = frequencyLabels.getOrElse(index) { "Band $index" }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${if (level > 0) "+" else ""}${level / 10} dB",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Slider
                                Slider(
                                    value = level.toFloat(),
                                    onValueChange = { onBandLevelChange(index, it.toInt()) },
                                    valueRange = -100f..100f,
                                    enabled = settings.isEnabled,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.testTag("eq_band_slider_$index")
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = freqLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bass Boost & Virtualizer 3D Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bass Boost Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bass Boost",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${settings.bassBoost / 10}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Slider(
                            value = settings.bassBoost.toFloat(),
                            onValueChange = { onBassBoostChange(it.toInt()) },
                            valueRange = 0f..1000f,
                            enabled = settings.isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("bass_boost_slider")
                        )
                    }
                }

                // Virtualizer / 3D Surround Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.SurroundSound,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "3D Surround",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${settings.virtualizer / 10}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Slider(
                            value = settings.virtualizer.toFloat(),
                            onValueChange = { onVirtualizerChange(it.toInt()) },
                            valueRange = 0f..1000f,
                            enabled = settings.isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("virtualizer_slider")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reverb Effect selector
            Text(
                text = "Acoustic Reverb / Hall Environment",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val reverbOptions = listOf(
                PresetReverb.PRESET_NONE to "None (Dry)",
                PresetReverb.PRESET_SMALLROOM to "Studio Room",
                PresetReverb.PRESET_MEDIUMHALL to "Acoustic Hall",
                PresetReverb.PRESET_LARGEHALL to "Grand Concert",
                PresetReverb.PRESET_PLATE to "Vintage Plate"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reverbOptions.forEach { (reverbCode, name) ->
                    val isSelected = settings.reverbPreset == reverbCode
                    FilterChip(
                        selected = isSelected,
                        onClick = { if (settings.isEnabled) onReverbChange(reverbCode) },
                        label = { Text(name) },
                        enabled = settings.isEnabled,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }
    }
}
