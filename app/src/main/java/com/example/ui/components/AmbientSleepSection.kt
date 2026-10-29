package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DeskPetViewModel
import com.example.ui.sound.AVAILABLE_SOUNDSCAPES
import com.example.ui.theme.*

@Composable
fun AmbientSleepSection(
    viewModel: DeskPetViewModel,
    modifier: Modifier = Modifier
) {
    val isPlaying by viewModel.isSoundscapePlaying.collectAsState()
    val activeSoundId by viewModel.activeSoundscapeId.collectAsState()
    val volume by viewModel.soundscapeVolume.collectAsState()
    val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsState()
    val sleepTimerRemainingSeconds by viewModel.sleepTimerRemainingSeconds.collectAsState()
    val isBedtimeMode by viewModel.isBedtimeModeActive.collectAsState()

    val remMin = sleepTimerRemainingSeconds / 60
    val remSec = sleepTimerRemainingSeconds % 60
    val timerFormatted = "%02d:%02d".format(remMin, remSec)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sleep Mode Card - Justified & Centered with Balanced Margins
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isBedtimeMode) Primary.copy(alpha = 0.14f) else CozyCardBackground,
            border = BorderStroke(1.dp, if (isBedtimeMode) Primary else CozyCardBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isBedtimeMode) Primary else OnSurfaceVariant.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌙", fontSize = 22.sp)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sleep with Companion",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            maxLines = 1
                        )
                        Text(
                            text = if (isBedtimeMode) "Twilight glow active · Sweet dreams! 💤" else "Dims room lighting & cuddles companion",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = isBedtimeMode,
                    onCheckedChange = { viewModel.toggleBedtimeMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary,
                        uncheckedThumbColor = OnSurfaceVariant,
                        uncheckedTrackColor = CozyCardBorder
                    )
                )
            }
        }

        // Soundscapes Grid Header & 3-Column Justified Tiles
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ambient Soundscapes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            text = "Offline soothing natural acoustics",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                    if (isPlaying) {
                        Surface(
                            shape = CircleShape,
                            color = Primary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Primary)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Playing",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "🎵", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 3-Column Justified Grid: 2 rows of 3 perfectly centered cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AVAILABLE_SOUNDSCAPES.chunked(3).forEach { rowSounds ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowSounds.forEach { sound ->
                                val isThisPlaying = isPlaying && activeSoundId == sound.id
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isThisPlaying) Primary.copy(alpha = 0.16f) else OnSurfaceVariant.copy(alpha = 0.05f),
                                    border = BorderStroke(
                                        width = if (isThisPlaying) 2.dp else 1.dp,
                                        color = if (isThisPlaying) Primary else CozyCardBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.toggleStandaloneSoundscape(sound.id) }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 14.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = sound.emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = sound.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isThisPlaying) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isThisPlaying) Primary else OnSurface,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = sound.tag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isThisPlaying) Primary.copy(alpha = 0.85f) else OnSurfaceVariant,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Volume Slider - Justified & Centered
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Volume",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Slider(
                        value = volume,
                        onValueChange = { viewModel.setSoundscapeVolume(it) },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = CozyCardBorder
                        )
                    )
                    Text(
                        text = "${(volume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sleep Timer Presets - Justified 5 Pills in 1 Line
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sleep Timer",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Sleep Timer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    }
                    if (sleepTimerMinutes > 0 && isPlaying) {
                        Text(
                            text = "Auto-off: $timerFormatted",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        0 to "Off ∞",
                        15 to "15m",
                        30 to "30m",
                        45 to "45m",
                        60 to "60m"
                    ).forEach { (minutes, label) ->
                        val isSelected = sleepTimerMinutes == minutes
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.06f),
                            border = BorderStroke(1.dp, if (isSelected) Primary else CozyCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clickable { viewModel.setSleepTimer(minutes) }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else OnSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
