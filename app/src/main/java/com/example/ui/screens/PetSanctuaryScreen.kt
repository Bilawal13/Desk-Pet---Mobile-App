package com.example.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PetItem
import com.example.ui.DeskPetViewModel
import com.example.ui.theme.*

@Composable
fun PetSanctuaryScreen(
    viewModel: DeskPetViewModel,
    activePet: PetItem,
    allPets: List<PetItem>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedHue by remember { mutableStateOf(activePet.furCoatColor) }
    var selectedHeadwear by remember { mutableStateOf(activePet.headwear) }
    var selectedSound by remember { mutableStateOf(activePet.ambientSound) }

    val filteredPets = remember(allPets, selectedCategory) {
        when (selectedCategory) {
            "Common" -> allPets.filter { it.rarity == "Common" }
            "Rare" -> allPets.filter { it.rarity == "Rare" }
            "Legendary" -> allPets.filter { it.rarity == "Legendary" }
            "Event" -> allPets.filter { it.rarity == "Mythic" }
            else -> allPets
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Sanctuary Summary & Stat Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("✨", fontSize = 12.sp)
                    Text(
                        text = "COMPANION HAVEN",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "My Pet Sanctuary",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { viewModel.showToast("Sanctuary guides: Level up pets with focus flow!") },
                modifier = Modifier
                    .size(36.dp)
                    .background(SurfaceContainerLow, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Help",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Stats capsules
        val discoveredCount = allPets.count { it.isUnlocked }
        val rareCount = allPets.count { it.rarity == "Rare" && it.isUnlocked }
        val legendaryCount = allPets.count { (it.rarity == "Legendary" || it.rarity == "Mythic") && it.isUnlocked }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SanctuaryStatCapsule(label = "Discovered", value = "$discoveredCount/${allPets.size}", modifier = Modifier.weight(1f))
            SanctuaryStatCapsule(label = "Rare Spirits", value = "⭐ $rareCount", modifier = Modifier.weight(1f))
            SanctuaryStatCapsule(label = "Legendary", value = "👑 $legendaryCount", modifier = Modifier.weight(1f))
        }

        // Featured Active Companion Card
        Surface(
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                PrimaryFixed.copy(alpha = 0.85f),
                                CozyCardBackground,
                                TertiaryFixed.copy(alpha = 0.55f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = SecondaryFixed) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("💖", fontSize = 11.sp)
                                Text(
                                    text = "Active Companion",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OnSecondaryFixedVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = SurfaceContainerLowest.copy(alpha = 0.85f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Max Bond",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Main companion showcase
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val activeRes = activePet.getDrawableResId()

                        Box(modifier = Modifier.size(92.dp)) {
                            Image(
                                painter = painterResource(id = activeRes),
                                contentDescription = activePet.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(SurfaceContainerLowest),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                shape = CircleShape,
                                color = Primary,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 4.dp, y = 4.dp)
                            ) {
                                Text(
                                    text = "Lvl ${activePet.level}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = activePet.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Secondary))
                                Text(
                                    text = activePet.typeLabel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHigh)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Secondary, SecondaryFixed)
                                                )
                                            )
                                    )
                                }
                                Text(
                                    text = "100% Affinity",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sanctuary Habitat Category Filter Chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sanctuary Habitat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Filter by rarity", style = MaterialTheme.typography.labelSmall, color = Outline)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Common", "Rare", "Legendary", "Event").forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) Primary else SurfaceContainerLow,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = when (cat) {
                                "All" -> "All Pets (${allPets.size})"
                                "Common" -> "Common 🌿"
                                "Rare" -> "Rare ⭐"
                                "Legendary" -> "Legendary 👑"
                                else -> "Event Limited ✨"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White else OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Companion Showcase 2-Column Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val chunkedPets = filteredPets.chunked(2)
            chunkedPets.forEach { rowPets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowPets.forEach { petItem ->
                        PetShowcaseCard(
                            pet = petItem,
                            isActive = petItem.id == activePet.id,
                            onSwitch = { viewModel.switchActivePet(petItem.id) },
                            onClaim = { viewModel.claimFocusHoursPet(petItem.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowPets.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Wardrobe & Aura Customization Drawer
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                .size(36.dp)
                                .background(PrimaryFixed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Wardrobe & Aura",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Customizing: ${activePet.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Outline
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.customizeActivePet(selectedHue, selectedHeadwear, selectedSound)
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp).testTag("save_wardrobe_btn")
                    ) {
                        Text("Save", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Fur Coat Hue Swatches
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Fur Coat Hue",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(
                            "lavender" to SwatchLavender,
                            "peach" to SwatchPeach,
                            "mint" to SwatchMint,
                            "butter" to SwatchButter
                        ).forEach { (hue, color) ->
                            val isSel = selectedHue == hue
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSel) 3.dp else 1.dp,
                                        color = if (isSel) Primary else OutlineVariant,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedHue = hue }
                                    .testTag("swatch_$hue"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Headwear Accessories
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Headwear Accessory",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeadwearOption(
                            title = "Headphones",
                            icon = Icons.Default.Headphones,
                            isSelected = selectedHeadwear == "headphones",
                            onClick = { selectedHeadwear = "headphones" },
                            modifier = Modifier.weight(1f)
                        )
                        HeadwearOption(
                            title = "Crown",
                            icon = Icons.Default.LocalFlorist,
                            isSelected = selectedHeadwear == "crown",
                            onClick = { selectedHeadwear = "crown" },
                            modifier = Modifier.weight(1f)
                        )
                        HeadwearOption(
                            title = "Cozy Beanie",
                            icon = Icons.Default.SportsMartialArts,
                            isSelected = selectedHeadwear == "beanie",
                            onClick = { selectedHeadwear = "beanie" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Companion Sound & Presence
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ambient Sound Presence",
                            style = MaterialTheme.typography.labelMedium
                        )
                        TextButton(
                            onClick = { viewModel.showToast("Sound preview playing! 🎵") }
                        ) {
                            Text("🔊 Preview", style = MaterialTheme.typography.labelSmall, color = Primary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SoundChip(
                            title = "🎵 Purr & Lofi",
                            isSelected = selectedSound == "purr_lofi",
                            onClick = { selectedSound = "purr_lofi" },
                            modifier = Modifier.weight(1f)
                        )
                        SoundChip(
                            title = "Gentle Chimes",
                            isSelected = selectedSound == "gentle_chimes",
                            onClick = { selectedSound = "gentle_chimes" },
                            modifier = Modifier.weight(1f)
                        )
                        SoundChip(
                            title = "Forest Rain",
                            isSelected = selectedSound == "forest_rain",
                            onClick = { selectedSound = "forest_rain" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Evolution Shrine
        Surface(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                TertiaryFixed.copy(alpha = 0.5f),
                                CozyCardBackground,
                                SecondaryFixed.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("⬆️", fontSize = 12.sp)
                            Text(
                                text = "EVOLUTION SHRINE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${activePet.name} Tier ${if (activePet.level >= 5) 2 else 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (activePet.level >= 5) "Mastered spirit bond" else "Reach Level 5 to awaken next tier",
                            style = MaterialTheme.typography.bodySmall,
                            color = Outline
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(SurfaceContainerLowest, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🥚", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SanctuaryStatCapsule(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CozyCardBackground,
        border = BorderStroke(1.dp, CozyCardBorder),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Outline, fontSize = 9.sp)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
        }
    }
}

@Composable
private fun PetShowcaseCard(
    pet: PetItem,
    isActive: Boolean,
    onSwitch: () -> Unit,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (pet.isUnlocked) CozyCardBackground else CozyCardBackgroundElevated,
        border = BorderStroke(1.2.dp, CozyCardBorder),
        shadowElevation = if (pet.isUnlocked) 3.dp else 1.dp,
        modifier = modifier.height(312.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper section: Top Badges + Avatar + Names
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top badges row (fixed height 28.dp for symmetry)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (pet.rarity == "Rare") Tertiary else SurfaceContainerHigh
                    ) {
                        Text(
                            text = if (pet.rarity == "Rare") "⭐ Rare" else "Lvl ${pet.level}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (pet.rarity == "Rare") Color.White else OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (isActive) {
                        Surface(shape = CircleShape, color = SecondaryFixed) {
                            Text(
                                text = "• Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSecondaryFixedVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (pet.isUnlocked) {
                        Button(
                            onClick = onSwitch,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryFixed),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp).testTag("switch_pet_${pet.id}")
                        ) {
                            Text("Switch", style = MaterialTheme.typography.labelSmall, color = Primary)
                        }
                    } else if (pet.id == "nimbus" && pet.focusHoursTarget > 0 && pet.focusHoursCurrent >= pet.focusHoursTarget) {
                        Surface(shape = CircleShape, color = SecondaryContainer) {
                            Text(
                                text = "Ready!",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSecondaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Pet Avatar (uniform 76.dp circular container)
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, CozyCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val resId = pet.getDrawableResId()

                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = pet.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Pet Name & Title (fixed vertical space for alignment)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = pet.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Lower section: Info Box / Progress + Action Button (uniform height & padding)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Info Box or Target Container (fixed height 46.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (pet.id == "nimbus" && !pet.isUnlocked && pet.focusHoursTarget > 0 && pet.focusHoursCurrent >= pet.focusHoursTarget) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SecondaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, SecondaryContainer),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "Ready to claim! 🎉",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (!pet.isUnlocked) {
                        if (pet.focusHoursTarget > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                border = BorderStroke(1.dp, CozyCardBorder),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Focus Hours", style = MaterialTheme.typography.labelSmall, color = Outline, fontSize = 10.sp)
                                        Text("${pet.focusHoursCurrent}/${pet.focusHoursTarget}h", style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceContainerHighest)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth((pet.focusHoursCurrent.toFloat() / pet.focusHoursTarget).coerceIn(0f, 1f))
                                                .fillMaxHeight()
                                                .background(Primary)
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                border = BorderStroke(1.dp, CozyCardBorder),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
                                    Text(
                                        text = pet.unlockRequirement.ifEmpty { "Adopt in Shop" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Outline,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerLow,
                            border = BorderStroke(1.dp, CozyCardBorder),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Type", style = MaterialTheme.typography.labelSmall, color = Outline, fontSize = 10.sp)
                                    Text(pet.typeLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, fontSize = 10.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Bonus", style = MaterialTheme.typography.labelSmall, color = Outline, fontSize = 10.sp)
                                    Text(pet.bonusLabel, style = MaterialTheme.typography.labelSmall, color = Secondary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // Action Button Row (always fixed height 32.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (pet.id == "nimbus" && !pet.isUnlocked && pet.focusHoursTarget > 0 && pet.focusHoursCurrent >= pet.focusHoursTarget) {
                        Button(
                            onClick = onClaim,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.fillMaxSize().testTag("claim_nimbus_btn"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("🎉 Claim Pet!", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (!pet.isUnlocked) {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceContainerHighest,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Outline, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Locked", style = MaterialTheme.typography.labelSmall, color = Outline)
                            }
                        }
                    } else if (isActive) {
                        Surface(
                            shape = CircleShape,
                            color = SecondaryFixed,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = OnSecondaryFixedVariant, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Active Companion", style = MaterialTheme.typography.labelSmall, color = OnSecondaryFixedVariant, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Button(
                            onClick = onSwitch,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.fillMaxSize().testTag("equip_pet_${pet.id}"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Equip Pet", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeadwearOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PrimaryFixed.copy(alpha = 0.5f) else SurfaceContainerLow,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Primary else OnSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Primary else OnSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SoundChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) Primary else SurfaceContainerLow,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
