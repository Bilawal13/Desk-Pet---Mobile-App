package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "user_default",
    val username: String = "kbilawal",
    val displayName: String = "Bilawal",
    val avatarUrl: String = "avatar_user",
    val coins: Int = 80,
    val starGems: Int = 0,
    val level: Int = 1,
    val xp: Int = 0,
    val maxXp: Int = 200,
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val streakMultiplier: Float = 1.0f,
    val totalFocusMinutes: Int = 0,
    val totalFocusSessions: Int = 0,
    val lastActiveDate: String = "2026-10-12"
) {
    fun getAvatarDrawableResId(): Int {
        return when (avatarUrl.ifBlank { "avatar_user" }) {
            "avatar_lofi_boy" -> com.example.R.drawable.avatar_lofi_boy
            "avatar_matcha_girl" -> com.example.R.drawable.avatar_matcha_girl
            "avatar_cat_lover" -> com.example.R.drawable.avatar_cat_lover
            "avatar_night_owl" -> com.example.R.drawable.avatar_night_owl
            "avatar_cozy_artist" -> com.example.R.drawable.avatar_cozy_artist
            else -> com.example.R.drawable.avatar_user
        }
    }
}

data class AvatarChoice(
    val id: String,
    val name: String,
    val description: String,
    val drawableResId: Int
)

val AVAILABLE_AVATARS = listOf(
    AvatarChoice("avatar_user", "Cozy Student", "Classic tea & book lover", com.example.R.drawable.avatar_user),
    AvatarChoice("avatar_lofi_boy", "Lo-Fi Beats", "Chill headphones & warm knit", com.example.R.drawable.avatar_lofi_boy),
    AvatarChoice("avatar_matcha_girl", "Matcha Scholar", "Green ribbon & nature lover", com.example.R.drawable.avatar_matcha_girl),
    AvatarChoice("avatar_cat_lover", "Cat Companion", "Purr-fect cozy study buddy", com.example.R.drawable.avatar_cat_lover),
    AvatarChoice("avatar_night_owl", "Night Dreamer", "Starry beret midnight focus", com.example.R.drawable.avatar_night_owl),
    AvatarChoice("avatar_cozy_artist", "Pastel Artist", "Creative & watercolor aesthetic", com.example.R.drawable.avatar_cozy_artist)
)

@Entity(tableName = "pets")
data class PetItem(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val level: Int = 1,
    val xp: Int = 0,
    val maxXp: Int = 1000,
    val hunger: Int = 85,       // 0 - 100
    val energy: Int = 92,       // 0 - 100
    val happiness: Int = 96,    // 0 - 100 (Joy)
    val affinityPercent: Int = 100,
    val isActive: Boolean = false,
    val isUnlocked: Boolean = false,
    val unlockRequirement: String = "",
    val focusHoursCurrent: Int = 0,
    val focusHoursTarget: Int = 0,
    val rarity: String = "Common", // Common, Rare, Legendary, Mythic
    val typeLabel: String = "Grass / Cozy",
    val bonusLabel: String = "+10% XP/hr",
    val drawableResName: String,
    val furCoatColor: String = "lavender", // lavender, peach, mint, butter
    val headwear: String = "headphones",   // headphones, crown, beanie, none
    val ambientSound: String = "purr_lofi" // purr_lofi, gentle_chimes, forest_rain
) {
    fun getDrawableResId(): Int {
        val key = (drawableResName.ifBlank { id }).lowercase()
        return when {
            key.contains("shiba") -> com.example.R.drawable.pet_shiba
            key.contains("axolotl") -> com.example.R.drawable.pet_axolotl
            key.contains("owl") -> com.example.R.drawable.pet_owl
            key.contains("hamster") -> com.example.R.drawable.pet_hamster
            key.contains("boba") -> com.example.R.drawable.pet_boba
            key.contains("pippin") -> com.example.R.drawable.pet_pippin
            key.contains("nimbus") -> com.example.R.drawable.pet_nimbus
            key.contains("aurelia") -> com.example.R.drawable.pet_aurelia
            key.contains("panda") -> com.example.R.drawable.pet_panda
            key.contains("study") -> com.example.R.drawable.mochi_study
            key.contains("mochi") -> com.example.R.drawable.mochi_duolingo_vector
            else -> com.example.R.drawable.mochi_duolingo_vector
        }
    }
}

