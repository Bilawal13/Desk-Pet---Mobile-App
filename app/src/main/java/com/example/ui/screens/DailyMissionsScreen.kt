package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MissionItem
import com.example.data.model.WeeklyQuestItem
import com.example.ui.DeskPetViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun DailyMissionsScreen(
    viewModel: DeskPetViewModel,
    missions: List<MissionItem>,
    weeklyQuest: WeeklyQuestItem?,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()

    // Real-time ticking countdown to midnight for Daily Reset
    val initialCountdown = remember {
        val now = java.util.Calendar.getInstance()
        val midnight = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 24)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        ((midnight.timeInMillis - now.timeInMillis) / 1000).coerceAtLeast(0).toInt()
    }
    var countdownSeconds by remember { mutableStateOf(initialCountdown) }
    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }
    val hours = countdownSeconds / 3600
    val mins = (countdownSeconds % 3600) / 60
    val secs = countdownSeconds % 60
    val countdownFormatted = "%02dh %02dm %02ds".format(hours, mins, secs)

    val doneMissions = missions.count { it.isCompleted }
    val totalMissions = missions.size.coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Milestone & Countdown Banner
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(PrimaryFixed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Daily Missions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("⏱️", fontSize = 11.sp)
                                Text(
                                    text = "Refreshes in $countdownFormatted",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("🌱", fontSize = 11.sp)
                            Text(
                                text = "$doneMissions / $totalMissions Done",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Golden Sprout Chest Milestone Progress
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Text("🎁", fontSize = 16.sp)
                                Text(
                                    text = "Golden Sprout Chest",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Text(
                                text = "MILESTONE ($doneMissions/$totalMissions)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Gummy bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHighest)
                        ) {
                            val fraction = (doneMissions.toFloat() / totalMissions).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(SecondaryFixedDim, Secondary)
                                        )
                                    )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("💎", fontSize = 13.sp)
                                Text(
                                    text = "Bonus: 250 Coins + Rare Seed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }

                            if (doneMissions >= totalMissions) {
                                Button(
                                    onClick = { viewModel.claimMilestoneChest() },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Claim Bonus!", style = MaterialTheme.typography.labelSmall)
                                }
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryFixed.copy(alpha = 0.7f)
                                ) {
                                    Text(
                                        text = "${totalMissions - doneMissions} Left",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnPrimaryFixedVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Streak Bonus Tracker Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val streak = profile?.currentStreak ?: 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🔥", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "$streak Day Streak",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (streak > 0) "Keep up your momentum!" else "Start a focus session today!",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = TertiaryFixed
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("⚡", fontSize = 11.sp)
                            Text(
                                text = if (streak > 0) "Active" else "Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnTertiaryFixedVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 7 Daily Reward Bubbles Row
                val dayInCycle = if (streak > 0) ((streak - 1) % 7) + 1 else 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..7).forEach { dayNum ->
                        val isClaimed = dayNum < dayInCycle
                        val isActive = dayNum == dayInCycle
                        val subtext = when (dayNum) {
                            5 -> "Treat"
                            7 -> "200🪙"
                            else -> ""
                        }
                        val label = when {
                            isClaimed -> "✓"
                            dayNum == 5 -> "🍪"
                            dayNum == 6 -> "🥚"
                            dayNum == 7 -> "⭐"
                            else -> "D$dayNum"
                        }
                        StreakBubble(
                            day = "D$dayNum",
                            label = label,
                            isClaimed = isClaimed,
                            isActive = isActive,
                            subtext = subtext
                        )
                    }
                }
            }
        }

        // Weekly Legendary Quest Card
        weeklyQuest?.let { quest ->
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CozyCardBackground,
                border = BorderStroke(1.dp, CozyCardBorder),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    PrimaryFixed.copy(alpha = 0.35f),
                                    CozyCardBackground
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.quest_beret),
                                    contentDescription = "Weekly Quest Reward",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = TertiaryFixed
                                        ) {
                                            Text(
                                                text = "WEEKLY QUEST",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = OnTertiaryFixedVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = "${quest.daysLeft}d left",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = quest.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = quest.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Progress
                        val progressFraction = (quest.progressMinutes.toFloat() / quest.targetMinutes).coerceIn(0f, 1f)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val hrs = quest.progressMinutes / 60
                                val remMins = quest.progressMinutes % 60
                                Text(
                                    text = "Progress (${hrs}h ${remMins}m / 5h)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = "${(progressFraction * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHighest)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressFraction)
                                        .fillMaxHeight()
                                        .clip(CircleShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(PrimaryContainer, Primary)
                                            )
                                        )
                                    )
                            }
                        }

                        // Reward bottom badge & claim action
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainer.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🎖️", fontSize = 16.sp)
                                Text(
                                    text = "Reward: ${quest.rewardTitle}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            when {
                                quest.isClaimed -> {
                                    Surface(
                                        shape = CircleShape,
                                        color = SecondaryContainer
                                    ) {
                                        Text(
                                            text = "Claimed ✓",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSecondaryContainer,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                quest.progressMinutes >= quest.targetMinutes -> {
                                    Button(
                                        onClick = { viewModel.claimWeeklyQuest() },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Claim Reward!", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                else -> {
                                    Surface(
                                        shape = CircleShape,
                                        color = SurfaceContainerLowest,
                                        shadowElevation = 1.dp
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("🪙", fontSize = 12.sp)
                                            Text(
                                                text = "+${quest.rewardCoins}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Missions List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Missions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Reset at 00:00",
                style = MaterialTheme.typography.bodySmall,
                color = Outline
            )
        }

        missions.forEach { mission ->
            MissionCard(
                mission = mission,
                onClaim = { viewModel.claimMission(mission.id) }
            )
        }
    }
}

@Composable
private fun StreakBubble(
    day: String,
    label: String,
    isClaimed: Boolean,
    isActive: Boolean,
    subtext: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        val bubbleBg = when {
            isClaimed -> SecondaryContainer
            isActive -> PrimaryContainer
            else -> SurfaceContainer
        }
        val bubbleColor = when {
            isClaimed -> OnSecondaryContainer
            isActive -> Color.White
            else -> OnSurfaceVariant
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(bubbleBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = if (label == "✓") 18.sp else 16.sp,
                color = bubbleColor,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = if (subtext.isNotEmpty()) "$day • $subtext" else day,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) Primary else OnSurfaceVariant,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun MissionCard(
    mission: MissionItem,
    onClaim: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (mission.isLocked) CozyCardBackgroundElevated else CozyCardBackground,
        border = BorderStroke(1.dp, CozyCardBorder),
        shadowElevation = if (mission.isLocked) 1.dp else 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val iconText = when (mission.iconName) {
                        "wb_twilight" -> "🌅"
                        "checklist" -> "📋"
                        "hourglass_top" -> "⏳"
                        "favorite" -> "💖"
                        "bedtime" -> "🌙"
                        else -> "🎯"
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (mission.isCompleted) SecondaryContainer.copy(alpha = 0.6f) else SurfaceContainerLow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iconText, fontSize = 20.sp)
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = mission.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (mission.isCompleted) {
                                Surface(
                                    shape = CircleShape,
                                    color = SecondaryFixed
                                ) {
                                    Text(
                                        text = "Done!",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSecondaryFixed,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (mission.isLocked) {
                                Surface(
                                    shape = CircleShape,
                                    color = SurfaceContainerHighest
                                ) {
                                    Text(
                                        text = mission.lockText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Outline,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = SurfaceContainer
                                ) {
                                    Text(
                                        text = "${mission.progress} / ${mission.target}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = mission.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // Progress bar if in progress
            if (!mission.isCompleted && !mission.isLocked) {
                val progressFrac = (mission.progress.toFloat() / mission.target).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFrac)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(SecondaryFixedDim, Secondary)
                                )
                            )
                    )
                }
            }

            // Reward badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = TertiaryFixed.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("🪙", fontSize = 12.sp)
                        Text(
                            text = "${mission.coinsReward} Coins",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnTertiaryFixedVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (mission.bonusItemText.isNotEmpty()) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryFixed.copy(alpha = 0.6f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("🍓", fontSize = 12.sp)
                            Text(
                                text = mission.bonusItemText,
                                style = MaterialTheme.typography.labelSmall,
                                color = OnPrimaryFixedVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (mission.xpReward > 0) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryFixed.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "+${mission.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnPrimaryFixedVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Claim button if completed
            if (mission.isCompleted) {
                if (mission.isClaimed) {
                    Surface(
                        shape = CircleShape,
                        color = SecondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = OnSecondaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Claimed!",
                                style = MaterialTheme.typography.labelLarge,
                                color = OnSecondaryContainer
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onClaim,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("claim_mission_${mission.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Claim Reward!",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
