package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.DeskPetDatabase
import com.example.data.model.*
import com.example.data.repository.DeskPetRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.data.auth.SupabaseUser
import com.example.ui.sound.AVAILABLE_SOUNDSCAPES
import com.example.ui.sound.DeskPetAudioHaptics
import com.example.ui.sound.SoothingSoundscapeEngine
import com.example.ui.sound.SoundscapeItem

enum class RoomWeather(val title: String, val emoji: String) {
    AUTO("Auto Live", "🌤️"),
    SUNNY("Sunny Day", "☀️"),
    RAINY("Gentle Rain", "🌧️"),
    SNOWY("Winter Snow", "❄️"),
    SUNSET("Golden Sunset", "🌅")
}

enum class AppNavTab(val title: String, val iconName: String) {
    ROOM("Room", "cottage"),
    FOCUS("Focus", "timer"),
    MISSIONS("Missions", "task_alt"),
    PETS("Pets", "cruelty_free"),
    SHOP("Shop", "storefront")
}

enum class SanctuaryHubTab(val title: String, val iconEmoji: String) {
    FOCUS("Focus", "🎯"),
    BREATHING("Breathe", "🧘"),
    SOUNDSCAPES("Sounds", "🌙")
}

enum class BreathingPhase(val label: String, val instruction: String) {
    INHALE("Inhale", "Breathe in deeply through your nose..."),
    HOLD_INHALE("Hold", "Hold gently and embrace the calm..."),
    EXHALE("Exhale", "Release slowly and relax completely..."),
    HOLD_EXHALE("Rest", "Rest in the quiet stillness...")
}

enum class PetAnimationState(val label: String, val emoji: String) {
    IDLE("Idle", "💤"),
    HAPPY("Happy", "💖"),
    FEED("Feed", "🍓"),
    PLAY("Wave", "👋"),
    ZEN("Think", "🤔"),
    SLEEP("Sleep", "🌙"),
    SAD("Sad", "🥺")
}

enum class PetAnimationEngine(val displayName: String) {
    RIVE_RUNTIME("Rive Runtime"),
    STITCH_MODEL("Stitch 3D Model")
}

