package com.example.data.repository

import com.example.data.db.DeskPetDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class DeskPetRepository(private val dao: DeskPetDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allPets: Flow<List<PetItem>> = dao.getAllPets()
    val activePet: Flow<PetItem?> = dao.getActivePet()
    val allTasks: Flow<List<TaskItem>> = dao.getAllTasks()
    val allMissions: Flow<List<MissionItem>> = dao.getAllMissions()
    val weeklyQuest: Flow<WeeklyQuestItem?> = dao.getWeeklyQuest()
    val shopProducts: Flow<List<ShopProduct>> = dao.getAllShopProducts()
    val roomDecor: Flow<List<RoomDecor>> = dao.getAllRoomDecor()
    val latestMoodEntry: Flow<MoodEntry?> = dao.getLatestMoodEntry()
    val allMoodEntries: Flow<List<MoodEntry>> = dao.getAllMoodEntries()
    val mindfulSessions: Flow<List<MindfulSession>> = dao.getAllMindfulSessions()

    suspend fun feedPet(petId: String): Boolean {
        val profile = dao.getUserProfile().firstOrNull() ?: return false
        val pet = dao.getPetById(petId) ?: return false

        if (profile.coins < 10) return false

        dao.updateProfile(profile.copy(coins = profile.coins - 10))
        val newHunger = (pet.hunger + 15).coerceAtMost(100)
        val newJoy = (pet.happiness + 5).coerceAtMost(100)
        dao.updatePet(pet.copy(hunger = newHunger, happiness = newJoy))

        // Check Loving Care mission
        updateLovingCareMissionProgress()
        return true
    }

    suspend fun petCompanion(petId: String) {
        val pet = dao.getPetById(petId) ?: return
        val newJoy = (pet.happiness + 10).coerceAtMost(100)
        dao.updatePet(pet.copy(happiness = newJoy))

        updateLovingCareMissionProgress()
    }

    suspend fun playWithPet(petId: String): Boolean {
        val pet = dao.getPetById(petId) ?: return false
        if (pet.energy < 10) return false

        val newEnergy = (pet.energy - 10).coerceAtLeast(0)
        val newJoy = (pet.happiness + 20).coerceAtMost(100)
        dao.updatePet(pet.copy(energy = newEnergy, happiness = newJoy))
        return true
    }

    suspend fun customizePet(petId: String, furCoat: String, headwear: String, sound: String) {
        val pet = dao.getPetById(petId) ?: return
        dao.updatePet(
            pet.copy(
                furCoatColor = furCoat,
                headwear = headwear,
                ambientSound = sound
            )
        )
    }

    suspend fun switchActivePet(petId: String) {
        equipPet(petId)
    }

    suspend fun claimNimbusPet() {
        claimFocusHoursPet("nimbus")
    }

    suspend fun claimFocusHoursPet(petId: String) {
        val pet = dao.getPetById(petId) ?: return
        if (pet.focusHoursCurrent >= pet.focusHoursTarget && pet.focusHoursTarget > 0) {
            dao.updatePet(pet.copy(isUnlocked = true, unlockRequirement = ""))
            equipPet(petId)

            val shopId = "shop_pet_$petId"
            val product = dao.getAllShopProducts().firstOrNull()?.find { it.id == shopId }
            if (product != null) {
                dao.updateShopProduct(product.copy(isOwned = true, isEquipped = true))
            }
        }
    }

    suspend fun toggleTask(taskId: Long): Pair<Boolean, String> {
        val tasks = dao.getAllTasks().firstOrNull() ?: return Pair(false, "Task not found")
        val task = tasks.find { it.id == taskId } ?: return Pair(false, "Task not found")
        val isNowCompleted = !task.isCompleted

        // Enforce 5-minute focus rule before marking as completed
        if (isNowCompleted) {
            val elapsedMs = System.currentTimeMillis() - task.createdAt
            val minFocusTimeMs = 5 * 60 * 1000L
            if (elapsedMs < minFocusTimeMs) {
                val remSeconds = ((minFocusTimeMs - elapsedMs) / 1000L).coerceAtLeast(1)
                val remMin = remSeconds / 60
                val remSec = remSeconds % 60
                val timeStr = if (remMin > 0) "${remMin}m ${remSec}s" else "${remSec}s"
                return Pair(false, "Must focus for at least 5 mins before completing! ($timeStr left) ⏳")
            }
        }

        val shouldGiveReward = isNowCompleted && !task.rewardClaimed

        dao.updateTask(task.copy(isCompleted = isNowCompleted, rewardClaimed = task.rewardClaimed || isNowCompleted))

        if (shouldGiveReward) {
            val profile = dao.getUserProfile().firstOrNull() ?: return Pair(true, "Task completed!")
            val newCoins = profile.coins + task.coinsReward
            val newXp = profile.xp + task.xpReward
            val (level, xp) = calculateLevel(profile.level, newXp, profile.maxXp)
            dao.insertProfile(profile.copy(coins = newCoins, level = level, xp = xp))

            // Advance Task Master mission
            advanceTaskMasterMission()
            return Pair(true, "Task completed! +${task.coinsReward} Coins & +${task.xpReward} XP 🎉")
        }
        return Pair(true, if (isNowCompleted) "Task completed! ✨" else "Task marked incomplete")
    }

    suspend fun addTask(title: String, priority: String = "Medium"): Boolean {
        val currentTasks = dao.getAllTasks().firstOrNull() ?: emptyList()
        val activeCount = currentTasks.count { !it.isCompleted }
        if (activeCount >= 7) return false

        dao.insertTask(
            TaskItem(
                title = title,
                coinsReward = 30,
                xpReward = 50,
                priority = priority,
                createdAt = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun deleteTask(taskId: Long): TaskItem? {
        val currentTasks = dao.getAllTasks().firstOrNull() ?: return null
        val task = currentTasks.find { it.id == taskId } ?: return null
        dao.deleteTask(task)
        return task
    }

    suspend fun restoreTask(task: TaskItem): Boolean {
        dao.insertTask(task)
        return true
    }

    suspend fun completeFocusSession(durationMinutes: Int, soundscape: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        val coinsEarned = 50
        val xpEarned = 100

        dao.insertFocusRecord(
            FocusRecord(
                durationMinutes = durationMinutes,
                soundscape = soundscape,
                coinsEarned = coinsEarned,
                xpEarned = xpEarned
            )
        )

        val newTotalMinutes = profile.totalFocusMinutes + durationMinutes
        val newTotalSessions = profile.totalFocusSessions + 1
        val newCoins = profile.coins + coinsEarned
        val newXp = profile.xp + xpEarned
        val (level, xp) = calculateLevel(profile.level, newXp, profile.maxXp)

        dao.insertProfile(
            profile.copy(
                coins = newCoins,
                level = level,
                xp = xp,
                totalFocusMinutes = newTotalMinutes,
                totalFocusSessions = newTotalSessions
            )
        )

        // Advance active pet XP
        val active = dao.getActivePet().firstOrNull()
        if (active != null) {
            val (petLevel, petXp) = calculateLevel(active.level, active.xp + xpEarned, active.maxXp)
            dao.updatePet(active.copy(level = petLevel, xp = petXp))
        }

        // Advance focus hours for all locked pets with target hours
        val totalHours = newTotalMinutes / 60
        val pets = dao.getAllPets().firstOrNull() ?: emptyList()
        pets.filter { !it.isUnlocked && it.focusHoursTarget > 0 }.forEach { p ->
            dao.updatePet(p.copy(focusHoursCurrent = totalHours.coerceAtMost(p.focusHoursTarget)))
        }

        // Advance Deep Learner mission
        advanceDeepLearnerMission(durationMinutes)

        // Advance Time of Day Missions
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (currentHour < 12) {
            advanceMorningFocusMission()
        } else if (currentHour >= 20) {
            advanceNightOwlMission()
        }

        // Advance Weekly Quest
        val quest = dao.getWeeklyQuest().firstOrNull()
        if (quest != null) {
            val newQuestMinutes = (quest.progressMinutes + durationMinutes).coerceAtMost(quest.targetMinutes)
            dao.updateWeeklyQuest(quest.copy(progressMinutes = newQuestMinutes))
        }
    }

    suspend fun recordMoodCheckIn(moodKey: String, note: String = ""): Pair<Boolean, String> {
        val mood = AVAILABLE_MOODS.find { it.key == moodKey } ?: AVAILABLE_MOODS.first()
        val now = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date())

        val entry = MoodEntry(
            moodKey = mood.key,
            moodEmoji = mood.emoji,
            moodLabel = mood.label,
            note = note.trim(),
            petResponse = mood.companionMessage,
            dateFormatted = now,
            timestamp = System.currentTimeMillis()
        )
        dao.insertMoodEntry(entry)

        // Boost pet joy
        val active = dao.getActivePet().firstOrNull()
        if (active != null) {
            val newJoy = (active.happiness + 25).coerceAtMost(100)
            dao.updatePet(active.copy(happiness = newJoy))
        }

        // Award Coins & XP
        val profile = dao.getUserProfile().firstOrNull()
        if (profile != null) {
            val newCoins = profile.coins + 25
            val newXp = profile.xp + 40
            val (level, xp) = calculateLevel(profile.level, newXp, profile.maxXp)
            dao.insertProfile(profile.copy(coins = newCoins, level = level, xp = xp))
        }

        return Pair(true, mood.companionMessage)
    }

    suspend fun completeMindfulSession(techniqueTitle: String, durationSeconds: Int, cyclesCompleted: Int) {
        val coinsEarned = 25
        val xpEarned = 50

        dao.insertMindfulSession(
            MindfulSession(
                technique = techniqueTitle,
                durationSeconds = durationSeconds,
                cyclesCompleted = cyclesCompleted,
                timestamp = System.currentTimeMillis()
            )
        )

        // Boost active pet happiness and rest energy
        val active = dao.getActivePet().firstOrNull()
        if (active != null) {
            val newJoy = (active.happiness + 20).coerceAtMost(100)
            val newEnergy = (active.energy + 15).coerceAtMost(100)
            val (petLevel, petXp) = calculateLevel(active.level, active.xp + xpEarned, active.maxXp)
            dao.updatePet(active.copy(happiness = newJoy, energy = newEnergy, level = petLevel, xp = petXp))
        }

        val profile = dao.getUserProfile().firstOrNull() ?: return
        val newCoins = profile.coins + coinsEarned
        val newXp = profile.xp + xpEarned
        val (level, xp) = calculateLevel(profile.level, newXp, profile.maxXp)
        dao.insertProfile(profile.copy(coins = newCoins, level = level, xp = xp))
    }

    suspend fun claimMissionReward(missionId: Int): Pair<Int, String>? {
        val missions = dao.getAllMissions().firstOrNull() ?: return null
        val mission = missions.find { it.id == missionId } ?: return null
        if (!mission.isCompleted || mission.isClaimed) return null

        dao.updateMission(mission.copy(isClaimed = true))

        val profile = dao.getUserProfile().firstOrNull() ?: return null
        val newCoins = profile.coins + mission.coinsReward
        val newXp = profile.xp + mission.xpReward
        val (level, xp) = calculateLevel(profile.level, newXp, profile.maxXp)
        dao.insertProfile(profile.copy(coins = newCoins, level = level, xp = xp))

        val rewardText = if (mission.bonusItemText.isNotEmpty()) {
            "+${mission.coinsReward} Coins + ${mission.bonusItemText}"
        } else {
            "+${mission.coinsReward} Coins + ${mission.xpReward} XP"
        }
        return Pair(mission.coinsReward, rewardText)
    }

    suspend fun claimWeeklyQuestReward(): Pair<Int, String>? {
        val quest = dao.getWeeklyQuest().firstOrNull() ?: return null
        if (quest.progressMinutes < quest.targetMinutes || quest.isClaimed) return null

        dao.updateWeeklyQuest(quest.copy(isClaimed = true))
        val profile = dao.getUserProfile().firstOrNull() ?: return null
        val newCoins = profile.coins + quest.rewardCoins
        dao.insertProfile(profile.copy(coins = newCoins))

        // Unlock Starlight Beret cosmetic
        val products = dao.getAllShopProducts().firstOrNull() ?: emptyList()
        products.find { it.id == "starlight_beret" }?.let { beret ->
            dao.updateShopProduct(beret.copy(isOwned = true))
        }
        return Pair(quest.rewardCoins, "Claimed ${quest.rewardTitle} + ${quest.rewardCoins} Coins! 🎖️")
    }

    suspend fun claimMilestoneChest(): Boolean {
        val profile = dao.getUserProfile().firstOrNull() ?: return false
        dao.insertProfile(profile.copy(coins = profile.coins + 250))
        return true
    }

    suspend fun equipPet(petId: String) {
        val pet = dao.getPetById(petId) ?: return
        if (!pet.isUnlocked) return
        dao.switchActivePetTx(petId)
        val products = dao.getAllShopProducts().firstOrNull() ?: return
        products.filter { it.category == "pets" }.forEach { p ->
            val matchingPetId = p.id.removePrefix("shop_pet_")
            dao.updateShopProduct(p.copy(isEquipped = (matchingPetId == petId)))
        }
    }

    suspend fun purchaseShopProduct(productId: String): Boolean {
        val products = dao.getAllShopProducts().firstOrNull() ?: return false
        val product = products.find { it.id == productId } ?: return false
        val profile = dao.getUserProfile().firstOrNull() ?: return false

        if (profile.coins < product.priceCoins) return false

        dao.insertProfile(profile.copy(coins = profile.coins - product.priceCoins))

        // Consumable Snacks: Apply immediate pet stats boost and keep purchasable
        if (product.category == "snacks") {
            val active = dao.getActivePet().firstOrNull()
            if (active != null) {
                val (addHunger, addJoy, addEnergy, addXp) = when (productId) {
                    "biscuits" -> listOf(25, 10, 0, 10)
                    "croissant" -> listOf(15, 35, 10, 15)
                    "macaron" -> listOf(10, 20, 30, 20)
                    "boba_tea" -> listOf(40, 40, 50, 30)
                    "dango" -> listOf(20, 40, 40, 25)
                    "star_candy" -> listOf(15, 50, 30, 35)
                    "fruit_parfait" -> listOf(50, 50, 50, 50)
                    else -> listOf(20, 20, 20, 15)
                }
                val newHunger = (active.hunger + addHunger).coerceAtMost(100)
                val newJoy = (active.happiness + addJoy).coerceAtMost(100)
                val newEnergy = (active.energy + addEnergy).coerceAtMost(100)
                val (newLevel, newPetXp) = calculateLevel(active.level, active.xp + addXp, active.maxXp)
                dao.updatePet(active.copy(hunger = newHunger, happiness = newJoy, energy = newEnergy, level = newLevel, xp = newPetXp))
                updateLovingCareMissionProgress()
            }
            return true
        }

        dao.updateShopProduct(product.copy(isOwned = true, isEquipped = true))

        if (product.category == "pets") {
            val petId = product.id.removePrefix("shop_pet_")
            val pet = dao.getPetById(petId)
            if (pet != null) {
                dao.updatePet(pet.copy(isUnlocked = true, unlockRequirement = ""))
                equipPet(petId)
            }
        } else if (product.category == "accessories") {
            val headwearId = when {
                product.id.contains("headphones") -> "headphones"
                product.id.contains("crown") -> "crown"
                product.id.contains("beanie") -> "beanie"
                else -> ""
            }
            if (headwearId.isNotBlank()) {
                val active = dao.getActivePet().firstOrNull()
                if (active != null) {
                    dao.updatePet(active.copy(headwear = headwearId))
                }
            }
        } else if (product.category == "furniture") {
            val decors = dao.getAllRoomDecor().firstOrNull() ?: emptyList()
            decors.find { it.itemId == productId }?.let { decor ->
                dao.insertRoomDecor(decor.copy(isVisible = true))
            }
        }
        return true
    }

    private suspend fun advanceTaskMasterMission() {
        val missions = dao.getAllMissions().firstOrNull() ?: return
        val taskMission = missions.find { it.id == 2 } ?: return
        val newProgress = (taskMission.progress + 1).coerceAtMost(taskMission.target)
        val isDone = newProgress >= taskMission.target
        dao.updateMission(taskMission.copy(progress = newProgress, isCompleted = isDone))
    }

    private suspend fun advanceDeepLearnerMission(mins: Int) {
        val missions = dao.getAllMissions().firstOrNull() ?: return
        val learner = missions.find { it.id == 3 } ?: return
        val newProgress = (learner.progress + mins).coerceAtMost(learner.target)
        val isDone = newProgress >= learner.target
        dao.updateMission(learner.copy(progress = newProgress, isCompleted = isDone))
    }

    private suspend fun advanceMorningFocusMission() {
        val missions = dao.getAllMissions().firstOrNull() ?: return
        val morning = missions.find { it.id == 1 } ?: return
        dao.updateMission(morning.copy(progress = 1, isCompleted = true))
    }

    private suspend fun advanceNightOwlMission() {
        val missions = dao.getAllMissions().firstOrNull() ?: return
        val night = missions.find { it.id == 5 } ?: return
        dao.updateMission(night.copy(progress = 1, isCompleted = true, isLocked = false))
    }

    private suspend fun updateLovingCareMissionProgress() {
        val missions = dao.getAllMissions().firstOrNull() ?: return
        val care = missions.find { it.id == 4 } ?: return
        val newProgress = (care.progress + 1).coerceAtMost(care.target)
        val isDone = newProgress >= care.target
        dao.updateMission(care.copy(progress = newProgress, isCompleted = isDone))
    }

    private fun calculateLevel(currentLevel: Int, currentXp: Int, maxXp: Int): Pair<Int, Int> {
        var lvl = currentLevel
        var xp = currentXp
        while (xp >= maxXp) {
            lvl += 1
            xp -= maxXp
        }
        return Pair(lvl, xp)
    }

    suspend fun updateUserProfileFromAuth(displayName: String, username: String, avatarUrl: String = "") {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfile()
        dao.insertProfile(
            current.copy(
                displayName = displayName.ifBlank { current.displayName },
                username = username.ifBlank { current.username },
                avatarUrl = avatarUrl.ifBlank { current.avatarUrl }
            )
        )
    }

    suspend fun updateUserProfile(displayName: String, avatarUrl: String) {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfile()
        dao.insertProfile(
            current.copy(
                displayName = displayName.ifBlank { current.displayName },
                avatarUrl = avatarUrl.ifBlank { current.avatarUrl }
            )
        )
    }
}
