package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AVAILABLE_MOODS
import com.example.data.model.MoodOption
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodCheckInSheet(
    initialMoodKey: String = "peaceful",
    onRecordMood: (moodKey: String, note: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMoodKey by remember { mutableStateOf(initialMoodKey) }
    var gratitudeNote by remember { mutableStateOf("") }
    val currentMood = AVAILABLE_MOODS.find { it.key == selectedMoodKey } ?: AVAILABLE_MOODS[1]

    val starterChips = listOf(
        "A warm cup of tea ☕",
        "Soft cozy rain 🌧️",
        "Finished a hard task ✨",
        "Pet snuggles 🐾",
        "Gentle fresh air 🍃",
        "Slow restful morning ☀️"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CozyCardBackground,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = OnSurfaceVariant.copy(alpha = 0.4f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Daily Mindful Check-In",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(text = "🍵", fontSize = 20.sp)
                    }
                    Text(
                        text = "How is your heart feeling in this moment?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(OnSurfaceVariant.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 5 Mood Options Selector - Justified in 1 Clean Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AVAILABLE_MOODS.forEach { mood ->
                    val isSelected = mood.key == selectedMoodKey
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedMoodKey = mood.key }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Primary.copy(alpha = 0.18f)
                                    else OnSurfaceVariant.copy(alpha = 0.06f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Primary else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mood.emoji, fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = mood.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Primary else OnSurfaceVariant,
                            fontSize = if (mood.label.length > 7) 9.5.sp else 11.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // Mochi's Empathetic Dialogue Bubble
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Primary.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🐾", fontSize = 28.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mochi's Cozy Whisper:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = currentMood.companionMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Optional Gratitude / Reflection Note
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "A small gratitude or cozy thought (optional):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                OutlinedTextField(
                    value = gratitudeNote,
                    onValueChange = { gratitudeNote = it },
                    placeholder = {
                        Text(
                            text = "e.g. Grateful for a warm blanket, a good breath...",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = CozyCardBorder,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    maxLines = 3
                )

                // Quick starter tags
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(starterChips) { chip ->
                        Surface(
                            shape = CircleShape,
                            color = OnSurfaceVariant.copy(alpha = 0.07f),
                            border = BorderStroke(1.dp, CozyCardBorder),
                            modifier = Modifier.clickable {
                                gratitudeNote = if (gratitudeNote.isBlank()) chip else "$gratitudeNote, $chip"
                            }
                        ) {
                            Text(
                                text = chip,
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Reward Notice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily check-in grants +25 🪙 Coins & +40 💖 Pet Joy",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Submit Button
            Button(
                onClick = {
                    onRecordMood(selectedMoodKey, gratitudeNote)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Log Today's Mood & Hug Mochi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "🌸", fontSize = 16.sp)
                }
            }
        }
    }
}
