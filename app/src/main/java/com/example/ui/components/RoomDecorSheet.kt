package com.example.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DeskPetViewModel
import com.example.ui.theme.*

enum class DecorZone(val title: String, val emoji: String) {
    WALL("Wall & Art", "🎨"),
    WINDOW("Window", "🪟"),
    DESK("Desk & Lamp", "🛋️"),
    RUG_LOUNGE("Rug & Lounge", "🧶")
}

data class DecorOption(
    val id: String,
    val slotId: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val previewColor: Color? = null
)

val WALL_TONE_OPTIONS = listOf(
    DecorOption("wall_pastel", "wall_style", "Pastel Lavender", "Soft cozy ambient lilac", "💜", Color(0xFFF3E8FF)),
    DecorOption("wall_sunset", "wall_style", "Golden Sunset", "Warm amber peach glow", "🌅", Color(0xFFFFE0B2)),
    DecorOption("wall_midnight", "wall_style", "Midnight Starlight", "Deep serene indigo night", "🌙", Color(0xFF1E1B4B)),
    DecorOption("wall_birch", "wall_style", "Rustic Timber", "Warm natural woodland cedar", "🪵", Color(0xFFD7CCC8)),
    DecorOption("wall_mint", "wall_style", "Fresh Matcha Mint", "Peaceful botanical green", "🌱", Color(0xFFE8F5E9))
)

val WALL_ART_OPTIONS = listOf(
    DecorOption("art_clock", "wall_art", "Classic Wall Clock", "Minimalist ivory quartz clock", "🕒"),
    DecorOption("art_polaroids", "wall_art", "Focus Milestones", "Snapshots of study memories", "📸"),
    DecorOption("art_poster", "wall_art", "Mochi Study Poster", "Cute inspirational pet portrait", "🖼️"),
    DecorOption("art_cuckoo", "wall_art", "Woodland Clock", "Carved alpine pinecone clock", "🕰️")
)

val WINDOW_FRAME_OPTIONS = listOf(
    DecorOption("win_classic", "window_frame", "Classic White Arch", "Clean architectural mullions", "🪟"),
    DecorOption("win_ivy", "window_frame", "Hanging Ivy Vines", "Cascading green woodland leaves", "🌿"),
    DecorOption("win_chimes", "window_frame", "Bamboo Wind Chimes", "Gentle resonant porch chimes", "🎐"),
    DecorOption("win_stained", "window_frame", "Stained Glass Accent", "Prismatic sunbeam pattern", "🌈")
)

val WINDOW_PLANT_OPTIONS = listOf(
    DecorOption("plant_flowers", "window_plant", "Window Sill Flowers", "Pink cherry blossom & tulip pots", "🌸"),
    DecorOption("plant_succulent", "window_plant", "Succulent Trio", "Calm jade & aloe terracotta pots", "🪴"),
    DecorOption("plant_bonsai", "window_plant", "Zen Juniper Bonsai", "Sculpted meditative Japanese tree", "🎋"),
    DecorOption("plant_sunflowers", "window_plant", "Mini Sunflowers", "Cheerful bright yellow blooms", "🌻")
)

val DESK_LAMP_OPTIONS = listOf(
    DecorOption("lamp_classic", "desk_lamp", "Cozy Amber Shade", "Warm glowing bedside lamp", "💡"),
    DecorOption("lamp_lava", "desk_lamp", "Retro Lava Lamp", "Slow undulating wax magma glow", "🌋"),
    DecorOption("lamp_lantern", "desk_lamp", "Moroccan Lantern", "Ornate punched brass candlelight", "🏮"),
    DecorOption("lamp_star", "desk_lamp", "Nebula Projector", "Swirling ceiling cosmic starlight", "✨")
)

val DESK_MUG_OPTIONS = listOf(
    DecorOption("mug_tea", "desk_mug", "Matcha Green Tea", "Freshly brewed warm ceremonial tea", "🍵"),
    DecorOption("mug_boba", "desk_mug", "Brown Sugar Boba", "Iced milk tea with sweet pearls", "🧋"),
    DecorOption("mug_coffee", "desk_mug", "Ceramic Latte", "Rich drip coffee with foam art", "☕"),
    DecorOption("mug_cactus", "desk_mug", "Desktop Cactus", "Tiny potted desert friend", "🌵")
)

