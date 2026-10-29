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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AVAILABLE_MOODS
import com.example.data.model.MoodEntry
import com.example.ui.theme.*

@Composable
fun DailyMoodCard(
    latestMood: MoodEntry?,
    userName: String,
    onOpenCheckIn: () -> Unit,
    onOpenBreathing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoggedToday = latestMood != null && isToday(latestMood.timestamp)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = CozyCardBackground,
        border = BorderStroke(1.dp, CozyCardBorder),
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (isLoggedToday && latestMood != null) {
                // LOGGED STATE: Justified & Aligned in 1 Clean Line Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCheckIn() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = latestMood.moodEmoji, fontSize = 20.sp)
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Sanctuary Vibe:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = latestMood.moodLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = if (latestMood.note.isNotBlank()) "\"${latestMood.note}\"" else "Checked in with Mochi today",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Primary.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, Primary.copy(alpha = 0.25f)),
                        modifier = Modifier.clickable { onOpenCheckIn() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit mood",
                                tint = Primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Companion whisper message in soft bubble
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Primary.copy(alpha = 0.06f),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.14f)),
                    modifier = Modifier.fillMaxWidth().clickable { onOpenCheckIn() }
                ) {
                    Text(
                        text = "🐾 ${latestMood.petResponse}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        lineHeight = 16.sp,
                        fontSize = 12.sp
                    )
                }
            } else {
                // NOT LOGGED YET: Justified in 1 Line Header & 5 Justified Mood Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCheckIn() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🍵", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "Daily Mindful Check-in",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "How is your heart feeling today, $userName?",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                maxLines = 1,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Check-In",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 5 Mood Chips Justified in 1 Line with Equal Widths
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AVAILABLE_MOODS.forEach { mood ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onOpenCheckIn() }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(OnSurfaceVariant.copy(alpha = 0.07f))
                                    .border(1.dp, CozyCardBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mood.emoji, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mood.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                fontSize = if (mood.label.length > 7) 9.2.sp else 10.5.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Quick Breathe Action Button - Justified & Aligned
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Primary.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.22f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBreathing() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Breathe",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Breathe with Mochi · 1-Min Reset",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            maxLines = 1
                        )
                    }
                    Text(text = "🧘", fontSize = 18.sp)
                }
            }
        }
    }
}

private fun isToday(timestamp: Long): Boolean {
    val cal1 = java.util.Calendar.getInstance()
    val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
}