fun getPetDrawableResByName(drawableResName: String?, petId: String? = null): Int {
    val key = (drawableResName?.ifBlank { petId } ?: petId ?: "").lowercase()
    return when {
        key.contains("shiba") -> com.example.R.drawable.pet_shiba
        key.contains("axolotl") -> com.example.R.drawable.pet_axolotl
        key.contains("owl") -> com.example.R.drawable.pet_owl
        key.contains("hamster") -> com.example.R.drawable.pet_hamster
        key.contains("boba") -> com.example.R.drawable.pet_boba
        key.contains("pippin") -> com.example.R.drawable.pet_pippin
        key.contains("nimbus") -> com.example.R.drawable.pet_nimbus
        key.contains("aurelia") -> com.example.R.drawable.pet_aurelia
        key.contains("panda") -> com.example.R.drawable.pet_panda
        key.contains("study") -> com.example.R.drawable.mochi_study
        key.contains("mochi") -> com.example.R.drawable.mochi_duolingo_vector
        else -> com.example.R.drawable.mochi_duolingo_vector
    }
}

fun getShopDrawableResByName(drawableResName: String?, category: String = ""): Int {
    val key = (drawableResName ?: "").lowercase()
    return when {
        key.contains("shiba") -> com.example.R.drawable.pet_shiba
        key.contains("axolotl") -> com.example.R.drawable.pet_axolotl
        key.contains("owl") -> com.example.R.drawable.pet_owl
        key.contains("hamster") -> com.example.R.drawable.pet_hamster
        key.contains("boba_tea") -> com.example.R.drawable.shop_boba_tea
        key.contains("boba") -> com.example.R.drawable.pet_boba
        key.contains("pippin") -> com.example.R.drawable.pet_pippin
        key.contains("nimbus") -> com.example.R.drawable.pet_nimbus
        key.contains("aurelia") -> com.example.R.drawable.pet_aurelia
        key.contains("panda") -> com.example.R.drawable.pet_panda
        key.contains("headphones") -> com.example.R.drawable.shop_headphones
        key.contains("ribbon") || key.contains("bow") -> com.example.R.drawable.shop_ribbon_bow
        key.contains("beanie") -> com.example.R.drawable.shop_beanie
        key.contains("flower_crown") -> com.example.R.drawable.shop_flower_crown
        key.contains("crown") -> com.example.R.drawable.shop_crown
        key.contains("beret") -> com.example.R.drawable.quest_beret
        key.contains("fairylights") || key.contains("lights") || key.contains("lamp") -> com.example.R.drawable.shop_fairylights
        key.contains("succulent") || key.contains("plant") || key.contains("bonsai") || key.contains("clock") || key.contains("bookshelf") || key.contains("warmer") -> com.example.R.drawable.shop_succulent
        key.contains("flooring") || key.contains("tatami") || key.contains("floor") || key.contains("rug") || key.contains("planks") || key.contains("garden") -> com.example.R.drawable.shop_flooring
        key.contains("biscuits") || key.contains("cookies") -> com.example.R.drawable.shop_biscuits
        key.contains("croissant") -> com.example.R.drawable.shop_croissant
        key.contains("macaron") || key.contains("dango") || key.contains("candy") || key.contains("parfait") -> com.example.R.drawable.shop_macaron
        key.contains("tea") || key.contains("drink") -> com.example.R.drawable.shop_boba_tea
        key.contains("beanbag") || key.contains("pillow") -> com.example.R.drawable.shop_beanbag
        category == "accessories" -> com.example.R.drawable.shop_ribbon_bow
        category == "furniture" -> com.example.R.drawable.shop_succulent
        category == "themes" -> com.example.R.drawable.shop_flooring
        category == "snacks" -> com.example.R.drawable.shop_biscuits
        else -> com.example.R.drawable.shop_beanbag
    }
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val rewardClaimed: Boolean = false,
    val isActiveSession: Boolean = false,
    val remainingTimeText: String = "",
    val coinsReward: Int = 30,
    val xpReward: Int = 50,
    val priority: String = "Medium",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_missions")
data class MissionItem(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val iconName: String,
    val progress: Int,
    val target: Int,
    val coinsReward: Int,
    val xpReward: Int,
    val bonusItemText: String = "",
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val isLocked: Boolean = false,
    val lockText: String = ""
)

@Entity(tableName = "weekly_quests")
data class WeeklyQuestItem(
    @PrimaryKey val id: String = "weekly_main",
    val title: String = "Focus Marathon",
    val description: String = "Accumulate 5 hours total flow time this week.",
    val progressMinutes: Int = 200, // 3h 20m
    val targetMinutes: Int = 300,   // 5h
    val daysLeft: Int = 4,
    val rewardCoins: Int = 300,
    val rewardTitle: String = "Starlight Beret",
    val isClaimed: Boolean = false
)

@Entity(tableName = "shop_products")
data class ShopProduct(
    @PrimaryKey val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val category: String, // accessories, furniture, themes, snacks
    val priceCoins: Int,
    val originalPriceCoins: Int = 0,
    val isDiscounted: Boolean = false,
    val discountPercent: Int = 0,
    val tagText: String = "",
    val drawableResName: String,
    val isDailyDeal: Boolean = false,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false,
    val effectBonus: String = "",
    val requiredLevel: Int = 1
)

@Entity(tableName = "room_decor")
data class RoomDecor(
    @PrimaryKey val slotId: String, // beanbag, desk_succulent, wall_lights, flooring
    val itemId: String,
    val isVisible: Boolean = true
)

@Entity(tableName = "focus_records")
data class FocusRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val soundscape: String,
    val timestamp: Long = System.currentTimeMillis(),
    val coinsEarned: Int,
    val xpEarned: Int
)

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodKey: String, // radiant, peaceful, tired, overwhelmed, gloomy
    val moodEmoji: String,
    val moodLabel: String,
    val note: String = "",
    val petResponse: String = "",
    val dateFormatted: String = "", // e.g., "Oct 26"
    val timestamp: Long = System.currentTimeMillis()
)

