package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.example.R
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.ui.DeskPetViewModel
import com.example.ui.SanctuaryHubTab
import com.example.ui.components.AmbientSleepSection
import com.example.ui.components.ZenBreathingSection
import com.example.ui.theme.*

@Composable
fun FocusTimerScreen(
    viewModel: DeskPetViewModel,
    profile: UserProfile,
    tasks: List<TaskItem>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isRunning by viewModel.isTimerRunning.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val totalSeconds by viewModel.totalDurationSeconds.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val selectedSoundscape by viewModel.selectedSoundscape.collectAsState()
    val focusTaskName by viewModel.focusTaskName.collectAsState()
    val activeSanctuaryTab by viewModel.activeSanctuaryTab.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var editedGoalInput by remember(focusTaskName) { mutableStateOf(focusTaskName) }

    var currentTimeMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTimeMs = System.currentTimeMillis()
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)
    val progressFraction = (remainingSeconds.toFloat() / totalSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sanctuary Mode Pill Switcher - Justified in 1 Clean Line
            Surface(
                shape = CircleShape,
                color = CozyCardBackground,
                border = BorderStroke(1.dp, CozyCardBorder),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SanctuaryHubTab.entries.forEach { tab ->
                        val isSelected = tab == activeSanctuaryTab
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) Primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clickable { viewModel.selectSanctuaryTab(tab) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = tab.iconEmoji, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else OnSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

            when (activeSanctuaryTab) {
                SanctuaryHubTab.BREATHING -> {
                    ZenBreathingSection(viewModel = viewModel)
                }
                SanctuaryHubTab.SOUNDSCAPES -> {
                    AmbientSleepSection(viewModel = viewModel)
                }
                SanctuaryHubTab.FOCUS -> {
                    // Study Partner Banner
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SecondaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("💖", fontSize = 11.sp)
                            Text(
                                text = "Focus Partner",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSecondaryContainer
                            )
                        }
                    }

                    Text(
                        text = "Study & Work with Mochi",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "\"You're doing splendidly! Just $minutes minutes to our yummy tea break.\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.mochi_study),
                    contentDescription = "Mochi Studying",
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Presets Segmented Selector
        Surface(
            shape = CircleShape,
            color = SurfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PresetTab(
                    label = "25m Pomodoro",
                    isSelected = selectedPreset == "25",
                    onClick = { viewModel.selectPreset("25") },
                    modifier = Modifier.weight(1f)
                )
                PresetTab(
                    label = "50m Deep Work",
                    isSelected = selectedPreset == "50",
                    onClick = { viewModel.selectPreset("50") },
                    modifier = Modifier.weight(1f)
                )
                PresetTab(
                    label = "Custom",
                    isSelected = selectedPreset == "custom",
                    onClick = { viewModel.selectPreset("custom", 15) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Radial Progress Timer Hero Card
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Circular Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {
                    val timerTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    val timerPrimary = MaterialTheme.colorScheme.primary
                    val timerPrimaryContainer = MaterialTheme.colorScheme.primaryContainer
                    val timerSecondary = MaterialTheme.colorScheme.secondary
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = timerTrackColor,
                            style = Stroke(width = 24f)
                        )
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(timerPrimary, timerPrimaryContainer, timerSecondary, timerPrimary)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * progressFraction,
                            useCenter = false,
                            style = Stroke(width = 24f, cap = StrokeCap.Round)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🌱", fontSize = 14.sp)
                            Text(
                                text = "DEEP FOCUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = CircleShape,
                            color = SurfaceContainerLow,
                            border = BorderStroke(1.dp, Primary.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    editedGoalInput = focusTaskName
                                    showEditGoalDialog = true
                                }
                                .testTag("edit_focus_goal_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Working on: $focusTaskName",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Goal",
                                    tint = Primary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }

                // Controls
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleTimer() },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) TertiaryContainer else Primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("toggle_timer_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Text(
                                text = if (isRunning) "Pause Session" else "Resume Session",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    TextButton(
                        onClick = { viewModel.giveUpTimer() },
                        modifier = Modifier.testTag("give_up_timer_button")
                    ) {
                        Text(
                            text = "Give Up (Take Mochi for a walk)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Outline
                        )
                    }
                }
            }
        }

        // Ambient Soundscapes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Ambient Soundscape",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text("4 Available", style = MaterialTheme.typography.labelSmall, color = Outline)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SoundscapeChip("☕ Lo-Fi Cafe", selectedSoundscape == "Lo-Fi Cafe") {
                    viewModel.selectSoundscape("Lo-Fi Cafe")
                }
                SoundscapeChip("🌧️ Rain on Window", selectedSoundscape == "Rain on Window") {
                    viewModel.selectSoundscape("Rain on Window")
                }
                SoundscapeChip("🔥 Cozy Campfire", selectedSoundscape == "Cozy Campfire") {
                    viewModel.selectSoundscape("Cozy Campfire")
                }
                SoundscapeChip("🍃 White Noise", selectedSoundscape == "White Noise") {
                    viewModel.selectSoundscape("White Noise")
                }
            }
        }

        // 7-Day Study Streak Card (Uncramped & Clean)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 3.dp,
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
                            .size(48.dp)
                            .background(TertiaryFixed, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 24.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "${profile.currentStreak}-Day Study Streak!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Daily Multiplier: 1.5x Coins",
                            style = MaterialTheme.typography.bodySmall,
                            color = Tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = TertiaryFixed
                ) {
                    Text(
                        text = "Day ${profile.currentStreak}/7",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnTertiaryFixedVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Weekly Streak Circles Row (Under the card with ample room)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Focus Habit",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${profile.currentStreak} of 7 Days Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = Outline
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val dayLetters = listOf("M", "T", "W", "T", "F", "S", "S")

                    dayLetters.forEachIndexed { index, letter ->
                        val isDone = index < profile.currentStreak
                        val isCurrentDay = index == profile.currentStreak - 1

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDone) Secondary else SurfaceContainerHigh
                                    )
                                    .then(
                                        if (isCurrentDay) Modifier.border(2.dp, Primary, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = letter,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = dayNames[index],
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = if (isDone) Secondary else Outline,
                                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Today's Focus Tasks
        val activeTasksCount = tasks.count { !it.isCompleted }
        val isMaxTasksReached = activeTasksCount >= 7

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Today's Focus Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isMaxTasksReached) TertiaryFixed else SecondaryFixed
                    ) {
                        Text(
                            text = "$activeTasksCount/7 Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isMaxTasksReached) OnTertiaryFixedVariant else OnSecondaryFixedVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (tasks.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CozyCardBackground,
                    border = BorderStroke(1.dp, CozyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("📋", fontSize = 28.sp)
                        Text(
                            text = "No focus tasks yet",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Add up to 7 tasks to stay on track & earn coins!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Outline
                        )
                    }
                }
            } else {
                tasks.forEach { task ->
                    key(task.id) {
                        SlidableTaskTile(
                            task = task,
                            currentTimeMs = currentTimeMs,
                            onToggle = { viewModel.toggleTask(task.id) },
                            onDelete = {
                                viewModel.deleteTask(task.id) { deleted ->
                                    if (deleted != null) {
                                        scope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Task \"${task.title}\" removed",
                                                actionLabel = "Undo",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.restoreTask(deleted)
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Add new task button
            OutlinedButton(
                onClick = {
                    if (isMaxTasksReached) {
                        viewModel.showToast("Maximum 7 active tasks reached! Complete some tasks first. 📋")
                    } else {
                        showAddTaskDialog = true
                    }
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isMaxTasksReached) OutlineVariant else PrimaryFixedDim),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isMaxTasksReached) SurfaceContainerLow else Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("add_task_trigger_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isMaxTasksReached) Icons.Default.Lock else Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = if (isMaxTasksReached) Outline else Primary
                    )
                    Text(
                        text = if (isMaxTasksReached) "Active Task Limit Reached (7/7)" else "Add new task",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isMaxTasksReached) Outline else Primary
                    )
                }
            }
        }

        // Daily Summary Bento Strip
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val dateFormatted = remember {
                        java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date())
                    }
                    Text("Daily Summary", style = MaterialTheme.typography.titleMedium)
                    Text("Today, $dateFormatted", style = MaterialTheme.typography.labelSmall, color = Outline)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        icon = "⏱️",
                        value = "${(profile.totalFocusMinutes / 60.0).let { "%.1f".format(it) }} hrs",
                        label = "Focused",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = "✅",
                        value = "${profile.totalFocusSessions}",
                        label = "Sessions",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = "🪙",
                        value = "${profile.coins}",
                        label = "Coins Earned",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(16.dp),
                containerColor = CozyCardBackgroundElevated,
                contentColor = MaterialTheme.colorScheme.onSurface,
                actionColor = Primary,
                dismissActionContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("New Focus Task", style = MaterialTheme.typography.titleLarge) },
            text = {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("What would you like to achieve?") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addNewTask(newTaskTitle)
                        newTaskTitle = ""
                        showAddTaskDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.testTag("confirm_add_task_btn")
                ) {
                    Text("Add Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditGoalDialog) {
        AlertDialog(
            onDismissRequest = { showEditGoalDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎯", fontSize = 20.sp)
                    Text(
                        text = "Edit Focus Goal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "What would you like to focus on during this session?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )

                    OutlinedTextField(
                        value = editedGoalInput,
                        onValueChange = { editedGoalInput = it },
                        label = { Text("Focus Topic or Goal") },
                        placeholder = { Text("e.g. Study Math, Coding, Writing") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_goal_input")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Quick Suggestions:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Outline
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "UI Design Systems 🎨",
                                "Study & Homework 📚",
                                "Code & Debug 💻",
                                "Writing & Research ✍️",
                                "Deep Reading 📖",
                                "Creative Flow ✨"
                            ).forEach { suggestion ->
                                Surface(
                                    shape = CircleShape,
                                    color = if (editedGoalInput == suggestion) PrimaryFixed else SurfaceContainerLow,
                                    border = BorderStroke(
                                        1.dp,
                                        if (editedGoalInput == suggestion) Primary else CozyCardBorder
                                    ),
                                    modifier = Modifier.clickable { editedGoalInput = suggestion }
                                ) {
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (editedGoalInput == suggestion) FontWeight.Bold else FontWeight.Normal,
                                        color = if (editedGoalInput == suggestion) OnPrimaryFixedVariant else OnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedGoalInput.isNotBlank()) {
                            viewModel.updateFocusTaskName(editedGoalInput)
                            showEditGoalDialog = false
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier.testTag("save_focus_goal_btn")
                ) {
                    Text("Save Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = CozyCardBackground
        )
    }
}

@Composable
private fun PresetTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) SurfaceContainerLowest else Color.Transparent,
        label = "preset_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Primary else OnSurfaceVariant,
        label = "preset_text"
    )

    Box(
        modifier = modifier
            .height(38.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun SoundscapeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) PrimaryFixed else SurfaceContainerLowest,
        shadowElevation = 2.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) OnPrimaryFixedVariant else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SlidableTaskTile(
    task: TaskItem,
    currentTimeMs: Long,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val density = LocalDensity.current
    val maxSwipePx = remember(density) { with(density) { 88.dp.toPx() } }
    val thresholdPx = remember(density) { with(density) { 44.dp.toPx() } }
    val offsetX = remember(task.id) { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
    ) {
        // Background Reveal Action Layer (Trash bin button)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFEF4444).copy(alpha = 0.16f),
            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
            modifier = Modifier.matchParentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDC2626),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable {
                            coroutineScope.launch {
                                offsetX.animateTo(0f)
                            }
                            onDelete()
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Task",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Foreground Slidable Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(task.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (offsetX.value < -thresholdPx) {
                                    offsetX.animateTo(-maxSwipePx, spring(stiffness = Spring.StiffnessMediumLow))
                                } else {
                                    offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, spring())
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                val target = (offsetX.value + dragAmount).coerceIn(-maxSwipePx, 0f)
                                offsetX.snapTo(target)
                            }
                        }
                    )
                }
        ) {
            TaskTileContent(
                task = task,
                currentTimeMs = currentTimeMs,
                isRevealed = offsetX.value < -10f,
                onCloseReveal = {
                    coroutineScope.launch {
                        offsetX.animateTo(0f, spring())
                    }
                },
                onToggle = onToggle
            )
        }
    }
}

