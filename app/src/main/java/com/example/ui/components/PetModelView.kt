package com.example.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Alignment as RiveAlignment
import app.rive.runtime.kotlin.core.Fit as RiveFit
import app.rive.runtime.kotlin.core.Loop as RiveLoop
import app.rive.runtime.kotlin.core.Rive
import com.example.R
import com.example.data.model.PetItem
import com.example.ui.PetAnimationEngine
import com.example.ui.PetAnimationState
import com.example.ui.theme.*

@Composable
fun PetModelView(
    pet: PetItem,
    animationState: PetAnimationState,
    engine: PetAnimationEngine,
    onPetClick: () -> Unit,
    onStateSelect: (PetAnimationState) -> Unit = {},
    onToggleEngine: () -> Unit = {},
    showStateSelector: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isHappy = animationState == PetAnimationState.HAPPY
    val isFeeding = animationState == PetAnimationState.FEED
    val isPlaying = animationState == PetAnimationState.PLAY
    val isIdle = animationState == PetAnimationState.IDLE

    // 1. Idle breathing and floating bob animation (infinite loop)
    val infiniteTransition = rememberInfiniteTransition(label = "pet_idle")
    val idleBreathingY by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_breathing"
    )

    val idleBobbingOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bob"
    )

    val idleTilt by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_tilt"
    )

    // 2. Action Animations (Stateful transitions)
    // Happy Petting Bounce & Purr
    val happyScaleX by animateFloatAsState(
        targetValue = if (isHappy) 1.16f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "happy_squash_x"
    )
    val happyScaleY by animateFloatAsState(
        targetValue = if (isHappy) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "happy_squash_y"
    )

    // Feeding munch animation (quick vertical bounce chew)
    val feedChewScaleY by animateFloatAsState(
        targetValue = if (isFeeding) 1.14f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "feed_chew"
    )

    // Playing leap & spin animation
    val playJumpOffset by animateFloatAsState(
        targetValue = if (isPlaying) -32f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "play_jump"
    )
    val playRotation by animateFloatAsState(
        targetValue = if (isPlaying) 18f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "play_rot"
    )

    // Aura glow based on fur coat color
    val auraGlowColor = when (pet.furCoatColor) {
        "peach" -> SwatchPeach
        "mint" -> SwatchMint
        "butter" -> SwatchButter
        else -> SwatchLavender
    }

    // Resolving pet drawable model matching Pets tab exactly
    val petDrawable = pet.getDrawableResId()

    // Resolving matching Rive animation resource
    val riveRawRes = when {
        pet.id == "mochi" || pet.drawableResName.contains("mochi") -> R.raw.pet_mochi
        pet.drawableResName == "pet_shiba" -> R.raw.pet_dog
        pet.drawableResName in listOf("pet_hamster", "pet_piggy") -> R.raw.pet_piggy
        pet.drawableResName in listOf("pet_owl", "pet_axolotl", "pet_boba") -> R.raw.pet_mascot
        else -> R.raw.pet_mochi
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Floating action emotion speech tag & particles
        Box(
            modifier = Modifier.height(34.dp),
            contentAlignment = Alignment.Center
        ) {
            when (animationState) {
                PetAnimationState.HAPPY -> {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("💖", fontSize = 14.sp)
                            Text(
                                text = "Happy Purr! (+10 Joy)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                PetAnimationState.FEED -> {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("🍓", fontSize = 14.sp)
                            Text(
                                text = "Yum! Munch Munch! (+15 Food)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                PetAnimationState.PLAY -> {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("🎾", fontSize = 14.sp)
                            Text(
                                text = "Wheee! Bounce! (+20 Joy)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                PetAnimationState.IDLE -> {
                    // Subtle cozy indicator
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.85f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text("💤", fontSize = 12.sp)
                            Text(
                                text = "Cozy & Resting",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                PetAnimationState.ZEN -> {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("🧘", fontSize = 14.sp)
                            Text(
                                text = "Deep Breath & Calm 🌸",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                PetAnimationState.SLEEP -> {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("🌙", fontSize = 14.sp)
                            Text(
                                text = "Sleeping peacefully... Zzz",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                PetAnimationState.SAD -> {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("🥺", fontSize = 14.sp)
                            Text(
                                text = "Aww... It's okay! We'll do it! (+5 Love)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Pet Model Container
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .width(220.dp)
                .height(210.dp)
        ) {
            // Shadow on the plush rug below the pet (grounded in place)
            Box(
                modifier = Modifier
                    .size(width = 135.dp, height = 26.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.18f),
                                Color.Black.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Dynamic Soft Aura Glow behind the model
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(y = (-20).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                auraGlowColor.copy(alpha = 0.35f),
                                auraGlowColor.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Pet Interactive Presentation (Rive Runtime vs Stitch 3D Model)
            if (engine == PetAnimationEngine.RIVE_RUNTIME) {
                // Rive Runtime Animated Character (Grounded, stays in one place)
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onPetClick
                        )
                        .testTag("pet_rive_model_interactive"),
                    contentAlignment = Alignment.Center
                ) {
                    RivePetView(
                        rawResId = riveRawRes,
                        animationState = animationState,
                        petName = pet.name,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay treats / accessory during animations
                    if (isFeeding) {
                        Text(
                            text = "🍓",
                            fontSize = 32.sp,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = 10.dp)
                        )
                    }
                    if (isPlaying) {
                        Text(
                            text = "🎾",
                            fontSize = 30.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 10.dp, y = (-4).dp)
                        )
                    }
                }
            } else {
                // Seamless Transparent Pet Character (Zero floating, grounded in place)
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onPetClick
                        )
                        .testTag("pet_3d_model_interactive"),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = petDrawable),
                            contentDescription = pet.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Blushing cheeks indicator when petted
                        if (isHappy) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .align(Alignment.Center),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF8A80).copy(alpha = 0.65f))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF8A80).copy(alpha = 0.65f))
                                )
                            }
                        }
                    }

                    // Headwear accessory badge/overlay (if equipped in wardrobe)
                    when (pet.headwear) {
                        "headphones" -> {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryFixed,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-10).dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = "Headphones",
                                        tint = Primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text("Audio", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "crown" -> {
                            Surface(
                                shape = CircleShape,
                                color = GoldCoin,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-10).dp)
                            ) {
                                Text("👑", fontSize = 14.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        "beanie" -> {
                            Surface(
                                shape = CircleShape,
                                color = TertiaryFixed,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-10).dp)
                            ) {
                                Text("🧶", fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    // Interactive Treat Falling Animation during Feed
                    if (isFeeding) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = 12.dp)
                        ) {
                            Text("🍓", fontSize = 28.sp)
                        }
                    }

                    // Interactive Ball during Play
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 18.dp, y = (-8).dp)
                        ) {
                            Text("🎾", fontSize = 26.sp)
                        }
                    }
                }
            }
        }

        if (showStateSelector) {
            Spacer(modifier = Modifier.height(10.dp))
            PetAnimationStateSelector(
                animationState = animationState,
                engine = engine,
                onStateSelect = onStateSelect,
                onToggleEngine = onToggleEngine
            )
        }
    }
}