data class MoodOption(
    val key: String,
    val emoji: String,
    val label: String,
    val description: String,
    val companionMessage: String
)

val AVAILABLE_MOODS = listOf(
    MoodOption("radiant", "✨", "Radiant", "Joyful, energetic, and proud", "Your positive light makes my little sprout bloom with joy! Let's cherish this beautiful energy today! 🌸"),
    MoodOption("peaceful", "🍵", "Peaceful", "Calm, grounded, and content", "Ah, a warm cup of peace. Sipping comfort with you right here in our quiet corner. 🍵✨"),
    MoodOption("tired", "🌙", "Tired", "Low battery, sleepy, need rest", "It's okay to slow down. Rest isn't giving up, it's recharging your cozy heart. Let's snuggle up! ☁️💤"),
    MoodOption("overwhelmed", "🌧️", "Overwhelm", "Stressed, lots of noise, anxious", "Take a soft breath with me. You don't have to carry the whole world today. One gentle step at a time. 🤍"),
    MoodOption("gloomy", "☁️", "Gloomy", "Feeling down, tender, or lonely", "I'm sitting right beside you, wrapped in a warm blanket. You are never alone, companion. 🐾💜")
)

@Entity(tableName = "mindful_sessions")
data class MindfulSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val technique: String, // Box Breathing, 4-7-8 Deep Calm, Cozy Flow
    val durationSeconds: Int,
    val cyclesCompleted: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class BreathingTechnique(
    val id: String,
    val title: String,
    val description: String,
    val inhaleSeconds: Int,
    val holdAfterInhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int,
    val tag: String
)

val AVAILABLE_BREATHING_TECHNIQUES = listOf(
    BreathingTechnique(
        id = "box",
        title = "Box Breathing",
        description = "4s Inhale · 4s Hold · 4s Exhale · 4s Hold\nDeep stress release & resets mental focus",
        inhaleSeconds = 4,
        holdAfterInhaleSeconds = 4,
        exhaleSeconds = 4,
        holdAfterExhaleSeconds = 4,
        tag = "Focus & Reset"
    ),
    BreathingTechnique(
        id = "478",
        title = "4-7-8 Deep Calm",
        description = "4s Inhale · 7s Hold · 8s Exhale\nNatural nervous system relaxant to ease anxiety",
        inhaleSeconds = 4,
        holdAfterInhaleSeconds = 7,
        exhaleSeconds = 8,
        holdAfterExhaleSeconds = 0,
        tag = "Deep Calm"
    ),
    BreathingTechnique(
        id = "cozy",
        title = "Cozy Flow",
        description = "4s Inhale · 4s Exhale\nEffortless rhythmic reset anytime you need a breath",
        inhaleSeconds = 4,
        holdAfterInhaleSeconds = 0,
        exhaleSeconds = 4,
        holdAfterExhaleSeconds = 0,
        tag = "Quick Relax"
    )
)
