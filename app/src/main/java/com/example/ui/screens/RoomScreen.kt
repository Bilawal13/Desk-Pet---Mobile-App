package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt
import kotlin.math.sin
import com.example.R
import com.example.data.model.PetItem
import com.example.data.model.UserProfile
import com.example.data.model.getShopDrawableResByName
import com.example.ui.AppNavTab
import com.example.ui.DeskPetViewModel
import com.example.ui.RoomWeather
import com.example.ui.SanctuaryHubTab
import com.example.ui.components.DailyMoodCard
import com.example.ui.components.DecorZone
import com.example.ui.components.PetAnimationStateSelector
import com.example.ui.components.PetModelView
import com.example.ui.components.RoomDecorSheet
import com.example.ui.sound.DeskPetAudioHaptics
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
    val roomDecorList by viewModel.roomDecor.collectAsState()
    val previewOverrides by viewModel.previewDecorOverrides.collectAsState()
    val activePreviewProduct by viewModel.activePreviewProduct.collectAsState()
    val roomWeather by viewModel.roomWeather.collectAsState()
    val isSoundscapePlaying by viewModel.isSoundscapePlaying.collectAsState()
    val activeSoundscapeId by viewModel.activeSoundscapeId.collectAsState()

    var isDecorSheetOpen by remember { mutableStateOf(false) }
    var decorZoneToOpen by remember { mutableStateOf(DecorZone.WALL) }

    // Map slotId -> itemId, prioritizing preview overrides if active
    val activeDecorMap = remember(roomDecorList, previewOverrides) {
        val baseMap = roomDecorList.associate { it.slotId to it.itemId }
        baseMap + previewOverrides
    }

    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val effectiveWeather = when (roomWeather) {
        RoomWeather.SUNNY -> RoomWeather.SUNNY
        RoomWeather.RAINY -> RoomWeather.RAINY
        RoomWeather.SNOWY -> RoomWeather.SNOWY
        RoomWeather.SUNSET -> RoomWeather.SUNSET
        RoomWeather.AUTO -> when {
            isBedtimeMode || currentHour >= 19 || currentHour < 7 -> RoomWeather.AUTO
            currentHour in 17..18 -> RoomWeather.SUNSET
            else -> RoomWeather.SUNNY
        }
    }
    val isNightTime = isBedtimeMode || (effectiveWeather == RoomWeather.AUTO && (currentHour >= 19 || currentHour < 7))
    val isSunset = effectiveWeather == RoomWeather.SUNSET
    val isRainy = effectiveWeather == RoomWeather.RAINY
    val isSnowy = effectiveWeather == RoomWeather.SNOWY

    val wallStyle = activeDecorMap["wall_style"] ?: "wall_pastel"
    val wallArt = activeDecorMap["wall_art"] ?: "art_clock"
    val windowFrame = activeDecorMap["window_frame"] ?: "win_classic"
    val windowPlant = activeDecorMap["window_plant"] ?: "plant_flowers"
    val deskLamp = activeDecorMap["desk_lamp"] ?: "lamp_classic"
    val deskMug = activeDecorMap["desk_mug"] ?: "mug_tea"
    val rugStyle = activeDecorMap["rug_style"] ?: "rug_cloud"
    val loungeItemId = activeDecorMap["beanbag"] ?: "lounge_beanbag"
    val currentLoungeDrawableRes = when (loungeItemId) {
        "lounge_sloth" -> R.drawable.pet_hamster
        "lounge_cushion" -> R.drawable.shop_flooring
        "lounge_armchair" -> R.drawable.shop_beanbag
        else -> getShopDrawableResByName(loungeItemId, "furniture")
    }

    val wallGradient = when {
        isNightTime -> listOf(Color(0xFF14172B), Color(0xFF1E1A34), Color(0xFF10121D))
        isSunset -> listOf(Color(0xFFFFCC80), Color(0xFFFFAB91), Color(0xFFFBE9E7))
        wallStyle == "wall_sunset" -> listOf(Color(0xFFFFE0B2), Color(0xFFFFCC80), Color(0xFFFFF3E0))
        wallStyle == "wall_midnight" -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0F172A))
        wallStyle == "wall_birch" -> listOf(Color(0xFFEDE0D4), Color(0xFFE6CCB2), Color(0xFFDDB892))
        wallStyle == "wall_mint" -> listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFF1F8E9))
        else -> listOf(Color(0xFFE8F0FE), Color(0xFFF3E8FF), Color(0xFFFFF0F5))
    }

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
        // Active "Try in Room" Shop Preview Floating Banner
        if (activePreviewProduct != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PrimaryContainer,
                border = BorderStroke(1.5.dp, Primary),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🛋️", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "Previewing: ${activePreviewProduct!!.name}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = OnPrimaryContainer
                            )
                            Text(
                                text = "3D Room Preview active before spending coins",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { viewModel.confirmPreviewPurchase() },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Buy ${activePreviewProduct!!.priceCoins} 🪙", style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(
                            onClick = { viewModel.clearRoomPreview() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Exit Preview", tint = OnPrimaryContainer)
                        }
                    }
                }
            }
        }

        // Room Atmosphere HUD: Weather & Ambient Audio Quick Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weather Chip (Cycles AUTO -> SUNNY -> RAINY -> SUNSET -> SNOWY)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        val next = when (roomWeather) {
                            RoomWeather.AUTO -> RoomWeather.SUNNY
                            RoomWeather.SUNNY -> RoomWeather.RAINY
                            RoomWeather.RAINY -> RoomWeather.SUNSET
                            RoomWeather.SUNSET -> RoomWeather.SNOWY
                            RoomWeather.SNOWY -> RoomWeather.AUTO
                        }
                        viewModel.setRoomWeather(next)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(roomWeather.emoji, fontSize = 16.sp)
                    Column {
                        Text("WEATHER", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = OnSurfaceVariant)
                        Text(roomWeather.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Ambient Soundscape Chip (Cycles Off -> Rain -> Lo-Fi -> Fire -> Purr)
            Surface(
                shape = CircleShape,
                color = if (isSoundscapePlaying) PrimaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(1.dp, if (isSoundscapePlaying) Primary else Outline.copy(alpha = 0.3f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (!isSoundscapePlaying) {
                            viewModel.toggleStandaloneSoundscape("rain")
                        } else {
                            when (activeSoundscapeId) {
                                "rain" -> viewModel.toggleStandaloneSoundscape("lofi")
                                "lofi" -> viewModel.toggleStandaloneSoundscape("fire")
                                "fire" -> viewModel.toggleStandaloneSoundscape("purr")
                                "purr" -> viewModel.toggleStandaloneSoundscape("purr")
                                else -> viewModel.toggleStandaloneSoundscape("rain")
                            }
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        if (isSoundscapePlaying) {
                            when (activeSoundscapeId) {
                                "rain" -> "🌧️"
                                "lofi" -> "☕"
                                "fire" -> "🔥"
                                "purr" -> "🐾"
                                else -> "🎧"
                            }
                        } else "🔇",
                        fontSize = 16.sp
                    )
                    Column {
                        Text("AMBIENCE", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = OnSurfaceVariant)
                        Text(
                            text = if (isSoundscapePlaying) {
                                when (activeSoundscapeId) {
                                    "rain" -> "Rain"
                                    "lofi" -> "Lo-Fi"
                                    "fire" -> "Hearth"
                                    "purr" -> "Purring"
                                    else -> "Playing"
                                }
                            } else "Sound Off",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSoundscapePlaying) OnPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Quick Decorate Pill
            Surface(
                shape = CircleShape,
                color = TertiaryContainer,
                modifier = Modifier.clickable {
                    decorZoneToOpen = DecorZone.WALL
                    isDecorSheetOpen = true
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🎨", fontSize = 16.sp)
                    Text("Decor", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = OnTertiaryContainer)
                }
            }
        }
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

        // Centerpiece 3D-Feel Pet Living Space (Interactive Tap-to-Place Room Habitat)
        Surface(
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(wallGradient))
            ) {
                // Room Baseboard & Cozy Wooden Floor Plane
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.38f)
                        .align(Alignment.BottomCenter)
                ) {
                    // Baseboard molding
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                if (isNightTime) Color(0xFF2A2038)
                                else if (isSunset) Color(0xFFBC8A5F)
                                else Color(0xFFD4A373)
                            )
                    )
                    // Wooden / warm floor gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                            .background(
                                Brush.verticalGradient(
                                    if (isNightTime) {
                                        listOf(Color(0xFF1E172E), Color(0xFF140F20))
                                    } else if (isSunset) {
                                        listOf(Color(0xFFDEBEA5), Color(0xFFC89B7B))
                                    } else {
                                        listOf(Color(0xFFEAD5C3), Color(0xFFDEBEA5))
                                    }
                                )
                            )
                    )
                }

                // Ceiling fairy lights string
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
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
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(bulbColor)
                                .shadow(2.dp, CircleShape)
                        )
                    }
                }

                // Big Beautiful Arched Wall Window (Interactive Tap-to-Customize Zone)
                CozyWindowView(
                    width = 96.dp,
                    height = 126.dp,
                    isNightTime = isNightTime,
                    isSunset = isSunset,
                    isRainy = isRainy,
                    isSnowy = isSnowy,
                    windowFrame = windowFrame,
                    windowPlant = windowPlant,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 22.dp, end = 12.dp),
                    onClick = {
                        decorZoneToOpen = DecorZone.WINDOW
                        isDecorSheetOpen = true
                    }
                )

                // Cozy Wall Bookshelf & Plant on Left Wall (Interactive Tap-to-Customize Zone)
                CozyBookshelfArt(
                    wallArt = wallArt,
                    scale = 1f,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 22.dp, start = 12.dp),
                    onClick = {
                        decorZoneToOpen = DecorZone.WALL
                        isDecorSheetOpen = true
                    }
                )

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
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainerLowest.copy(alpha = 0.95f),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 18.dp)
                        .widthIn(max = 210.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = petDialogue,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 11.sp
                        )
                    }
                }

                // Floor Area with Furniture & Mochi (Matching Full View Layout)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                ) {
                    // Large Fluffy Center Plush Rug (Interactive Tap-to-Customize Zone)
                    CozyPlushRug(
                        rugStyle = rugStyle,
                        width = 175.dp,
                        height = 54.dp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-18).dp),
                        onClick = {
                            decorZoneToOpen = DecorZone.RUG_LOUNGE
                            isDecorSheetOpen = true
                        }
                    )

                    // Left Furniture: Desk with customizable lamp & mug (Interactive Tap-to-Customize Zone)
                    CozyDeskDecor(
                        isBedtimeMode = isBedtimeMode,
                        lampStyle = deskLamp,
                        mugStyle = deskMug,
                        scale = 0.65f,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 10.dp, bottom = 24.dp),
                        onClick = {
                            decorZoneToOpen = DecorZone.DESK
                            isDecorSheetOpen = true
                        }
                    )

                    // Right Furniture: Cozy Corner Lounge / Beanbag (Interactive Tap-to-Customize Zone)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp, bottom = 26.dp)
                            .clickable {
                                decorZoneToOpen = DecorZone.RUG_LOUNGE
                                isDecorSheetOpen = true
                            }
                    ) {
                        Image(
                            painter = painterResource(id = currentLoungeDrawableRes),
                            contentDescription = "Cozy Beanbag",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Center: Animated Pet Character Model (Interactive Petting Purr)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-20).dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        PetModelView(
                            pet = pet,
                            animationState = animationState,
                            engine = animationEngine,
                            onPetClick = { viewModel.petCompanion() },
                            onStateSelect = { viewModel.setAnimationState(it) },
                            onToggleEngine = { viewModel.toggleAnimationEngine() },
                            showStateSelector = false
                        )
                    }
                }
            }
        }

        // Beautiful Aesthetically Styled "View Room" Card Button (Moved below pet screen)
        Surface(
            onClick = { isFullScreenRoomOpen = true },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(
                        Primary.copy(alpha = 0.5f),
                        Secondary.copy(alpha = 0.5f),
                        Tertiary.copy(alpha = 0.35f)
                    )
                )
            ),
            shadowElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("view_room_btn")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                PrimaryContainer.copy(alpha = 0.3f),
                                SecondaryContainer.copy(alpha = 0.18f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Primary,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "View Room",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Enter Full Room View",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = CircleShape,
                                color = PrimaryFixed
                            ) {
                                Text(
                                    text = "3D Cozy ✨",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnPrimaryFixedVariant,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = "Tap to explore full room decor, daylight & animations",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(15.dp)
                )
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
                onClick = {
                    decorZoneToOpen = DecorZone.WALL
                    isDecorSheetOpen = true
                },
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
            viewModel = viewModel,
            animationState = animationState,
            animationEngine = animationEngine,
            isBedtimeMode = isBedtimeMode,
            loungeDrawableRes = currentLoungeDrawableRes,
            activeDecorMap = activeDecorMap,
            onPetClick = { viewModel.petCompanion() },
            onStateSelect = { viewModel.setAnimationState(it) },
            onToggleEngine = { viewModel.toggleAnimationEngine() },
            onDismiss = { isFullScreenRoomOpen = false },
            onOpenDecorZone = { zone ->
                decorZoneToOpen = zone
                isDecorSheetOpen = true
            }
        )
    }

    if (isDecorSheetOpen) {
        RoomDecorSheet(
            viewModel = viewModel,
            activeDecorMap = activeDecorMap,
            initialZone = decorZoneToOpen,
            onDismiss = { isDecorSheetOpen = false }
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
    viewModel: DeskPetViewModel? = null,
    animationState: com.example.ui.PetAnimationState,
    animationEngine: com.example.ui.PetAnimationEngine,
    isBedtimeMode: Boolean,
    loungeDrawableRes: Int = R.drawable.shop_beanbag,
    activeDecorMap: Map<String, String> = emptyMap(),
    onPetClick: () -> Unit,
    onStateSelect: (com.example.ui.PetAnimationState) -> Unit,
    onToggleEngine: () -> Unit,
    onDismiss: () -> Unit,
    onOpenDecorZone: (DecorZone) -> Unit = {}
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val roomWeather by viewModel?.roomWeather?.collectAsState() ?: remember { mutableStateOf(RoomWeather.AUTO) }
    val isSoundscapePlaying by viewModel?.isSoundscapePlaying?.collectAsState() ?: remember { mutableStateOf(false) }
    val activeSoundscapeId by viewModel?.activeSoundscapeId?.collectAsState() ?: remember { mutableStateOf<String?>(null) }

    val wallArt = activeDecorMap["wall_art"] ?: "plant_shelf"
    val windowFrame = activeDecorMap["window_frame"] ?: "classic"
    val windowPlant = activeDecorMap["window_plant"] ?: "succulents"
    val deskLamp = activeDecorMap["desk_lamp"] ?: "classic_lamp"
    val deskMug = activeDecorMap["desk_mug"] ?: "green_tea"
    val rugStyle = activeDecorMap["rug_style"] ?: "cream_fluffy"

    val calendar = remember { java.util.Calendar.getInstance() }
    val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val effectiveWeather = when (roomWeather) {
        RoomWeather.SUNNY -> RoomWeather.SUNNY
        RoomWeather.RAINY -> RoomWeather.RAINY
        RoomWeather.SNOWY -> RoomWeather.SNOWY
        RoomWeather.SUNSET -> RoomWeather.SUNSET
        RoomWeather.AUTO -> when {
            isBedtimeMode || currentHour >= 19 || currentHour < 7 -> RoomWeather.AUTO
            currentHour in 17..18 -> RoomWeather.SUNSET
            else -> RoomWeather.SUNNY
        }
    }
    val isNightTime = isBedtimeMode || (effectiveWeather == RoomWeather.AUTO && (currentHour >= 19 || currentHour < 7))
    val isSunset = effectiveWeather == RoomWeather.SUNSET
    val isRainy = effectiveWeather == RoomWeather.RAINY
    val isSnowy = effectiveWeather == RoomWeather.SNOWY

    var lastTickStep by remember { mutableIntStateOf(0) }

    var isExpanded by remember { mutableStateOf(false) }
    var expandableContentHeightPx by remember { mutableFloatStateOf(with(density) { 150.dp.toPx() }) }
    // Collapsed shift: leaves the header prominently elevated upwards (~36dp higher up)
    val maxCollapsedShiftPx = (expandableContentHeightPx - with(density) { 36.dp.toPx() }).coerceAtLeast(0f)

    var isDragging by remember { mutableStateOf(false) }
    var dragProgressOffset by remember { mutableFloatStateOf(with(density) { 114.dp.toPx() }) }

    // When collapsed: offset is maxCollapsedShiftPx (prominently elevated and visible at bottom)
    // When expanded: offset is 0f (fully open above navigation bar)
    val targetOffset = if (isExpanded) 0f else maxCollapsedShiftPx
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) dragProgressOffset else targetOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "sheet_offset"
    )
    val currentSheetOffset = if (isDragging) dragProgressOffset else animatedOffset

    // Drag progress from 0f (collapsed) to 1f (expanded)
    val dragProgress = (1f - (currentSheetOffset / maxCollapsedShiftPx.coerceAtLeast(1f))).coerceIn(0f, 1f)

    // Mochi Screen Rise: Mochi and the room smoothly move upwards WITH the pop up bar!
    val mochiMaxRisePx = with(density) { (-185).dp.toPx() }
    val mochiRoomOffsetY = mochiMaxRisePx * dragProgress

    var isPetBouncing by remember { mutableStateOf(false) }
    val localPetScale by animateFloatAsState(
        targetValue = if (isPetBouncing) 1.18f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fs_pet_scale",
        finishedListener = { isPetBouncing = false }
    )

    val wallGradient = when {
        isNightTime -> listOf(Color(0xFF0F1123), Color(0xFF1B1934), Color(0xFF121422))
        isSunset -> listOf(Color(0xFFFBE4D8), Color(0xFFFFCCBC), Color(0xFFFFAB91))
        isRainy -> listOf(Color(0xFFCFD8DC), Color(0xFFB0BEC5), Color(0xFF90A4AE))
        isSnowy -> listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB), Color(0xFFE1F5FE))
        androidx.compose.foundation.isSystemInDarkTheme() -> listOf(Color(0xFF1E2235), Color(0xFF171928), Color(0xFF11131E))
        else -> listOf(Color(0xFFE8F0FE), Color(0xFFF3E8FF), Color(0xFFFFF0F5))
    }

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
                .background(Brush.verticalGradient(wallGradient))
                // Full-screen vertical drag gesture: dragging up/down ANYWHERE on the screen moves the bottom sheet!
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            isDragging = true
                            dragProgressOffset = currentSheetOffset
                        },
                        onDragEnd = {
                            isDragging = false
                            isExpanded = dragProgressOffset < (maxCollapsedShiftPx * 0.5f)
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            dragProgressOffset = (dragProgressOffset + dragAmount).coerceIn(0f, maxCollapsedShiftPx)
                            val step = (dragProgressOffset / with(density) { 28.dp.toPx() }).toInt()
                            if (step != lastTickStep) {
                                lastTickStep = step
                                DeskPetAudioHaptics.performTactileTick(context)
                            }
                        }
                    )
                }
        ) {
            // Deep Floor Background: Anchored to the bottom half so even when Mochi moves up,
            // the warm floor covers the bottom with ZERO black screen!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.60f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            if (isNightTime) listOf(Color(0xFF1E172E), Color(0xFF140F20))
                            else if (isSunset) listOf(Color(0xFFD69E7B), Color(0xFFBD825E))
                            else listOf(Color(0xFFEAD5C3), Color(0xFFDEBEA5))
                        )
                    )
            )

            // Moving Room Canvas: Mochi, rug, table, beanbag, and room decor
            // smoothly glide upwards WITH the pop up bar!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, mochiRoomOffsetY.roundToInt()) }
            ) {
                // Room Baseboard & Cozy Wooden Floor Plane
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.40f)
                        .align(Alignment.BottomCenter)
                ) {
                    // Baseboard molding
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                if (isNightTime) Color(0xFF2A2038)
                                else if (isSunset) Color(0xFFB57E58)
                                else Color(0xFFD4A373)
                            )
                    )
                    // Wooden / warm floor gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp)
                            .background(
                                Brush.verticalGradient(
                                    if (isNightTime) {
                                        listOf(Color(0xFF1E172E), Color(0xFF140F20))
                                    } else if (isSunset) {
                                        listOf(Color(0xFFD69E7B), Color(0xFFBD825E))
                                    } else {
                                        listOf(Color(0xFFEAD5C3), Color(0xFFDEBEA5))
                                    }
                                )
                            )
                    )
                }

                // Fullscreen Ceiling Fairy Lights
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 6.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(13) { index ->
                        val bulbColor = when (index % 4) {
                            0 -> Color(0xFFFFE082)
                            1 -> Color(0xFFFFCC80)
                            2 -> Color(0xFFFF8A80)
                            else -> Color(0xFF80D8FF)
                        }
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(bulbColor)
                                .shadow(4.dp, CircleShape)
                        )
                    }
                }

                // Big Beautiful Arched Wall Window (Interactive Tap-to-Customize Zone)
                CozyWindowView(
                    width = 150.dp,
                    height = 190.dp,
                    isNightTime = isNightTime,
                    isSunset = isSunset,
                    isRainy = isRainy,
                    isSnowy = isSnowy,
                    windowFrame = windowFrame,
                    windowPlant = windowPlant,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 74.dp, end = 18.dp),
                    onClick = { onOpenDecorZone(DecorZone.WINDOW) }
                )

                // Cozy Wall Bookshelf & Plant on Left Wall (Interactive Tap-to-Customize Zone)
                CozyBookshelfArt(
                    wallArt = wallArt,
                    scale = 1.35f,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(top = 80.dp, start = 18.dp),
                    onClick = { onOpenDecorZone(DecorZone.WALL) }
                )

                // Room Floor Area with Furniture & Mochi (Interactive Tap-to-Customize Zones)
                // Grounded comfortably above the bottom sheet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 88.dp)
                ) {
                    // Large Fluffy Center Plush Rug (Interactive Tap-to-Customize Zone)
                    CozyPlushRug(
                        rugStyle = rugStyle,
                        width = 280.dp,
                        height = 86.dp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-36).dp),
                        onClick = { onOpenDecorZone(DecorZone.RUG_LOUNGE) }
                    )

                    // Left Furniture: Desk with customizable lamp & mug (Interactive Tap-to-Customize Zone)
                    CozyDeskDecor(
                        isBedtimeMode = isNightTime,
                        lampStyle = deskLamp,
                        mugStyle = deskMug,
                        scale = 1.05f,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 18.dp, bottom = 52.dp),
                        onClick = { onOpenDecorZone(DecorZone.DESK) }
                    )

                    // Right Furniture: Big Cozy Beanbag / Lounge (Interactive Tap-to-Customize Zone)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = 54.dp)
                            .clickable { onOpenDecorZone(DecorZone.RUG_LOUNGE) }
                    ) {
                        Image(
                            painter = painterResource(id = loungeDrawableRes),
                            contentDescription = "Cozy Beanbag",
                            modifier = Modifier.size(142.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Center: Mochi Character in HD (Grounded on the rug, interactive cuddle & purr)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-40).dp)
                            .scale(localPetScale),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        PetModelView(
                            pet = pet,
                            animationState = animationState,
                            engine = animationEngine,
                            onPetClick = {
                                isPetBouncing = true
                                onPetClick()
                            },
                            onStateSelect = onStateSelect,
                            onToggleEngine = onToggleEngine,
                            showStateSelector = false
                        )
                    }
                }
            }

            // Top Bar with Room Title, Atmosphere Controls & Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp, start = 14.dp, end = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    shadowElevation = 5.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🏡", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "${pet.name}'s Sanctuary",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap any decor to customize",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Weather Quick Toggle Chip
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable {
                            val next = when (roomWeather) {
                                RoomWeather.AUTO -> RoomWeather.SUNNY
                                RoomWeather.SUNNY -> RoomWeather.RAINY
                                RoomWeather.RAINY -> RoomWeather.SUNSET
                                RoomWeather.SUNSET -> RoomWeather.SNOWY
                                RoomWeather.SNOWY -> RoomWeather.AUTO
                            }
                            viewModel?.setRoomWeather(next)
                        }
                    ) {
                        Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                            Text(roomWeather.emoji, fontSize = 16.sp)
                        }
                    }

                    // Ambience Audio Toggle Chip
                    Surface(
                        shape = CircleShape,
                        color = if (isSoundscapePlaying) PrimaryFixed.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable {
                            if (!isSoundscapePlaying) {
                                viewModel?.toggleStandaloneSoundscape("rain")
                            } else {
                                when (activeSoundscapeId) {
                                    "rain" -> viewModel?.toggleStandaloneSoundscape("lofi")
                                    "lofi" -> viewModel?.toggleStandaloneSoundscape("fire")
                                    "fire" -> viewModel?.toggleStandaloneSoundscape("purr")
                                    "purr" -> viewModel?.toggleStandaloneSoundscape("purr")
                                    else -> viewModel?.toggleStandaloneSoundscape("rain")
                                }
                            }
                        }
                    ) {
                        Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                            Text(
                                if (isSoundscapePlaying) {
                                    when (activeSoundscapeId) {
                                        "rain" -> "🌧️"
                                        "lofi" -> "🎧"
                                        "fire" -> "🔥"
                                        "purr" -> "🐱"
                                        else -> "🎵"
                                    }
                                } else "🔇",
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Decorate Zone Sheet Button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable {
                            onOpenDecorZone(DecorZone.WALL)
                        }
                    ) {
                        Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                            Text("🎨", fontSize = 16.sp)
                        }
                    }

                    // Close Button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .testTag("close_fullscreen_room_btn")
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Fullscreen",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Full-Width Draggable Bottom Sheet Tray:
            // Sits at the very bottom edge with 0 margins, full screen width.
            // In collapsed state, the header is raised prominently so it's impossible to miss and easy to tap/drag.
            // When dragged up or tapped: expands to reveal pet dialogue and the animation state selector pills.
            // Strictly clamped so it cannot overdrag or expose any black screen.
            Surface(
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                shadowElevation = 14.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset { IntOffset(0, currentSheetOffset.roundToInt()) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Drag Handle Header (Clearly visible, elevated, easy to see, tap and drag)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isExpanded = !isExpanded
                            }
                            .padding(top = 10.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(52.dp)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f))
                        )
                        Surface(
                            shape = CircleShape,
                            color = if (isExpanded) MaterialTheme.colorScheme.surfaceContainerHigh else PrimaryFixed.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, if (isExpanded) MaterialTheme.colorScheme.outlineVariant else Primary.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = if (isExpanded) MaterialTheme.colorScheme.onSurfaceVariant else OnPrimaryFixedVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isExpanded) "Drag down to close" else "✨ Pet Animations & Moods",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpanded) MaterialTheme.colorScheme.onSurfaceVariant else OnPrimaryFixedVariant
                                )
                            }
                        }
                    }

                    // 2. Expandable Content: Speech Bubble & Animation Mood Selector
                    // Revealed when the sheet is dragged upwards
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                if (coordinates.size.height > 0) {
                                    val measuredHeight = coordinates.size.height.toFloat()
                                    if (expandableContentHeightPx != measuredHeight) {
                                        expandableContentHeightPx = measuredHeight
                                        if (!isExpanded && !isDragging) {
                                            dragProgressOffset = (measuredHeight - with(density) { 36.dp.toPx() }).coerceAtLeast(0f)
                                        }
                                    }
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pet Dialogue Speech Bubble
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
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
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f, fill = false)
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

                        // Generous gap after animation options before the bottom line
                        Spacer(modifier = Modifier.height(44.dp))
                    }
                }
            }
        }
    }
}