val RUG_STYLE_OPTIONS = listOf(
    DecorOption("rug_cloud", "rug_style", "Fluffy Cloud Cream", "Ultra-soft round white plush rug", "☁️"),
    DecorOption("rug_celestial", "rug_style", "Celestial Stars", "Deep indigo silver constellation carpet", "✨"),
    DecorOption("rug_sakura", "rug_style", "Sakura Blossom Mat", "Delicate pink cherry petal circle", "🌸"),
    DecorOption("rug_tatami", "rug_style", "Straw Rush Tatami", "Traditional woven herbal flooring", "🎋")
)

val LOUNGE_OPTIONS = listOf(
    DecorOption("lounge_beanbag", "beanbag", "Corduroy Beanbag", "Plump cozy beanbag chair", "🛋️"),
    DecorOption("lounge_sloth", "beanbag", "Giant Sloth Plush", "Huge cuddly nap pillow", "🦥"),
    DecorOption("lounge_cushion", "beanbag", "Zen Floor Cushion", "Round meditation floor seat", "🧘"),
    DecorOption("lounge_armchair", "beanbag", "Velvet Shell Seat", "Deep retro mustard lounge seat", "🪑")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDecorSheet(
    viewModel: DeskPetViewModel,
    activeDecorMap: Map<String, String>,
    initialZone: DecorZone = DecorZone.WALL,
    onDismiss: () -> Unit
) {
    var selectedZone by remember { mutableStateOf(initialZone) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customize Room Decor 🎨",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap any item to instantly decorate your pet's space",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Zone Selection Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DecorZone.entries.forEach { zone ->
                    val isSelected = selectedZone == zone
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = if (isSelected) null else BorderStroke(1.dp, Outline.copy(alpha = 0.2f)),
                        modifier = Modifier.clickable { selectedZone = zone }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(zone.emoji, fontSize = 14.sp)
                            Text(
                                text = zone.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Zone Options Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedZone) {
                    DecorZone.WALL -> {
                        DecorSectionTitle("Wall Ambiance & Wallpaper", "Choose your room's wall paint and mood")
                        DecorGrid(WALL_TONE_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }

                        DecorSectionTitle("Wall Hanging & Art", "Clocks, memory polaroids, and posters")
                        DecorGrid(WALL_ART_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }
                    }
                    DecorZone.WINDOW -> {
                        DecorSectionTitle("Window Arch & Accents", "Vines, wind chimes, and frames")
                        DecorGrid(WINDOW_FRAME_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }

                        DecorSectionTitle("Window Sill Flora", "Pots of flowers, bonsai, and succulents")
                        DecorGrid(WINDOW_PLANT_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }
                    }
                    DecorZone.DESK -> {
                        DecorSectionTitle("Desk Lamp Style", "Warm glow lamps, lava lamps, and lanterns")
                        DecorGrid(DESK_LAMP_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }

                        DecorSectionTitle("Desktop Mug & Refreshment", "Warm tea, coffee, and iced boba")
                        DecorGrid(DESK_MUG_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }
                    }
                    DecorZone.RUG_LOUNGE -> {
                        DecorSectionTitle("Plush Floor Rug", "Soft center carpet for Mochi to relax on")
                        DecorGrid(RUG_STYLE_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }

                        DecorSectionTitle("Corner Lounge & Seating", "Beanbags, plushies, and floor chairs")
                        DecorGrid(LOUNGE_OPTIONS, activeDecorMap) { opt ->
                            viewModel.setRoomDecorSlot(opt.slotId, opt.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DecorSectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun DecorGrid(
    options: List<DecorOption>,
    activeDecorMap: Map<String, String>,
    onSelect: (DecorOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowOptions.forEach { opt ->
                    val isEquipped = (activeDecorMap[opt.slotId] ?: options.first().id) == opt.id
                    DecorCard(
                        option = opt,
                        isEquipped = isEquipped,
                        onClick = { onSelect(opt) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DecorCard(
    option: DecorOption,
    isEquipped: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (isEquipped) PrimaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            if (isEquipped) 2.dp else 1.dp,
            if (isEquipped) Primary else Outline.copy(alpha = 0.25f)
        ),
        shadowElevation = if (isEquipped) 3.dp else 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = option.previewColor ?: MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(option.iconEmoji, fontSize = 18.sp)
                    }
                }

                if (isEquipped) {
                    Surface(
                        shape = CircleShape,
                        color = Primary
                    ) {
                        Text(
                            text = "EQUIPPED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Text(
                text = option.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = option.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                maxLines = 2,
                fontSize = 11.sp
            )
        }
    }
}