class DeskPetViewModel(
    application: Application,
    private val user: SupabaseUser? = null
) : AndroidViewModel(application) {

    private val repository: DeskPetRepository

    init {
        val userId = user?.id?.ifBlank { user.email } ?: "guest"
        val displayName = user?.displayName?.ifBlank { "Companion" } ?: "Companion"
        val username = user?.username?.ifBlank { "friend" } ?: "friend"
        val avatarUrl = user?.avatarUrl?.ifBlank { "avatar_user" } ?: "avatar_user"

        val database = DeskPetDatabase.getDatabaseForUser(
            context = application,
            userId = userId,
            displayName = displayName,
            username = username
        )
        repository = DeskPetRepository(database.dao())

        // Seed if first time
        viewModelScope.launch {
            if (database.dao().getActivePet().firstOrNull() == null) {
                DeskPetDatabase.populateInitialData(
                    dao = database.dao(),
                    userId = userId,
                    username = username,
                    displayName = displayName,
                    avatarUrl = avatarUrl
                )
            } else {
                repository.updateUserProfileFromAuth(
                    displayName = displayName,
                    username = username,
                    avatarUrl = avatarUrl
                )
            }
        }
    }

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(AppNavTab.ROOM)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    // Reactive Data Flows
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfile()
        )

    val activePet: StateFlow<PetItem> = repository.activePet
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PetItem(
                id = "mochi",
                name = "Mochi",
                title = "Sprout Kitten",
                drawableResName = "mochi_featured"
            )
        )

    val allPets: StateFlow<List<PetItem>> = repository.allPets
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val allTasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val allMissions: StateFlow<List<MissionItem>> = repository.allMissions
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val weeklyQuest: StateFlow<WeeklyQuestItem?> = repository.weeklyQuest
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val shopProducts: StateFlow<List<ShopProduct>> = repository.shopProducts
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val roomDecor: StateFlow<List<RoomDecor>> = repository.roomDecor
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val latestMoodEntry: StateFlow<MoodEntry?> = repository.latestMoodEntry
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val allMoodEntries: StateFlow<List<MoodEntry>> = repository.allMoodEntries
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val mindfulSessions: StateFlow<List<MindfulSession>> = repository.mindfulSessions
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Sanctuary Hub Mode (Focus / Breathing / Soundscapes)
    private val _activeSanctuaryTab = MutableStateFlow(SanctuaryHubTab.FOCUS)
    val activeSanctuaryTab: StateFlow<SanctuaryHubTab> = _activeSanctuaryTab.asStateFlow()

    fun selectSanctuaryTab(tab: SanctuaryHubTab) {
        _activeSanctuaryTab.value = tab
    }

    // Daily Mood Check-In Sheet
    private val _showMoodCheckInDialog = MutableStateFlow(false)
    val showMoodCheckInDialog: StateFlow<Boolean> = _showMoodCheckInDialog.asStateFlow()

    fun setMoodCheckInDialogVisible(visible: Boolean) {
        _showMoodCheckInDialog.value = visible
    }

    fun recordMood(moodKey: String, note: String = "") {
        viewModelScope.launch {
            val result = repository.recordMoodCheckIn(moodKey, note)
            setAnimationState(PetAnimationState.ZEN)
            showToast("Mood logged! +25 🪙 & +40 Pet Joy 🌸")
        }
    }

    // Focus Timer State
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(25 * 60)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _selectedPreset = MutableStateFlow("25")
    val selectedPreset: StateFlow<String> = _selectedPreset.asStateFlow()

    private val _selectedSoundscape = MutableStateFlow("Lo-Fi Cafe")
    val selectedSoundscape: StateFlow<String> = _selectedSoundscape.asStateFlow()

    private val _focusTaskName = MutableStateFlow("UI Design Systems 🎨")
    val focusTaskName: StateFlow<String> = _focusTaskName.asStateFlow()

    fun updateFocusTaskName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank()) {
            _focusTaskName.value = trimmed
            showToast("Focus goal updated! 🎯")
        }
    }

    private var timerJob: Job? = null

    init {
        startTimerTicker()
    }

    private fun startTimerTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isTimerRunning.value && _remainingSeconds.value > 0) {
                    _remainingSeconds.value -= 1
                    if (_remainingSeconds.value == 0) {
                        _isTimerRunning.value = false
                        onTimerFinished()
                    }
                }
            }
        }
    }

    fun toggleTimer() {
        _isTimerRunning.value = !_isTimerRunning.value
    }

    fun selectPreset(preset: String, customMinutes: Int = 15) {
        _selectedPreset.value = preset
        val duration = when (preset) {
            "25" -> 25 * 60
            "50" -> 50 * 60
            else -> customMinutes * 60
        }
        _totalDurationSeconds.value = duration
        _remainingSeconds.value = duration
        _isTimerRunning.value = false
    }

    fun selectSoundscape(name: String) {
        _selectedSoundscape.value = name
        showToast("Playing $name")
    }

    fun giveUpTimer() {
        _isTimerRunning.value = false
        _remainingSeconds.value = _totalDurationSeconds.value
        showToast("Session cancelled. Mochi is taking a walk! 🐾")
    }

    private fun onTimerFinished() {
        val minutes = _totalDurationSeconds.value / 60
        viewModelScope.launch {
            repository.completeFocusSession(minutes, _selectedSoundscape.value)
            showToast("Focus complete! +50 Coins & +100 Pet XP 🎉")
        }
    }

    // Pet Reactions & Stateful Animation Machine (Rive & Native)
    private val _animationState = MutableStateFlow(PetAnimationState.IDLE)
    val animationState: StateFlow<PetAnimationState> = _animationState.asStateFlow()

    private val _animationEngine = MutableStateFlow(PetAnimationEngine.RIVE_RUNTIME)
    val animationEngine: StateFlow<PetAnimationEngine> = _animationEngine.asStateFlow()

    private val _petHeartAnim = MutableStateFlow(false)
    val petHeartAnim: StateFlow<Boolean> = _petHeartAnim.asStateFlow()

    private val _petBerryAnim = MutableStateFlow(false)
    val petBerryAnim: StateFlow<Boolean> = _petBerryAnim.asStateFlow()

    private val _petWiggleAnim = MutableStateFlow(false)
    val petWiggleAnim: StateFlow<Boolean> = _petWiggleAnim.asStateFlow()

    fun setAnimationState(state: PetAnimationState) {
        _animationState.value = state
        _petHeartAnim.value = (state == PetAnimationState.HAPPY)
        _petBerryAnim.value = (state == PetAnimationState.FEED)
        _petWiggleAnim.value = (state == PetAnimationState.PLAY)
        if (state != PetAnimationState.IDLE) {
            viewModelScope.launch {
                delay(2400)
                if (_animationState.value == state) {
                    _animationState.value = PetAnimationState.IDLE
                    _petHeartAnim.value = false
                    _petBerryAnim.value = false
                    _petWiggleAnim.value = false
                }
            }
        }
    }

    fun toggleAnimationEngine() {
        _animationEngine.value = if (_animationEngine.value == PetAnimationEngine.RIVE_RUNTIME) {
            PetAnimationEngine.STITCH_MODEL
        } else {
            PetAnimationEngine.RIVE_RUNTIME
        }
        val name = if (_animationEngine.value == PetAnimationEngine.RIVE_RUNTIME) "Rive Interactive Runtime" else "Stitch 3D Model"
        showToast("Switched pet display to $name")
    }

    fun petCompanion() {
        viewModelScope.launch {
            repository.petCompanion(activePet.value.id)
            setAnimationState(PetAnimationState.HAPPY)
            DeskPetAudioHaptics.performPurrHaptic(getApplication())
            DeskPetAudioHaptics.playPurrSound()
        }
    }

    fun feedPet() {
        viewModelScope.launch {
            val success = repository.feedPet(activePet.value.id)
            if (success) {
                setAnimationState(PetAnimationState.FEED)
                DeskPetAudioHaptics.performTactileTick(getApplication())
                DeskPetAudioHaptics.playCrunchSound()
                showToast("Yummy treat fed to ${activePet.value.name}! 🍓 (+15 Hunger, -10 🪙)")
            } else {
                showToast("Not enough coins to buy snacks! Need 10 🪙")
            }
        }
    }

    fun playWithPet() {
        viewModelScope.launch {
            val success = repository.playWithPet(activePet.value.id)
            if (success) {
                setAnimationState(PetAnimationState.PLAY)
                showToast("Played ball with ${activePet.value.name}! 🎾 (+20 Joy, -10 Energy)")
            } else {
                showToast("${activePet.value.name} is too tired to play! Let them rest. 💤")
            }
        }
    }

    fun customizeActivePet(furCoat: String, headwear: String, sound: String) {
        viewModelScope.launch {
            repository.customizePet(activePet.value.id, furCoat, headwear, sound)
            showToast("Wardrobe & Aura saved!")
        }
    }

    fun switchActivePet(petId: String) {
        viewModelScope.launch {
            repository.equipPet(petId)
            showToast("Active companion switched!")
        }
    }

    fun equipPet(petId: String) {
        viewModelScope.launch {
            repository.equipPet(petId)
            val pet = allPets.value.find { it.id == petId }
            val name = pet?.name ?: "Companion"
            showToast("Equipped $name as your active desk pet! 🐾")
        }
    }

    fun claimNimbus() {
        claimFocusHoursPet("nimbus")
    }

    fun claimFocusHoursPet(petId: String) {
        viewModelScope.launch {
            repository.claimFocusHoursPet(petId)
            val pet = allPets.value.find { it.id == petId }
            val name = pet?.name ?: "Companion"
            showToast("$name unlocked from deep focus flow! ✨🐾")
        }
    }

    fun claimWeeklyQuest() {
        viewModelScope.launch {
            val result = repository.claimWeeklyQuestReward()
            if (result != null) {
                showToast(result.second)
            } else {
                showToast("Reach the 5-hour focus target to claim your reward! ⏱️")
            }
        }
    }

    // Tasks Actions
    fun toggleTask(taskId: Long) {
        viewModelScope.launch {
            val result = repository.toggleTask(taskId)
            showToast(result.second)
        }
    }

    fun addNewTask(title: String) {
        val trimmed = title.trim()
        if (trimmed.isBlank()) return
        val activeCount = allTasks.value.count { !it.isCompleted }
        if (activeCount >= 7) {
            showToast("Active limit reached (max 7 tasks)! Complete some tasks first. 📋")
            return
        }
        viewModelScope.launch {
            val success = repository.addTask(trimmed)
            if (success) {
                showToast("New task added to today's list! 📝")
            } else {
                showToast("Active limit reached (max 7 tasks)! Complete some tasks first. 📋")
            }
        }
    }

    fun deleteTask(taskId: Long, onResult: (TaskItem?) -> Unit = {}) {
        viewModelScope.launch {
            val deletedTask = repository.deleteTask(taskId)
            onResult(deletedTask)
        }
    }

    fun restoreTask(task: TaskItem) {
        viewModelScope.launch {
            repository.restoreTask(task)
            showToast("Task restored! ↩️")
        }
    }

    // Mission Actions
    fun claimMission(missionId: Int) {
        viewModelScope.launch {
            val result = repository.claimMissionReward(missionId)
            if (result != null) {
                showToast("Claimed ${result.second}! 🎉")
            }
        }
    }

    fun claimMilestoneChest() {
        viewModelScope.launch {
            val success = repository.claimMilestoneChest()
            if (success) {
                showToast("Golden Sprout Chest opened! +250 Coins & Rare Seed! 🎁")
            }
        }
    }

    // Shop Actions
    private val _previewProduct = MutableStateFlow<ShopProduct?>(null)
    val previewProduct: StateFlow<ShopProduct?> = _previewProduct.asStateFlow()

    fun openProductPreview(product: ShopProduct) {
        _previewProduct.value = product
    }

    fun closeProductPreview() {
        _previewProduct.value = null
    }

    fun purchaseProduct(productId: String) {
        viewModelScope.launch {
            val success = repository.purchaseShopProduct(productId)
            if (success) {
                DeskPetAudioHaptics.performCoinHaptic(getApplication())
                DeskPetAudioHaptics.playCoinChimeSound()
                showToast("Purchased and delivered to inventory! ✨")
                closeProductPreview()
            } else {
                showToast("Not enough coins! Complete focus sessions to earn more 🪙")
            }
        }
    }

    // Dynamic Weather in Room
    private val _roomWeather = MutableStateFlow(RoomWeather.AUTO)
    val roomWeather: StateFlow<RoomWeather> = _roomWeather.asStateFlow()

    fun setRoomWeather(weather: RoomWeather) {
        _roomWeather.value = weather
        DeskPetAudioHaptics.performTactileTick(getApplication())
        showToast("Weather set to ${weather.emoji} ${weather.title}")
    }

    // Room Decor Customization & "Try in Room" Preview
    private val _previewDecorOverrides = MutableStateFlow<Map<String, String>>(emptyMap())
    val previewDecorOverrides: StateFlow<Map<String, String>> = _previewDecorOverrides.asStateFlow()

    private val _activePreviewProduct = MutableStateFlow<ShopProduct?>(null)
    val activePreviewProduct: StateFlow<ShopProduct?> = _activePreviewProduct.asStateFlow()

    fun previewProductInRoom(product: ShopProduct) {
        val slot = mapProductToSlot(product)
        _previewDecorOverrides.value = _previewDecorOverrides.value + (slot to product.id)
        _activePreviewProduct.value = product
        selectTab(AppNavTab.ROOM)
        DeskPetAudioHaptics.playWoodTapSound()
        DeskPetAudioHaptics.performTactileTick(getApplication())
        showToast("Previewing '${product.name}' in your room! 🛋️✨")
    }

    fun clearRoomPreview() {
        _previewDecorOverrides.value = emptyMap()
        _activePreviewProduct.value = null
        DeskPetAudioHaptics.performTactileTick(getApplication())
    }

    fun confirmPreviewPurchase() {
        val product = _activePreviewProduct.value ?: return
        viewModelScope.launch {
            val success = repository.purchaseShopProduct(product.id)
            if (success) {
                DeskPetAudioHaptics.performCoinHaptic(getApplication())
                DeskPetAudioHaptics.playCoinChimeSound()
                val slot = mapProductToSlot(product)
                repository.setRoomDecorSlot(slot, product.id, true)
                _previewDecorOverrides.value = emptyMap()
                _activePreviewProduct.value = null
                showToast("Purchased & permanently placed in your room! ✨🛋️")
            } else {
                showToast("Not enough coins! Complete focus sessions to earn more 🪙")
            }
        }
    }

    fun setRoomDecorSlot(slotId: String, itemId: String) {
        viewModelScope.launch {
            DeskPetAudioHaptics.playWoodTapSound()
            DeskPetAudioHaptics.performTactileTick(getApplication())
            repository.setRoomDecorSlot(slotId, itemId, true)
            _previewDecorOverrides.value = _previewDecorOverrides.value - slotId
            showToast("Room updated! ✨")
        }
    }

    private fun mapProductToSlot(product: ShopProduct): String {
        return when {
            product.category == "themes" || product.id.contains("floor") || product.id.contains("rug") || product.id.contains("tatami") || product.id.contains("carpet") -> "rug_style"
            product.id.contains("lamp") || product.id.contains("lantern") || product.id.contains("projector") || product.id.contains("light") -> "desk_lamp"
            product.id.contains("tea") || product.id.contains("boba") || product.id.contains("cup") || product.id.contains("drink") -> "desk_mug"
            product.id.contains("beanbag") || product.id.contains("pillow") || product.id.contains("chair") -> "beanbag"
            product.id.contains("bonsai") || product.id.contains("succulent") || product.id.contains("plant") -> "window_plant"
            product.id.contains("ivy") || product.id.contains("chime") || product.id.contains("stained") -> "window_frame"
            product.id.contains("clock") || product.id.contains("poster") || product.id.contains("polaroid") -> "wall_art"
            else -> "beanbag"
        }
    }

    // Profile Settings Dialog
    private val _showProfileSheet = MutableStateFlow(false)
    val showProfileSheet: StateFlow<Boolean> = _showProfileSheet.asStateFlow()

    fun setProfileSheetVisible(visible: Boolean) {
        _showProfileSheet.value = visible
    }

    // Pet Overview Dialog (Navbar Pet Icon Click)
    private val _showPetOverviewSheet = MutableStateFlow(false)
    val showPetOverviewSheet: StateFlow<Boolean> = _showPetOverviewSheet.asStateFlow()

    fun setPetOverviewSheetVisible(visible: Boolean) {
        _showPetOverviewSheet.value = visible
    }

    fun updateUserProfile(displayName: String, avatarUrl: String) {
        viewModelScope.launch {
            repository.updateUserProfile(displayName, avatarUrl)
            showToast("Profile updated successfully! ✨")
        }
    }

    // Global Toast / Celebration Event
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2800)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    // Guided Zen Breathing State
    private val _selectedBreathingTechnique = MutableStateFlow(AVAILABLE_BREATHING_TECHNIQUES[0])
    val selectedBreathingTechnique: StateFlow<BreathingTechnique> = _selectedBreathingTechnique.asStateFlow()

    private val _isBreathingActive = MutableStateFlow(false)
    val isBreathingActive: StateFlow<Boolean> = _isBreathingActive.asStateFlow()

    private val _breathingPhase = MutableStateFlow(BreathingPhase.INHALE)
    val breathingPhase: StateFlow<BreathingPhase> = _breathingPhase.asStateFlow()

    private val _breathingPhaseSecondsRemaining = MutableStateFlow(4)
    val breathingPhaseSecondsRemaining: StateFlow<Int> = _breathingPhaseSecondsRemaining.asStateFlow()

    private val _breathingTotalDurationSeconds = MutableStateFlow(60) // 1 min default
    val breathingTotalDurationSeconds: StateFlow<Int> = _breathingTotalDurationSeconds.asStateFlow()

    private val _breathingElapsedSeconds = MutableStateFlow(0)
    val breathingElapsedSeconds: StateFlow<Int> = _breathingElapsedSeconds.asStateFlow()

    private val _breathingCyclesCompleted = MutableStateFlow(0)
    val breathingCyclesCompleted: StateFlow<Int> = _breathingCyclesCompleted.asStateFlow()

    private var breathingJob: Job? = null

    fun selectBreathingTechnique(technique: BreathingTechnique) {
        _selectedBreathingTechnique.value = technique
        resetBreathingSession()
    }

    fun setBreathingDuration(seconds: Int) {
        _breathingTotalDurationSeconds.value = seconds
        resetBreathingSession()
    }

    fun startBreathingSession() {
        if (_isBreathingActive.value) return
        _isBreathingActive.value = true
        setAnimationState(PetAnimationState.ZEN)
        startBreathingTicker()
    }

    fun pauseBreathingSession() {
        _isBreathingActive.value = false
        breathingJob?.cancel()
        breathingJob = null
    }

    fun resetBreathingSession() {
        pauseBreathingSession()
        _breathingPhase.value = BreathingPhase.INHALE
        _breathingPhaseSecondsRemaining.value = _selectedBreathingTechnique.value.inhaleSeconds
        _breathingElapsedSeconds.value = 0
        _breathingCyclesCompleted.value = 0
    }

    private fun startBreathingTicker() {
        breathingJob?.cancel()
        breathingJob = viewModelScope.launch {
            val tech = _selectedBreathingTechnique.value
            _breathingPhaseSecondsRemaining.value = tech.inhaleSeconds
            _breathingPhase.value = BreathingPhase.INHALE

            while (_isBreathingActive.value && _breathingElapsedSeconds.value < _breathingTotalDurationSeconds.value) {
                delay(1000)
                _breathingElapsedSeconds.value += 1
                val remaining = _breathingPhaseSecondsRemaining.value - 1
                if (remaining > 0) {
                    _breathingPhaseSecondsRemaining.value = remaining
                } else {
                    // Transition to next phase
                    when (_breathingPhase.value) {
                        BreathingPhase.INHALE -> {
                            if (tech.holdAfterInhaleSeconds > 0) {
                                _breathingPhase.value = BreathingPhase.HOLD_INHALE
                                _breathingPhaseSecondsRemaining.value = tech.holdAfterInhaleSeconds
                            } else {
                                _breathingPhase.value = BreathingPhase.EXHALE
                                _breathingPhaseSecondsRemaining.value = tech.exhaleSeconds
                            }
                        }
                        BreathingPhase.HOLD_INHALE -> {
                            _breathingPhase.value = BreathingPhase.EXHALE
                            _breathingPhaseSecondsRemaining.value = tech.exhaleSeconds
                        }
                        BreathingPhase.EXHALE -> {
                            if (tech.holdAfterExhaleSeconds > 0) {
                                _breathingPhase.value = BreathingPhase.HOLD_EXHALE
                                _breathingPhaseSecondsRemaining.value = tech.holdAfterExhaleSeconds
                            } else {
                                _breathingCyclesCompleted.value += 1
                                _breathingPhase.value = BreathingPhase.INHALE
                                _breathingPhaseSecondsRemaining.value = tech.inhaleSeconds
                            }
                        }
                        BreathingPhase.HOLD_EXHALE -> {
                            _breathingCyclesCompleted.value += 1
                            _breathingPhase.value = BreathingPhase.INHALE
                            _breathingPhaseSecondsRemaining.value = tech.inhaleSeconds
                        }
                    }
                }
            }

            if (_breathingElapsedSeconds.value >= _breathingTotalDurationSeconds.value) {
                _isBreathingActive.value = false
                onBreathingFinished()
            }
        }
    }

    private fun onBreathingFinished() {
        viewModelScope.launch {
            val techTitle = _selectedBreathingTechnique.value.title
            val duration = _breathingTotalDurationSeconds.value
            val cycles = _breathingCyclesCompleted.value
            repository.completeMindfulSession(techTitle, duration, cycles)
            setAnimationState(PetAnimationState.HAPPY)
            showToast("Breathe session complete! +25 🪙 & Mochi feels so peaceful! 🌸")
        }
    }

    // Standalone Ambient Soundscapes & Sleep Mode
    val soundscapeEngine = SoothingSoundscapeEngine()

    private val _isSoundscapePlaying = MutableStateFlow(false)
    val isSoundscapePlaying: StateFlow<Boolean> = _isSoundscapePlaying.asStateFlow()

    private val _activeSoundscapeId = MutableStateFlow("rain")
    val activeSoundscapeId: StateFlow<String> = _activeSoundscapeId.asStateFlow()

    private val _soundscapeVolume = MutableStateFlow(0.7f)
    val soundscapeVolume: StateFlow<Float> = _soundscapeVolume.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow(0) // 0 = continuous
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    private val _sleepTimerRemainingSeconds = MutableStateFlow(0)
    val sleepTimerRemainingSeconds: StateFlow<Int> = _sleepTimerRemainingSeconds.asStateFlow()

    private val _isBedtimeModeActive = MutableStateFlow(false)
    val isBedtimeModeActive: StateFlow<Boolean> = _isBedtimeModeActive.asStateFlow()

    private var sleepTimerJob: Job? = null

    fun toggleStandaloneSoundscape(soundId: String) {
        if (_isSoundscapePlaying.value && _activeSoundscapeId.value == soundId) {
            // Pause
            soundscapeEngine.stopSoundscape()
            _isSoundscapePlaying.value = false
            sleepTimerJob?.cancel()
            showToast("Ambience paused ⏸️")
        } else {
            // Play
            _activeSoundscapeId.value = soundId
            soundscapeEngine.playSoundscape(soundId)
            _isSoundscapePlaying.value = true
            val name = AVAILABLE_SOUNDSCAPES.find { it.id == soundId }?.title ?: soundId
            showToast("Now playing: $name 🎧")
            if (_sleepTimerMinutes.value > 0) {
                startSleepTimerCountdown()
            }
        }
    }

    fun setSoundscapeVolume(vol: Float) {
        _soundscapeVolume.value = vol
        soundscapeEngine.setVolume(vol)
    }

    fun setSleepTimer(minutes: Int) {
        _sleepTimerMinutes.value = minutes
        _sleepTimerRemainingSeconds.value = minutes * 60
        if (minutes > 0) {
            showToast("Sleep timer set to ${minutes}m 🌙")
            if (_isSoundscapePlaying.value) {
                startSleepTimerCountdown()
            }
        } else {
            sleepTimerJob?.cancel()
            showToast("Sleep timer turned off ∞")
        }
    }

    private fun startSleepTimerCountdown() {
        sleepTimerJob?.cancel()
        sleepTimerJob = viewModelScope.launch {
            while (_isSoundscapePlaying.value && _sleepTimerRemainingSeconds.value > 0) {
                delay(1000)
                _sleepTimerRemainingSeconds.value -= 1
                if (_sleepTimerRemainingSeconds.value == 0) {
                    soundscapeEngine.stopSoundscape()
                    _isSoundscapePlaying.value = false
                    showToast("Sleep timer complete. Sweet dreams! 🌙💤")
                }
            }
        }
    }

    fun toggleBedtimeMode() {
        _isBedtimeModeActive.value = !_isBedtimeModeActive.value
        if (_isBedtimeModeActive.value) {
            setAnimationState(PetAnimationState.SLEEP)
            showToast("Bedtime mode activated: Warm twilight & sleepy Mochi 🌙")
        } else {
            setAnimationState(PetAnimationState.IDLE)
            showToast("Day mode restored ☀️")
        }
    }

    override fun onCleared() {
        soundscapeEngine.release()
        breathingJob?.cancel()
        sleepTimerJob?.cancel()
        super.onCleared()
    }
}

class DeskPetViewModelFactory(
    private val application: Application,
    private val user: SupabaseUser?
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeskPetViewModel::class.java)) {
            return DeskPetViewModel(application, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
