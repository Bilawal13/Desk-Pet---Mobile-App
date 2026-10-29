package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.PetItem
import com.example.data.model.UserProfile
import com.example.ui.AppNavTab
import com.example.ui.DeskPetViewModel
import com.example.ui.SanctuaryHubTab
import com.example.ui.components.DailyMoodCard
import com.example.ui.components.PetAnimationStateSelector
import com.example.ui.components.PetModelView
import com.example.ui.theme.*

@Composable
fun RoomScreen(
    viewModel: DeskPetViewModel,
    profile: UserProfile,
    pet: PetItem,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isFullScreenRoomOpen by remember { mutableStateOf(false) }
    val animationState by viewModel.animationState.collectAsState()
    val animationEngine by viewModel.animationEngine.collectAsState()
    val isHeartAnim by viewModel.petHeartAnim.collectAsState()
    val isBerryAnim by viewModel.petBerryAnim.collectAsState()
    val isWiggleAnim by viewModel.petWiggleAnim.collectAsState()
    val latestMood by viewModel.latestMoodEntry.collectAsState()
    val isBedtimeMode by viewModel.isBedtimeModeActive.collectAsState()

    // Bouncy scale for pet interaction
    val petScale by animateFloatAsState(
        targetValue = if (isHeartAnim || isBerryAnim) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pet_scale"
    )

    val wiggleRotation by animateFloatAsState(
        targetValue = if (isWiggleAnim) 15f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "pet_wiggle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Dashboard Top Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Identity & XP Card (Weight 7)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CozyCardBackground,
                border = BorderStroke(1.dp, CozyCardBorder),
                shadowElevation = 3.dp,
                modifier = Modifier.weight(0.68f)
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
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = pet.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text("✨", fontSize = 16.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryFixed
                            ) {
                                Text(
                                    text = pet.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnPrimaryFixedVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = SecondaryContainer,
                                modifier = Modifier
                                    .clickable { viewModel.selectTab(AppNavTab.PETS) }
                                    .testTag("switch_pet_shortcut_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = null,
                                        tint = OnSecondaryContainer,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Equip",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSecondaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // XP Progress bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Level ${pet.level}",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = "${pet.xp} / ${pet.maxXp} XP",
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
                                .background(SurfaceContainer)
                        ) {
                            val fraction = (pet.xp.toFloat() / pet.maxXp).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
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
                }
            }

            // Daily Focus Ring Card (Weight 3)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CozyCardBackground,
                border = BorderStroke(1.dp, CozyCardBorder),
                shadowElevation = 3.dp,
                modifier = Modifier.weight(0.32f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val completedSessions = profile.totalFocusSessions.coerceAtLeast(0)
                    val targetSessions = 4
                    val sessionFraction = (completedSessions.toFloat() / targetSessions).coerceIn(0f, 1f)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(52.dp)
                    ) {
                    val sessionTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    val sessionProgressColor = MaterialTheme.colorScheme.secondary
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = sessionTrackColor,
                            style = Stroke(width = 12f)
                        )
                        drawArc(
                            color = sessionProgressColor,
                            startAngle = -90f,
                            sweepAngle = 360f * sessionFraction,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = StrokeCap.Round)
                        )
                    }
                        Text(
                            text = "${completedSessions.coerceAtMost(targetSessions)}/$targetSessions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Focus Goals",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        // Pet Vitals Soft Status Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VitalStatCard(
                icon = "🍔",
                iconBg = TertiaryFixed,
                label = "Hunger",
                percent = pet.hunger,
                barColor = TertiaryContainer,
                modifier = Modifier.weight(1f)
            )
            VitalStatCard(
                icon = "⚡",
                iconBg = SecondaryFixed,
                label = "Energy",
                percent = pet.energy,
                barColor = SecondaryContainer,
                modifier = Modifier.weight(1f)
            )
            VitalStatCard(
                icon = "💖",
                iconBg = PrimaryFixed,
                label = "Joy",
                percent = pet.happiness,
                barColor = Primary,
                modifier = Modifier.weight(1f)
            )
        }

        // Centerpiece 3D-Feel Pet Living Space
        Surface(
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(335.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            if (isBedtimeMode) {
                                listOf(
                                    Color(0xFF14172B),
                                    Color(0xFF1F1C36),
                                    Color(0xFF121422)
                                )
                            } else if (androidx.compose.foundation.isSystemInDarkTheme()) {
                                listOf(
                                    Color(0xFF222638),
                                    Color(0xFF191C2B),
                                    Color(0xFF131522)
                                )
                            } else {
                                listOf(
                                    Color(0xFFEEF2FF),
                                    Color(0xFFF5F3FF),
                                    Color(0xFFFDF4FF)
                                )
                            }
                        )
                    )
                    .padding(16.dp)
            ) {
                // Ceiling fairy lights string
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(7) { index ->
                        val bulbColor = when (index % 3) {
                            0 -> Color(0xFFFFE082)
                            1 -> Color(0xFFFFCC80)
                            else -> Color(0xFFFFAB91)
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(bulbColor)
                        )
                    }
                }

                // Sunny Window in the top-right
                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 88.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFB3E5FC), Color(0xFFE1F5FE))
                            )
                        )
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.8f),
                            RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                        )
                ) {
                    Text("☀️", modifier = Modifier.align(Alignment.TopEnd).padding(4.dp), fontSize = 16.sp)
                    Text("☁️", modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp), fontSize = 14.sp)
                }

                // View Room Option (Opens Fullscreen Experience)
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, PrimaryFixed.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 96.dp, end = 2.dp)
                        .clickable { isFullScreenRoomOpen = true }
                        .testTag("view_room_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "View Room",
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "View Room",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }

                // Speech Bubble with Pet-specific voice
                val petDialogue = when (animationState) {
                    com.example.ui.PetAnimationState.HAPPY -> "${pet.name}: 'Purr... I love spending time with you!' 💖"
                    com.example.ui.PetAnimationState.FEED -> "${pet.name}: 'Nom nom! That treat was delicious!' 🍓"
                    com.example.ui.PetAnimationState.PLAY -> "${pet.name}: 'Wheee! Playing with you is so much fun!' 🎾"
                    com.example.ui.PetAnimationState.ZEN -> "${pet.name}: 'Inhale peace... exhale tension... 🧘🌸'"
                    com.example.ui.PetAnimationState.SLEEP -> "${pet.name}: 'Zzz... Sleeping peacefully under the stars... 🌙💤'"
                    com.example.ui.PetAnimationState.SAD -> "${pet.name}: 'Aww... don't worry, we can try again together! 🥺'"
                    com.example.ui.PetAnimationState.IDLE -> when (pet.id) {
                        "mochi" -> "Mochi: 'Ready for another cozy study session?' 🌱"
                        "boba" -> "Boba: 'Let's stay calm and focused together!' 🧋"
                        "pippin" -> "Pippin: 'Ribbit! Soaking up peaceful vibes!' 🍵"
                        "nimbus" -> "Nimbus: 'Drifting gently above distractions!' ☁️"
                        "shiba" -> "Chai: 'I believe in you! Keep going strong!' 🐕"
                        "axolotl" -> "Taro: 'Take slow, serene deep breaths!' 🌸"
                        "owl" -> "Luna: 'Quiet night focus brings wisdom!' 🦉"
                        "hamster" -> "Pixel: 'High energy! Let's clear tasks!' 🐹"
                        "aurelia" -> "Aurelia: 'Starlight sparkles in our room!' ✨"
                        "panda" -> "Matcha Panda: 'Peace in every mindful breath.' 🐼"
                        else -> "${pet.name}: 'Resting happily in your cozy room!' 🐾"
                    }
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceContainerLowest.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 16.dp)
                        .widthIn(max = 240.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = petDialogue,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Left Corner: Mini Desk with Plant & Books
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 8.dp)
                ) {
                    Text("🪴", fontSize = 18.sp)
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFC89B7B))
                    )
                    Row(
                        modifier = Modifier.width(44.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.size(width = 6.dp, height = 18.dp).background(Color(0xFFA97858)))
                        Box(modifier = Modifier.size(width = 6.dp, height = 18.dp).background(Color(0xFFA97858)))
                    }
                }

                // Dead Center of Room: Mochi on Soft Plush Rug
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 0.dp)
                ) {
                    // Fluffy Rug centered on the floor
                    Box(
                        modifier = Modifier
                            .size(width = 175.dp, height = 56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                            .border(2.dp, PrimaryFixed.copy(alpha = 0.6f), CircleShape)
                    )

                    // Interactive Animated Pet Character Model (Rive / 3D)
                    PetModelView(
                        pet = pet,
                        animationState = animationState,
                        engine = animationEngine,
                        onPetClick = { viewModel.petCompanion() },
                        onStateSelect = { viewModel.setAnimationState(it) },
                        onToggleEngine = { viewModel.toggleAnimationEngine() },
                        showStateSelector = false,
                        modifier = Modifier.offset(y = (-6).dp)
                    )
                }

                // Right Corner: Cozy Floor Beanbag / Pillow (Clean, zero overlap)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shop_beanbag),
                        contentDescription = "Cozy Beanbag",
                        modifier = Modifier.size(50.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // Clean Pet Animations & Moods Bar (Unobstructed, readable, easy to tap, zero furniture collisions)
        PetAnimationStateSelector(
            animationState = animationState,
            engine = animationEngine,
            onStateSelect = { viewModel.setAnimationState(it) },
            onToggleEngine = { viewModel.toggleAnimationEngine() }
        )

        // Interactive Care Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CareActionButton(
                icon = "🥐",
                label = "Feed",
                subtext = "10 🪙",
                subtextColor = GoldCoin,
                onClick = { viewModel.feedPet() },
                modifier = Modifier.weight(1f),
                tag = "care_feed_btn"
            )
            CareActionButton(
                icon = "✋",
                label = "Pet",
                subtext = "Free",
                subtextColor = Secondary,
                onClick = { viewModel.petCompanion() },
                modifier = Modifier.weight(1f),
                tag = "care_pet_btn"
            )
            CareActionButton(
                icon = "⚾",
                label = "Play",
                subtext = "-10 ⚡",
                subtextColor = Tertiary,
                onClick = { viewModel.playWithPet() },
                modifier = Modifier.weight(1f),
                tag = "care_play_btn"
            )
            CareActionButton(
                icon = "🎨",
                label = "Room",
                subtext = "Decor",
                subtextColor = Primary,
                onClick = { viewModel.selectTab(AppNavTab.SHOP) },
                modifier = Modifier.weight(1f),
                tag = "care_room_btn"
            )
        }

        // Daily Mood & Mindful Breathing Sanctuary Card
        DailyMoodCard(
            latestMood = latestMood,
            userName = profile.displayName.ifBlank { profile.username },
            onOpenCheckIn = { viewModel.setMoodCheckInDialogVisible(true) },
            onOpenBreathing = {
                viewModel.selectSanctuaryTab(SanctuaryHubTab.BREATHING)
                viewModel.selectTab(AppNavTab.FOCUS)
            }
        )

        // Bottom Productivity Quick Action Widget
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                            .size(46.dp)
                            .background(PrimaryFixed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Deep Focus 25m",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+50 🪙",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tertiary,
                                fontWeight = FontWeight.Bold
                            )
                            Text("•", color = OutlineVariant)
                            Text(
                                text = "+100 Pet XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Button(
                    onClick = { viewModel.selectTab(AppNavTab.FOCUS) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.testTag("start_quick_focus_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Start", style = MaterialTheme.typography.labelLarge)
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (isFullScreenRoomOpen) {
        FullScreenRoomDialog(
            pet = pet,
            animationState = animationState,
            animationEngine = animationEngine,
            isBedtimeMode = isBedtimeMode,
            onPetClick = { viewModel.petCompanion() },
            onStateSelect = { viewModel.setAnimationState(it) },
            onToggleEngine = { viewModel.toggleAnimationEngine() },
            onDismiss = { isFullScreenRoomOpen = false }
        )
    }
}

@Composable
private fun VitalStatCard(
    icon: String,
    iconBg: Color,
    label: String,
    percent: Int,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CozyCardBackground,
        border = BorderStroke(1.dp, CozyCardBorder),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 13.sp)
                }
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percent / 100f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(barColor)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun CareActionButton(
    icon: String,
    label: String,
    subtext: String,
    subtextColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CozyCardBackground,
        border = BorderStroke(1.dp, CozyCardBorder),
        shadowElevation = 3.dp,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(SurfaceContainerLow, CircleShape)
                    .border(1.dp, CozyCardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = subtextColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Immersive Full Screen View of Mochi's Room.
 * Features full-bleed atmospheric room backdrop, fairy lights, wall shelf, desk,
 * cozy beanbag, centered grounded pet, interactive speech bubble, and animations controller.
 */
@Composable
fun FullScreenRoomDialog(
    pet: PetItem,
    animationState: com.example.ui.PetAnimationState,
    animationEngine: com.example.ui.PetAnimationEngine,
    isBedtimeMode: Boolean,
    onPetClick: () -> Unit,
    onStateSelect: (com.example.ui.PetAnimationState) -> Unit,
    onToggleEngine: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (isBedtimeMode) {
                            listOf(
                                Color(0xFF0F1123),
                                Color(0xFF1B1934),
                                Color(0xFF121422)
                            )
                        } else if (androidx.compose.foundation.isSystemInDarkTheme()) {
                            listOf(
                                Color(0xFF1E2235),
                                Color(0xFF171928),
                                Color(0xFF11131E)
                            )
                        } else {
                            listOf(
                                Color(0xFFE8F0FE),
                                Color(0xFFF3E8FF),
                                Color(0xFFFFF0F5)
                            )
                        }
                    )
                )
        ) {
            // Fullscreen Ceiling Fairy Lights
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(11) { index ->
                    val bulbColor = when (index % 4) {
                        0 -> Color(0xFFFFE082)
                        1 -> Color(0xFFFFCC80)
                        2 -> Color(0xFFFF8A80)
                        else -> Color(0xFF80D8FF)
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(bulbColor)
                            .shadow(4.dp, CircleShape)
                    )
                }
            }

            // Top Bar with Room Title & Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 58.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🏡", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "${pet.name}'s Cozy Room",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Full Screen Sanctuary • Tap pet to cuddle",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .testTag("close_fullscreen_room_btn")
                ) {
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Fullscreen",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Fullscreen Wall Decor:
            // Big Beautiful Sunny Arched Window
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 135.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 138.dp, end = 20.dp)
                    .clip(RoundedCornerShape(topStart = 55.dp, topEnd = 55.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(
                        Brush.verticalGradient(
                            if (isBedtimeMode) {
                                listOf(Color(0xFF1E2640), Color(0xFF2C3E6B))
                            } else {
                                listOf(Color(0xFFB3E5FC), Color(0xFFE1F5FE))
                            }
                        )
                    )
                    .border(
                        3.dp,
                        Color.White.copy(alpha = 0.85f),
                        RoundedCornerShape(topStart = 55.dp, topEnd = 55.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
            ) {
                if (isBedtimeMode) {
                    Text("🌙", modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), fontSize = 22.sp)
                    Text("✨", modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp), fontSize = 16.sp)
                } else {
                    Text("☀️", modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), fontSize = 22.sp)
                    Text("☁️", modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp), fontSize = 18.sp)
                }
            }

            // Cozy Wall Bookshelf & Plant on Left Wall
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 138.dp, start = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(bottom = 2.dp, start = 8.dp)
                ) {
                    Text("🪴", fontSize = 24.sp)
                    Text("📚", fontSize = 20.sp)
                    Text("🖼️", fontSize = 18.sp)
                }
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFC89B7B))
                )
            }

            // Room Floor Area with Furniture & Mochi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 150.dp)
            ) {
                // Large Fluffy Plush Rug
                Box(
                    modifier = Modifier
                        .size(width = 260.dp, height = 80.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-40).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(3.dp, PrimaryFixed.copy(alpha = 0.7f), CircleShape)
                )

                // Left Furniture: Desk with cozy lamp & tea
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 36.dp)
                ) {
                    Text("💡", fontSize = 26.sp)
                    Text("🍵", fontSize = 18.sp)
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFC89B7B))
                    )
                    Row(
                        modifier = Modifier.width(60.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.size(width = 8.dp, height = 24.dp).background(Color(0xFFA97858)))
                        Box(modifier = Modifier.size(width = 8.dp, height = 24.dp).background(Color(0xFFA97858)))
                    }
                }

                // Center: Mochi Character in HD (Grounded on the rug, interactive)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-45).dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    PetModelView(
                        pet = pet,
                        animationState = animationState,
                        engine = animationEngine,
                        onPetClick = onPetClick,
                        onStateSelect = onStateSelect,
                        onToggleEngine = onToggleEngine,
                        showStateSelector = false
                    )
                }

                // Right Furniture: Big Cozy Beanbag
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 36.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shop_beanbag),
                        contentDescription = "Cozy Beanbag",
                        modifier = Modifier.size(76.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Bottom Floating Controls Tray:
            // Mood Selector & Dynamic Dialogue
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pet Dialogue Speech Bubble
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("💬", fontSize = 16.sp)
                        Text(
                            text = when (animationState) {
                                com.example.ui.PetAnimationState.HAPPY -> "${pet.name}: 'So cozy! Thank you for spending time here with me! 💖'"
                                com.example.ui.PetAnimationState.FEED -> "${pet.name}: 'Yum! Munching my favorite treats in my room! 🍓'"
                                com.example.ui.PetAnimationState.PLAY -> "${pet.name}: 'Look at my room! High five! 👋✨'"
                                com.example.ui.PetAnimationState.ZEN -> "${pet.name}: 'Breathing peacefully in my zen sanctuary... 🧘🌸'"
                                com.example.ui.PetAnimationState.SLEEP -> "${pet.name}: 'Cozy blankets and sweet dreams... 🌙💤'"
                                com.example.ui.PetAnimationState.SAD -> "${pet.name}: 'I love being here with you, feels so safe... 🥺'"
                                com.example.ui.PetAnimationState.IDLE -> "${pet.name}: 'Welcome to my full cozy room! Tap anywhere to hang out!' 🌱"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Fullscreen Animation & Mood Pills Bar
                PetAnimationStateSelector(
                    animationState = animationState,
                    engine = animationEngine,
                    onStateSelect = onStateSelect,
                    onToggleEngine = onToggleEngine
                )
            }
        }
    }
}