/**
 * Clean, unobstructed state machine pill selector for pet animations & moods.
 * Can be rendered cleanly below the room habitat without colliding with furniture.
 */
@Composable
fun PetAnimationStateSelector(
    animationState: PetAnimationState,
    engine: PetAnimationEngine,
    onStateSelect: (PetAnimationState) -> Unit,
    onToggleEngine: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CozyCardBackground,
        border = BorderStroke(1.dp, CozyCardBorder),
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    Text("✨", fontSize = 14.sp)
                    Text(
                        text = "Pet Animations & Moods",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Engine Mode Toggle (3D Pet Art / Rive Vector)
                Surface(
                    shape = CircleShape,
                    color = if (engine == PetAnimationEngine.STITCH_MODEL) PrimaryFixed else SurfaceContainerHigh,
                    modifier = Modifier
                        .clickable { onToggleEngine() }
                        .testTag("engine_toggle_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (engine == PetAnimationEngine.STITCH_MODEL) Icons.Default.Pets else Icons.Default.Animation,
                            contentDescription = "Switch Engine",
                            tint = if (engine == PetAnimationEngine.STITCH_MODEL) OnPrimaryFixedVariant else OnSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (engine == PetAnimationEngine.STITCH_MODEL) "3D Pet" else "Rive HD",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (engine == PetAnimationEngine.STITCH_MODEL) OnPrimaryFixedVariant else OnSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Horizontally scrollable row of animation pills - clear, legible and zero furniture collisions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PetAnimationState.values().forEach { state ->
                    val isSelected = (animationState == state)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Primary else SurfaceContainerLow,
                        border = if (isSelected) null else BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
                        shadowElevation = if (isSelected) 3.dp else 0.dp,
                        modifier = Modifier
                            .clickable { onStateSelect(state) }
                            .testTag("state_pill_${state.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(state.emoji, fontSize = 14.sp)
                            Text(
                                text = state.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Rive Animation View wrapper connecting Rive runtime with stateful interaction inputs.
 */
@Composable
fun RivePetView(
    rawResId: Int,
    animationState: PetAnimationState,
    petName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Safely check Rive initialization
    LaunchedEffect(Unit) {
        try {
            Rive.init(context)
        } catch (e: Throwable) {
            Log.w("RivePetView", "Rive.init error: ${e.message}")
        }
    }

    var riveViewRef by remember { mutableStateOf<RiveAnimationView?>(null) }

    // Respond reactively to state machine transitions: idle, happy, feed, play
    LaunchedEffect(animationState, riveViewRef) {
        val view = riveViewRef ?: return@LaunchedEffect
        try {
            val artboard = runCatching { view.controller.activeArtboard }.getOrNull() ?: return@LaunchedEffect
            val stateMachineNames = runCatching { artboard.stateMachineNames }.getOrNull() ?: emptyList()
            val animationNames = runCatching { artboard.animationNames }.getOrNull() ?: emptyList()

            // Safe State Machine Interaction: ONLY if a state machine actually exists
            if (stateMachineNames.isNotEmpty()) {
                val smInstances = runCatching { view.controller.stateMachines }.getOrNull() ?: emptyList()
                val activeSm = smInstances.firstOrNull { stateMachineNames.contains(it.name) } ?: smInstances.firstOrNull()
                val smName = activeSm?.name ?: stateMachineNames.first()
                val inputNames = runCatching { activeSm?.inputNames }.getOrNull() ?: emptyList()

                when (animationState) {
                    PetAnimationState.HAPPY -> {
                        if (inputNames.contains("happy")) runCatching { view.fireState(smName, "happy") }
                        if (inputNames.contains("celebrate")) runCatching { view.fireState(smName, "celebrate") }
                        if (inputNames.contains("Pressed")) runCatching { view.fireState(smName, "Pressed") }
                        if (inputNames.contains("isHappy")) runCatching { view.setBooleanState(smName, "isHappy", true) }
                    }
                    PetAnimationState.FEED -> {
                        if (inputNames.contains("feed")) runCatching { view.fireState(smName, "feed") }
                        if (inputNames.contains("happy")) runCatching { view.fireState(smName, "happy") }
                        if (inputNames.contains("celebrate")) runCatching { view.fireState(smName, "celebrate") }
                        if (inputNames.contains("Pressed")) runCatching { view.fireState(smName, "Pressed") }
                        if (inputNames.contains("isFeeding")) runCatching { view.setBooleanState(smName, "isFeeding", true) }
                    }
                    PetAnimationState.PLAY -> {
                        if (inputNames.contains("wave")) runCatching { view.fireState(smName, "wave") }
                        if (inputNames.contains("play")) runCatching { view.fireState(smName, "play") }
                        if (inputNames.contains("happy")) runCatching { view.fireState(smName, "happy") }
                        if (inputNames.contains("Pressed")) runCatching { view.fireState(smName, "Pressed") }
                        if (inputNames.contains("isPlaying")) runCatching { view.setBooleanState(smName, "isPlaying", true) }
                    }
                    PetAnimationState.IDLE -> {
                        if (inputNames.contains("isHappy")) runCatching { view.setBooleanState(smName, "isHappy", false) }
                        if (inputNames.contains("isFeeding")) runCatching { view.setBooleanState(smName, "isFeeding", false) }
                        if (inputNames.contains("isPlaying")) runCatching { view.setBooleanState(smName, "isPlaying", false) }
                        if (inputNames.contains("isSleeping")) runCatching { view.setBooleanState(smName, "isSleeping", false) }
                        if (inputNames.contains("isThinking")) runCatching { view.setBooleanState(smName, "isThinking", false) }
                        if (inputNames.contains("isIdle")) runCatching { view.setBooleanState(smName, "isIdle", true) }
                    }
                    PetAnimationState.ZEN -> {
                        if (inputNames.contains("isThinking")) runCatching { view.setBooleanState(smName, "isThinking", true) }
                        if (inputNames.contains("zen")) runCatching { view.fireState(smName, "zen") }
                        if (inputNames.contains("isZen")) runCatching { view.setBooleanState(smName, "isZen", true) }
                    }
                    PetAnimationState.SLEEP -> {
                        if (inputNames.contains("isSleeping")) runCatching { view.setBooleanState(smName, "isSleeping", true) }
                        if (inputNames.contains("sleep")) runCatching { view.fireState(smName, "sleep") }
                    }
                    PetAnimationState.SAD -> {
                        if (inputNames.contains("sad")) runCatching { view.fireState(smName, "sad") }
                        if (inputNames.contains("isSad")) runCatching { view.setBooleanState(smName, "isSad", true) }
                    }
                }
            }

            // Safe Animation Playback: ONLY for animations that actually exist in the artboard
            if (animationNames.isNotEmpty()) {
                val targetAnim = when (animationState) {
                    PetAnimationState.HAPPY -> animationNames.firstOrNull { it.contains("celebrate", ignoreCase = true) || it.contains("happy", ignoreCase = true) || it.contains("cheer", ignoreCase = true) }
                    PetAnimationState.FEED -> animationNames.firstOrNull { it.contains("feed", ignoreCase = true) || it.contains("eat", ignoreCase = true) || it.contains("celebrate", ignoreCase = true) }
                    PetAnimationState.PLAY -> animationNames.firstOrNull { it.contains("wave", ignoreCase = true) || it.contains("play", ignoreCase = true) || it.contains("jump", ignoreCase = true) }
                    PetAnimationState.IDLE -> animationNames.firstOrNull { it.contains("idle", ignoreCase = true) }
                    PetAnimationState.ZEN -> animationNames.firstOrNull { it.contains("thinking", ignoreCase = true) || it.contains("zen", ignoreCase = true) || it.contains("idle", ignoreCase = true) }
                    PetAnimationState.SLEEP -> animationNames.firstOrNull { it.contains("sleep", ignoreCase = true) || it.contains("rest", ignoreCase = true) || it.contains("idle", ignoreCase = true) }
                    PetAnimationState.SAD -> animationNames.firstOrNull { it.contains("sad", ignoreCase = true) || it.contains("cry", ignoreCase = true) }
                } ?: animationNames.firstOrNull()

                if (targetAnim != null) {
                    runCatching {
                        view.play(
                            targetAnim,
                            if (animationState == PetAnimationState.IDLE) RiveLoop.LOOP else RiveLoop.AUTO,
                            app.rive.runtime.kotlin.core.Direction.AUTO,
                            false
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            Log.d("RivePetView", "State dispatch note: ${e.message}")
        }
    }

    AndroidView(
        factory = { ctx ->
            try {
                Rive.init(ctx)
            } catch (e: Throwable) {
                Log.w("RivePetView", "Rive init in factory: ${e.message}")
            }

            val view = RiveAnimationView.Builder(ctx)
                .setFit(RiveFit.CONTAIN)
                .setAlignment(RiveAlignment.CENTER)
                .setAutoplay(true)
                .build()

            view.isOpaque = false
            view.background = null

            view.tag = rawResId
            try {
                if (rawResId == R.raw.pet_piggy) {
                    view.setRiveResource(
                        rawResId,
                        "Piggy",
                        null,
                        "PiggyMachine",
                        true
                    )
                } else {
                    view.setRiveResource(
                        rawResId,
                        null,
                        null,
                        null,
                        true
                    )
                }
            } catch (e: Throwable) {
                Log.w("RivePetView", "setRiveResource error: ${e.message}")
            }

            riveViewRef = view
            view
        },
        update = { view ->
            riveViewRef = view
            if (view.tag != rawResId) {
                view.tag = rawResId
                try {
                    if (rawResId == R.raw.pet_piggy) {
                        view.setRiveResource(rawResId, "Piggy", null, "PiggyMachine", true)
                    } else {
                        view.setRiveResource(rawResId, null, null, null, true)
                    }
                } catch (e: Throwable) {
                    Log.w("RivePetView", "update setRiveResource error: ${e.message}")
                }
            }
        },
        modifier = modifier
    )
}