/**
 * Cozy custom table lamp with glowing shade, bronze neck, and solid base.
 */
@Composable
private fun CozyDeskLamp(
    isBedtimeMode: Boolean,
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    val shadeWidth = (32 * scale).dp
    val shadeHeight = (22 * scale).dp
    val neckWidth = (4 * scale).coerceAtLeast(2f).dp
    val neckHeight = (16 * scale).dp
    val baseWidth = (22 * scale).dp
    val baseHeight = (4 * scale).coerceAtLeast(2f).dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Lamp Shade (warm cozy trapezoid shape with subtle glow)
        Box(
            modifier = Modifier
                .size(width = shadeWidth, height = shadeHeight)
                .shadow(
                    elevation = if (isBedtimeMode) (8 * scale).dp else (3 * scale).dp,
                    shape = RoundedCornerShape(
                        topStart = (8 * scale).dp,
                        topEnd = (8 * scale).dp,
                        bottomStart = (3 * scale).dp,
                        bottomEnd = (3 * scale).dp
                    ),
                    spotColor = Color(0xFFFFD54F)
                )
                .clip(
                    RoundedCornerShape(
                        topStart = (8 * scale).dp,
                        topEnd = (8 * scale).dp,
                        bottomStart = (3 * scale).dp,
                        bottomEnd = (3 * scale).dp
                    )
                )
                .background(
                    Brush.verticalGradient(
                        if (isBedtimeMode) listOf(Color(0xFFFFEE58), Color(0xFFFFB300))
                        else listOf(Color(0xFFFFF9C4), Color(0xFFFFE082))
                    )
                )
                .border(
                    1.dp,
                    Color(0xFFFFCA28).copy(alpha = 0.8f),
                    RoundedCornerShape(
                        topStart = (8 * scale).dp,
                        topEnd = (8 * scale).dp,
                        bottomStart = (3 * scale).dp,
                        bottomEnd = (3 * scale).dp
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Warm inner illumination rim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((4 * scale).coerceAtLeast(2f).dp)
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }

        // Lamp Neck / Stand (metallic brass / warm bronze)
        Box(
            modifier = Modifier
                .width(neckWidth)
                .height(neckHeight)
                .background(Color(0xFF8D6E63))
        )

        // Lamp Base (flat base resting directly on the table)
        Box(
            modifier = Modifier
                .width(baseWidth)
                .height(baseHeight)
                .clip(RoundedCornerShape((2 * scale).coerceAtLeast(1f).dp))
                .background(Color(0xFF6D4C41))
        )
    }
}

@Composable
private fun CozyWindowView(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    isNightTime: Boolean,
    isSunset: Boolean,
    isRainy: Boolean,
    isSnowy: Boolean,
    windowFrame: String,
    windowPlant: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "window_weather")
    val weatherPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "weather_phase"
    )

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = width, height = height)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(topStart = width / 2, topEnd = width / 2, bottomStart = 8.dp, bottomEnd = 8.dp)
                )
                .clip(RoundedCornerShape(topStart = width / 2, topEnd = width / 2, bottomStart = 8.dp, bottomEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        when {
                            isNightTime -> listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81))
                            isSunset -> listOf(Color(0xFFFF7043), Color(0xFFFFB74D), Color(0xFFFFE082))
                            isRainy -> listOf(Color(0xFF546E7A), Color(0xFF78909C), Color(0xFFB0BEC5))
                            isSnowy -> listOf(Color(0xFF90A4AE), Color(0xFFCFD8DC), Color(0xFFECEFF1))
                            else -> listOf(Color(0xFF42A5F5), Color(0xFF90CAF9), Color(0xFFE3F2FD))
                        }
                    )
                )
                .border(
                    width = 3.dp,
                    color = when (windowFrame) {
                        "win_stained" -> Color(0xFFFFD54F).copy(alpha = 0.95f)
                        else -> Color.White.copy(alpha = 0.95f)
                    },
                    shape = RoundedCornerShape(topStart = width / 2, topEnd = width / 2, bottomStart = 8.dp, bottomEnd = 8.dp)
                )
        ) {
            // Sky & Celestial Elements
            if (isNightTime) {
                Text("🌙", modifier = Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp), fontSize = (width.value * 0.18).sp)
                Text("⭐", modifier = Modifier.align(Alignment.TopStart).padding(top = 18.dp, start = 10.dp), fontSize = (width.value * 0.11).sp)
                Text("✨", modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp), fontSize = (width.value * 0.10).sp)
                Text("☁️", modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 10.dp, start = 8.dp), fontSize = (width.value * 0.13).sp)
            } else if (isSunset) {
                Text("🌅", modifier = Modifier.align(Alignment.Center).offset(y = (-4).dp), fontSize = (width.value * 0.22).sp)
                Text("☁️", modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp, start = 8.dp), fontSize = (width.value * 0.13).sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((height.value * 0.15).dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 20.dp))
                        .background(Color(0xFFD4A373).copy(alpha = 0.85f))
                )
            } else {
                Text(if (isRainy) "🌧️" else "☀️", modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 8.dp), fontSize = (width.value * 0.20).sp)
                Text("☁️", modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp), fontSize = (width.value * 0.14).sp)
                if (!isRainy && !isSnowy) {
                    Text("🕊️", modifier = Modifier.align(Alignment.TopStart).padding(top = 22.dp, start = 12.dp), fontSize = (width.value * 0.10).sp)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((height.value * 0.15).dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 20.dp))
                        .background(Color(0xFF81C784).copy(alpha = 0.85f))
                )
            }

            // Animated Weather Particles (Raindrops or Snowflakes)
            if (isRainy || isSnowy) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val particleCount = 14
                    for (i in 0 until particleCount) {
                        val startX = (w * ((i * 37) % 100) / 100f)
                        val speedMult = 0.7f + ((i % 5) * 0.15f)
                        val curY = ((weatherPhase * speedMult * h) + (i * 24f)) % h

                        if (isRainy) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.65f),
                                start = Offset(startX, curY),
                                end = Offset(startX - 2.5f, curY + 14f),
                                strokeWidth = 2f,
                                cap = StrokeCap.Round
                            )
                        } else {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.85f),
                                radius = 2.5f,
                                center = Offset(startX + sin(curY * 0.05f) * 4f, curY)
                            )
                        }
                    }
                }
            }

            // Window Mullions (Classic 4-Pane Crossbars)
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.85f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .align(Alignment.Center)
                    .offset(y = 8.dp)
                    .background(Color.White.copy(alpha = 0.85f))
            )

            // Window Frame Decor Accents
            when (windowFrame) {
                "win_ivy" -> {
                    Text(
                        text = "🌿 🍃 🌿",
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
                    )
                }
                "win_chimes" -> {
                    Text(
                        text = "🎐",
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.TopStart).padding(start = 6.dp, top = 6.dp)
                    )
                }
                "win_stained" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0x22E91E63),
                                        Color(0x229C27B0),
                                        Color(0x222196F3),
                                        Color(0x224CAF50),
                                        Color(0x22FFEB3B)
                                    )
                                )
                            )
                    )
                }
            }
        }

        // Window Sill with Cozy Plant & Flowers
        Box(
            modifier = Modifier
                .width(width + 10.dp)
                .height((height.value * 0.08).coerceIn(8.0, 14.0).dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFD4A373))
                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            val floraIcons = when (windowPlant) {
                "plant_succulent" -> listOf("🪴", "🌵", "🪴")
                "plant_bonsai" -> listOf("🎋", "🍵", "🎋")
                "plant_sunflowers" -> listOf("🌻", "🌱", "🌻")
                else -> listOf("🌸", "🌱", "🌷")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                floraIcons.forEach { icon ->
                    Text(icon, fontSize = (width.value * 0.08).sp)
                }
            }
        }
    }
}