@Composable
private fun TaskTileContent(
    task: TaskItem,
    currentTimeMs: Long,
    isRevealed: Boolean,
    onCloseReveal: () -> Unit,
    onToggle: () -> Unit
) {
    val elapsedMs = currentTimeMs - task.createdAt
    val minFocusMs = 5 * 60 * 1000L
    val isLocked = !task.isCompleted && elapsedMs < minFocusMs
    val remainingSec = if (isLocked) ((minFocusMs - elapsedMs) / 1000L).coerceAtLeast(1) else 0L
    val remMin = remainingSec / 60
    val remS = remainingSec % 60
    val timeRemainingStr = if (remMin > 0) "${remMin}m ${remS}s" else "${remS}s"

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (task.isCompleted) CozyCardBackgroundElevated else CozyCardBackground,
        border = BorderStroke(1.dp, CozyCardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isRevealed) {
                    Modifier.clickable(onClick = onCloseReveal)
                } else {
                    Modifier
                }
            )
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                task.isCompleted -> Secondary
                                isLocked -> SurfaceContainerHigh
                                else -> PrimaryFixed
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                task.isCompleted -> Secondary
                                isLocked -> OutlineVariant
                                else -> Primary
                            },
                            CircleShape
                        )
                        .clickable(enabled = !isRevealed, onClick = onToggle)
                        .testTag("task_check_${task.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        task.isCompleted -> {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        isLocked -> {
                            Text("⏳", fontSize = 12.sp)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) Outline else MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+${task.coinsReward} 🪙",
                            style = MaterialTheme.typography.labelSmall,
                            color = Tertiary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+${task.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "‹ slide to remove",
                            style = MaterialTheme.typography.labelSmall,
                            color = Outline.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            if (task.isCompleted) {
                Surface(
                    shape = CircleShape,
                    color = SecondaryContainer
                ) {
                    Text(
                        text = "Done ✓",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            } else if (isLocked) {
                Surface(
                    shape = CircleShape,
                    color = TertiaryFixed.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, Tertiary.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable(enabled = !isRevealed, onClick = onToggle)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("⏳", fontSize = 11.sp)
                        Text(
                            text = timeRemainingStr,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnTertiaryFixedVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                Surface(
                    shape = CircleShape,
                    color = PrimaryFixed.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable(enabled = !isRevealed, onClick = onToggle)
                ) {
                    Text(
                        text = "Tap to Complete",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnPrimaryFixedVariant,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLow,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 18.sp)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Outline)
        }
    }
}
