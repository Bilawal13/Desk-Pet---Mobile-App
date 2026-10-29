package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AVAILABLE_BREATHING_TECHNIQUES
import com.example.data.model.BreathingTechnique
import com.example.data.model.getPetDrawableResByName
import com.example.ui.BreathingPhase
import com.example.ui.DeskPetViewModel
import com.example.ui.theme.*

@Composable
fun ZenBreathingSection(
    viewModel: DeskPetViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTechnique by viewModel.selectedBreathingTechnique.collectAsState()
    val isActive by viewModel.isBreathingActive.collectAsState()
    val phase by viewModel.breathingPhase.collectAsState()
    val phaseRemainingSeconds by viewModel.breathingPhaseSecondsRemaining.collectAsState()
    val elapsedSeconds by viewModel.breathingElapsedSeconds.collectAsState()
    val totalSeconds by viewModel.breathingTotalDurationSeconds.collectAsState()
    val cyclesCompleted by viewModel.breathingCyclesCompleted.collectAsState()
    val activePet by viewModel.activePet.collectAsState()

    // Smooth pulsing scale driven by current breathing phase
    val targetScale = when {
        !isActive -> 1.0f
        phase == BreathingPhase.INHALE -> 1.35f
        phase == BreathingPhase.HOLD_INHALE -> 1.35f
        phase == BreathingPhase.EXHALE -> 0.95f
        else -> 0.95f // HOLD_EXHALE
    }

    val animScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = when (phase) {
                BreathingPhase.INHALE -> selectedTechnique.inhaleSeconds * 1000
                BreathingPhase.HOLD_INHALE -> 400
                BreathingPhase.EXHALE -> selectedTechnique.exhaleSeconds * 1000
                else -> 400
            },
            easing = FastOutSlowInEasing
        ),
        label = "breathing_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Technique Selector Chips
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Breathing Mode",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Breathing Technique",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    }
                    Text(
                        text = selectedTechnique.tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AVAILABLE_BREATHING_TECHNIQUES.forEach { tech ->
                        val isSelected = tech.id == selectedTechnique.id
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.07f),
                            border = BorderStroke(1.dp, if (isSelected) Primary else CozyCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clickable { viewModel.selectBreathingTechnique(tech) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 6.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = when (tech.id) {
                                        "box" -> "Box 4-4"
                                        "478" -> "Calm 4-7-8"
                                        else -> "Flow 4-4"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else OnSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tech.tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else OnSurfaceVariant,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Text(
                    text = selectedTechnique.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        // Duration Presets (1m, 2m, 3m, 5m)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(60 to "1 Min", 120 to "2 Mins", 180 to "3 Mins", 300 to "5 Mins").forEach { (sec, label) ->
                val isSelected = totalSeconds == sec
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Primary.copy(alpha = 0.16f) else OnSurfaceVariant.copy(alpha = 0.06f),
                    border = BorderStroke(1.dp, if (isSelected) Primary else CozyCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clickable { viewModel.setBreathingDuration(sec) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Primary else OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Glowing Breathing Visualizer Card
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Expanding / Contracting Lotus Orb
                Box(
                    modifier = Modifier
                        .size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Radiant Ring
                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .scale(animScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = if (isActive) 0.35f else 0.12f),
                                        Primary.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Secondary Glowing Aura
                    Box(
                        modifier = Modifier
                            .size(175.dp)
                            .scale(animScale * 0.9f)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.18f))
                            .border(2.dp, Primary.copy(alpha = 0.4f), CircleShape)
                    )

                    // Inner Center Bubble with Pet
                    Surface(
                        shape = CircleShape,
                        color = CozyCardBackground,
                        border = BorderStroke(2.dp, Primary),
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(110.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = getPetDrawableResByName(activePet.drawableResName, activePet.id)),
                                contentDescription = "Breathing Companion",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                }

                // Phase Instruction & Timer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isActive) "${phase.label} (${phaseRemainingSeconds}s)" else "Ready to Breathe",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Primary else OnSurface
                    )
                    Text(
                        text = if (isActive) phase.instruction else "Take a moment to center yourself with ${activePet.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Progress Info: Cycles & Total Time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cycles: $cyclesCompleted",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${elapsedSeconds}s / ${totalSeconds}s",
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { (elapsedSeconds.toFloat() / totalSeconds.coerceAtLeast(1)).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Primary,
                    trackColor = OnSurfaceVariant.copy(alpha = 0.12f)
                )

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset Button
                    IconButton(
                        onClick = { viewModel.resetBreathingSession() },
                        modifier = Modifier
                            .size(52.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.08f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Session",
                            tint = OnSurfaceVariant
                        )
                    }

                    // Main Start/Pause Button
                    Button(
                        onClick = {
                            if (isActive) viewModel.pauseBreathingSession()
                            else viewModel.startBreathingSession()
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f),
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
                            Icon(
                                imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isActive) "Pause" else "Start"
                            )
                            Text(
                                text = if (isActive) "Pause" else "Start Breathing",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