@Composable
private fun CozyDeskDecor(
    isBedtimeMode: Boolean,
    lampStyle: String,
    mugStyle: String,
    scale: Float = 1f,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        // Items flush on tabletop
        Row(
            modifier = Modifier
                .width((104 * scale).dp)
                .padding(horizontal = (4 * scale).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            when (lampStyle) {
                "lamp_lava" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = (2 * scale).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = (20 * scale).dp, height = (32 * scale).dp)
                                .shadow((4 * scale).dp, RoundedCornerShape((8 * scale).dp), spotColor = Color(0xFFFF5722))
                                .clip(RoundedCornerShape((8 * scale).dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFFE91E63))
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape((8 * scale).dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌋", fontSize = (12 * scale).sp)
                        }
                        Box(
                            modifier = Modifier
                                .size(width = (14 * scale).dp, height = (4 * scale).dp)
                                .background(Color(0xFF616161), RoundedCornerShape(2.dp))
                        )
                    }
                }
                "lamp_lantern" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = (2 * scale).dp)
                    ) {
                        Text("🏮", fontSize = (22 * scale).sp)
                    }
                }
                "lamp_star" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = (2 * scale).dp)
                    ) {
                        Text("✨", fontSize = (22 * scale).sp)
                    }
                }
                else -> {
                    CozyDeskLamp(
                        isBedtimeMode = isBedtimeMode,
                        scale = scale
                    )
                }
            }

            val mugEmoji = when (mugStyle) {
                "mug_boba" -> "🧋"
                "mug_coffee" -> "☕"
                "mug_cactus" -> "🌵"
                else -> "🍵"
            }
            Text(
                text = mugEmoji,
                fontSize = (24 * scale).sp,
                modifier = Modifier.offset(y = (3 * scale).dp)
            )
        }

        // Tabletop Surface (Solid warm wood with beveled edge)
        Box(
            modifier = Modifier
                .width((108 * scale).dp)
                .height((16 * scale).dp)
                .shadow((2 * scale).dp, RoundedCornerShape((4 * scale).dp))
                .clip(RoundedCornerShape((4 * scale).dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFD4A373), Color(0xFFBC8A5F))
                    )
                )
                .border(1.dp, Color(0xFFFAEDCD).copy(alpha = 0.5f), RoundedCornerShape((4 * scale).dp))
        )

        // Table Apron
        Box(
            modifier = Modifier
                .width((92 * scale).dp)
                .height((5 * scale).dp)
                .background(Color(0xFFA97858))
        )

        // Table Legs
        Row(
            modifier = Modifier.width((88 * scale).dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(width = (10 * scale).dp, height = (36 * scale).dp)
                    .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFA97858), Color(0xFF8B5E3C))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(width = (10 * scale).dp, height = (36 * scale).dp)
                    .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFA97858), Color(0xFF8B5E3C))
                        )
                    )
            )
        }
    }
}

@Composable
private fun CozyPlushRug(
    rugStyle: String,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val rugBrush = when (rugStyle) {
        "rug_celestial" -> Brush.radialGradient(listOf(Color(0xFF283593), Color(0xFF1A237E)))
        "rug_sakura" -> Brush.radialGradient(listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0)))
        "rug_tatami" -> Brush.linearGradient(listOf(Color(0xFFD7CCC8), Color(0xFFC7B198)))
        else -> Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.95f), Color(0xFFF5F5F5)))
    }
    val rugBorder = when (rugStyle) {
        "rug_celestial" -> Color(0xFFFFD54F).copy(alpha = 0.8f)
        "rug_sakura" -> Color(0xFFEC407A).copy(alpha = 0.7f)
        "rug_tatami" -> Color(0xFF8D6E63).copy(alpha = 0.8f)
        else -> PrimaryFixed.copy(alpha = 0.75f)
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(CircleShape)
            .background(rugBrush)
            .border(2.dp, rugBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (rugStyle == "rug_celestial") {
            Text("✨", fontSize = 14.sp, color = Color(0xFFFFD54F))
        } else if (rugStyle == "rug_sakura") {
            Text("🌸", fontSize = 14.sp)
        }
    }
}

@Composable
private fun CozyBookshelfArt(
    wallArt: String,
    scale: Float = 1f,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        // Mini wall clock & picture frame
        Row(
            horizontalArrangement = Arrangement.spacedBy((6 * scale).dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = (4 * scale).dp, start = (2 * scale).dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke((1.5 * scale).dp, Color(0xFFC89B7B)),
                modifier = Modifier.size((22 * scale).dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val clockEmoji = when (wallArt) {
                        "art_cuckoo" -> "🕰️"
                        "art_polaroids" -> "📸"
                        "art_poster" -> "🖼️"
                        else -> "🕒"
                    }
                    Text(clockEmoji, fontSize = (12 * scale).sp)
                }
            }
            Text(if (wallArt == "art_poster") "🎨" else "🖼️", fontSize = (15 * scale).sp)
        }

        // Shelf items & wooden board
        Row(
            horizontalArrangement = Arrangement.spacedBy((4 * scale).dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = (2 * scale).dp, start = (4 * scale).dp)
        ) {
            Text("🪴", fontSize = (18 * scale).sp)
            Text("📚", fontSize = (15 * scale).sp)
            Text("🕯️", fontSize = (13 * scale).sp)
        }
        Box(
            modifier = Modifier
                .size(width = (80 * scale).dp, height = (8 * scale).dp)
                .clip(RoundedCornerShape((3 * scale).dp))
                .background(Color(0xFFC89B7B))
                .border(1.dp, Color(0xFFA97858), RoundedCornerShape((3 * scale).dp))
        )
    }
}
