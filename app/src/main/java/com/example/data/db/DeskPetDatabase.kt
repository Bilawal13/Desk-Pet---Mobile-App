package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        PetItem::class,
        TaskItem::class,
        MissionItem::class,
        WeeklyQuestItem::class,
        ShopProduct::class,
        RoomDecor::class,
        FocusRecord::class,
        MoodEntry::class,
        MindfulSession::class
    ],
    version = 6,
    exportSchema = false
)
abstract class DeskPetDatabase : RoomDatabase() {
    abstract fun dao(): DeskPetDao

    companion object {
        @Volatile
        private var INSTANCE: DeskPetDatabase? = null
        private val userDatabases = java.util.concurrent.ConcurrentHashMap<String, DeskPetDatabase>()

        fun getDatabaseForUser(
            context: Context,
            userId: String,
            displayName: String = "Companion",
            username: String = "friend"
        ): DeskPetDatabase {
            val cleanKey = if (userId.isBlank() || userId.equals("guest", ignoreCase = true) || userId.startsWith("guest_")) {
                "guest"
            } else {
                userId.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(48)
            }
            val dbName = if (cleanKey == "default") "desk_pet_database" else "desk_pet_user_$cleanKey.db"

            return userDatabases.computeIfAbsent(cleanKey) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    DeskPetDatabase::class.java,
                    dbName
                )
                    .fallbackToDestructiveMigration()
                    .build()
                if (cleanKey == "default") {
                    INSTANCE = db
                }
                db
            }
        }

        fun getDatabase(context: Context): DeskPetDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: getDatabaseForUser(context, "default").also { INSTANCE = it }
            }
        }

        suspend fun populateInitialData(
            dao: DeskPetDao,
            userId: String = "user_default",
            username: String = "friend",
            displayName: String = "Companion",
            avatarUrl: String = "avatar_user"
        ) {
            // Profile: Isolated Account Starter State
            dao.insertProfile(
                UserProfile(
                    id = userId.ifBlank { "user_default" },
                    username = username.ifBlank { "friend" },
                    displayName = displayName.ifBlank { "Companion" },
                    avatarUrl = avatarUrl.ifBlank { "avatar_user" },
                    coins = 80,
                    starGems = 0,
                    level = 1,
                    xp = 0,
                    maxXp = 200,
                    currentStreak = 1,
                    longestStreak = 1,
                    streakMultiplier = 1.0f,
                    totalFocusMinutes = 0,
                    totalFocusSessions = 0,
                    lastActiveDate = "2026-10-14"
                )
            )

            // Complete Roster of 10 Pets - ONLY Mochi is unlocked at start!
            val pets = listOf(
                PetItem(
                    id = "mochi",
                    name = "Mochi",
                    title = "Sprout Kitten",
                    level = 1,
                    xp = 0,
                    maxXp = 200,
                    hunger = 80,
                    energy = 90,
                    happiness = 90,
                    affinityPercent = 10,
                    isActive = true,
                    isUnlocked = true,
                    rarity = "Common",
                    typeLabel = "Grass / Cozy",
                    bonusLabel = "+10% XP/hr",
                    drawableResName = "mochi_featured",
                    furCoatColor = "lavender",
                    headwear = "none",
                    ambientSound = "purr_lofi"
                ),
                PetItem(
                    id = "boba",
                    name = "Boba",
                    title = "Dumpling Bunny",
                    level = 1,
                    xp = 0,
                    maxXp = 200,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Adopt in Shop or Reach Lvl 2",
                    rarity = "Common",
                    typeLabel = "Sweet / Calm",
                    bonusLabel = "-5m Break Need",
                    drawableResName = "pet_boba",
                    furCoatColor = "butter",
                    headwear = "none",
                    ambientSound = "gentle_chimes"
                ),
                PetItem(
                    id = "pippin",
                    name = "Pippin",
                    title = "Matcha Frog",
                    level = 1,
                    xp = 0,
                    maxXp = 250,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Adopt in Shop or Reach Lvl 3",
                    rarity = "Common",
                    typeLabel = "Rain / Focus",
                    bonusLabel = "Rain Ambience",
                    drawableResName = "pet_pippin",
                    furCoatColor = "mint",
                    headwear = "none",
                    ambientSound = "forest_rain"
                ),
                PetItem(
                    id = "nimbus",
                    name = "Nimbus",
                    title = "Cloud Fox",
                    level = 1,
                    xp = 0,
                    maxXp = 300,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Focus Hours",
                    focusHoursCurrent = 0,
                    focusHoursTarget = 10,
                    rarity = "Rare",
                    typeLabel = "Cloud / Ethereal",
                    bonusLabel = "+15% Coin Chance",
                    drawableResName = "pet_nimbus",
                    furCoatColor = "lavender",
                    headwear = "none",
                    ambientSound = "gentle_chimes"
                ),
                PetItem(
                    id = "shiba",
                    name = "Chai",
                    title = "Caramel Shiba",
                    level = 1,
                    xp = 0,
                    maxXp = 350,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Adopt in Shop or Reach Lvl 4",
                    rarity = "Rare",
                    typeLabel = "Loyal / Cheerful",
                    bonusLabel = "+15% Focus Streak",
                    drawableResName = "pet_shiba",
                    furCoatColor = "peach",
                    headwear = "none",
                    ambientSound = "gentle_chimes"
                ),
                PetItem(
                    id = "axolotl",
                    name = "Taro",
                    title = "Pastel Axolotl",
                    level = 1,
                    xp = 0,
                    maxXp = 400,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Adopt in Shop or Reach Lvl 5",
                    rarity = "Rare",
                    typeLabel = "Serene / Gentle",
                    bonusLabel = "+10% Energy Recovery",
                    drawableResName = "pet_axolotl",
                    furCoatColor = "lavender",
                    headwear = "none",
                    ambientSound = "forest_rain"
                ),
                PetItem(
                    id = "owl",
                    name = "Luna",
                    title = "Moonlight Owl",
                    level = 1,
                    xp = 0,
                    maxXp = 450,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Adopt in Shop or Reach Lvl 6",
                    rarity = "Rare",
                    typeLabel = "Night / Scholar",
                    bonusLabel = "+25% Evening Flow",
                    drawableResName = "pet_owl",
                    furCoatColor = "lavender",
                    headwear = "none",
                    ambientSound = "purr_lofi"
                ),
                PetItem(
                    id = "hamster",
                    name = "Pixel",
                    title = "Cyber Hamster",
                    level = 1,
                    xp = 0,
                    maxXp = 500,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Adopt in Shop or Reach Lvl 7",
                    rarity = "Rare",
                    typeLabel = "Speedy / Tech",
                    bonusLabel = "+20% Coin Bonus",
                    drawableResName = "pet_hamster",
                    furCoatColor = "butter",
                    headwear = "none",
                    ambientSound = "purr_lofi"
                ),
                PetItem(
                    id = "aurelia",
                    name = "Aurelia",
                    title = "Starlight Dragon",
                    level = 1,
                    xp = 0,
                    maxXp = 1000,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Focus Hours",
                    focusHoursCurrent = 0,
                    focusHoursTarget = 50,
                    rarity = "Legendary",
                    typeLabel = "Starlight / Dragon",
                    bonusLabel = "2x Multiplier",
                    drawableResName = "pet_aurelia",
                    furCoatColor = "lavender",
                    headwear = "none",
                    ambientSound = "purr_lofi"
                ),
                PetItem(
                    id = "panda",
                    name = "Matcha Panda",
                    title = "Zen Master",
                    level = 1,
                    xp = 0,
                    maxXp = 1000,
                    hunger = 100,
                    energy = 100,
                    happiness = 100,
                    affinityPercent = 0,
                    isActive = false,
                    isUnlocked = false,
                    unlockRequirement = "Reach Level 10 (Mythic)",
                    rarity = "Mythic",
                    typeLabel = "Zen / Master",
                    bonusLabel = "Deep Zen Flow",
                    drawableResName = "pet_panda",
                    furCoatColor = "mint",
                    headwear = "none",
                    ambientSound = "forest_rain"
                )
            )
            dao.insertPets(pets)

            // Starter Tasks
            val tasks = listOf(
                TaskItem(
                    id = 1,
                    title = "Complete 1st 25-minute focus session",
                    isCompleted = false,
                    isActiveSession = false,
                    remainingTimeText = "",
                    coinsReward = 30,
                    xpReward = 50,
                    priority = "High"
                ),
                TaskItem(
                    id = 2,
                    title = "Feed Mochi a snack and give cozy pets",
                    isCompleted = false,
                    isActiveSession = false,
                    remainingTimeText = "",
                    coinsReward = 20,
                    xpReward = 30,
                    priority = "Medium"
                ),
                TaskItem(
                    id = 3,
                    title = "Organize today's priority study notes",
                    isCompleted = false,
                    isActiveSession = false,
                    remainingTimeText = "",
                    coinsReward = 25,
                    xpReward = 40,
                    priority = "Medium"
                )
            )
            dao.insertTasks(tasks)

            // Daily Missions
            val missions = listOf(
                MissionItem(
                    id = 1,
                    title = "Morning Focus",
                    description = "Complete 1 focus session before noon.",
                    iconName = "wb_twilight",
                    progress = 0,
                    target = 1,
                    coinsReward = 50,
                    xpReward = 60,
                    bonusItemText = "1x Fresh Strawberry",
                    isCompleted = false,
                    isClaimed = false,
                    isLocked = false
                ),
                MissionItem(
                    id = 2,
                    title = "Task Master",
                    description = "Check off 3 daily tasks in your quest notebook.",
                    iconName = "checklist",
                    progress = 0,
                    target = 3,
                    coinsReward = 40,
                    xpReward = 75,
                    isCompleted = false,
                    isClaimed = false,
                    isLocked = false
                ),
                MissionItem(
                    id = 3,
                    title = "Deep Learner",
                    description = "Accumulate 60 minutes of cozy focus time.",
                    iconName = "hourglass_top",
                    progress = 0,
                    target = 60,
                    coinsReward = 60,
                    xpReward = 120,
                    isCompleted = false,
                    isClaimed = false,
                    isLocked = false
                ),
                MissionItem(
                    id = 4,
                    title = "Loving Care",
                    description = "Feed Mochi twice and pet 3 times today.",
                    iconName = "favorite",
                    progress = 0,
                    target = 2,
                    coinsReward = 30,
                    xpReward = 80,
                    isCompleted = false,
                    isClaimed = false,
                    isLocked = false
                ),
                MissionItem(
                    id = 5,
                    title = "Night Owl Reflection",
                    description = "Complete a 15-minute reflection session.",
                    iconName = "bedtime",
                    progress = 0,
                    target = 1,
                    coinsReward = 45,
                    xpReward = 80,
                    isCompleted = false,
                    isClaimed = false,
                    isLocked = true,
                    lockText = "Unlocks 8 PM"
                )
            )
            dao.insertMissions(missions)

            // Weekly Quest
            dao.insertWeeklyQuest(
                WeeklyQuestItem(
                    id = "weekly_main",
                    title = "Focus Marathon",
                    description = "Accumulate 5 hours total flow time this week.",
                    progressMinutes = 0,
                    targetMinutes = 300,
                    daysLeft = 7,
                    rewardCoins = 300,
                    rewardTitle = "Starlight Beret",
                    isClaimed = false
                )
            )

            // Rich Shop Products Catalog across all categories
            val shopProducts = listOf(
                // Daily Deal
                ShopProduct(
                    id = "beanbag",
                    name = "Lavender Beanbag",
                    subtitle = "Pet Nap Spot",
                    description = "Pet nap spots give +20% faster energy recovery. Squishy tactile fabric texture.",
                    category = "furniture",
                    priceCoins = 140,
                    originalPriceCoins = 200,
                    isDiscounted = true,
                    discountPercent = 30,
                    tagText = "Deal of the Day",
                    drawableResName = "shop_beanbag",
                    isDailyDeal = true,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+20% Energy Recovery",
                    requiredLevel = 1
                ),
                // Adoptable Pets in the Shop (all unowned at start!)
                ShopProduct(
                    id = "shop_pet_boba",
                    name = "Boba (Dumpling Bunny)",
                    subtitle = "Sweet Calm",
                    description = "Chubby round dumpling bunny with serene floppy ears. Reduces necessary break time.",
                    category = "pets",
                    priceCoins = 250,
                    tagText = "Sweet Calm",
                    drawableResName = "pet_boba",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "-5m Break Need",
                    requiredLevel = 2
                ),
                ShopProduct(
                    id = "shop_pet_pippin",
                    name = "Pippin (Matcha Frog)",
                    subtitle = "Rain Flow",
                    description = "Miniature green matcha frog under a tiny lotus leaf. Unlocks soothing gentle rain ambience.",
                    category = "pets",
                    priceCoins = 300,
                    tagText = "Rain Flow",
                    drawableResName = "pet_pippin",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Rain Ambience",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "shop_pet_nimbus",
                    name = "Nimbus (Cloud Fox)",
                    subtitle = "Ethereal Spirit",
                    description = "Fluffy cloud fox with a vapor tail and celestial sparkles. High coin bonus probability.",
                    category = "pets",
                    priceCoins = 450,
                    tagText = "Rare Spirit",
                    drawableResName = "pet_nimbus",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+15% Coin Chance",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "shop_pet_shiba",
                    name = "Chai (Caramel Shiba)",
                    subtitle = "Loyal Companion",
                    description = "Cheerful fluffy Shiba Inu with a tiny cozy scarf. Loves motivating your focus streaks!",
                    category = "pets",
                    priceCoins = 380,
                    tagText = "Loyal Partner",
                    drawableResName = "pet_shiba",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+15% Focus Streak",
                    requiredLevel = 4
                ),
                ShopProduct(
                    id = "shop_pet_axolotl",
                    name = "Taro (Pastel Axolotl)",
                    subtitle = "Serene Spirit",
                    description = "Gentle pastel axolotl with glowing frilly gills. Brings calmness and deep tranquility to your desk.",
                    category = "pets",
                    priceCoins = 500,
                    tagText = "Serene Spirit",
                    drawableResName = "pet_axolotl",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+10% Energy Recovery",
                    requiredLevel = 5
                ),
                ShopProduct(
                    id = "shop_pet_owl",
                    name = "Luna (Moonlight Owl)",
                    subtitle = "Night Scholar",
                    description = "Wise miniature baby barn owl wearing tiny round spectacles. Boosts late evening study sessions.",
                    category = "pets",
                    priceCoins = 480,
                    tagText = "Night Scholar",
                    drawableResName = "pet_owl",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+25% Evening Flow",
                    requiredLevel = 6
                ),
                ShopProduct(
                    id = "shop_pet_hamster",
                    name = "Pixel (Cyber Hamster)",
                    subtitle = "Speedy Worker",
                    description = "Energetic golden dwarf hamster wearing mint gaming headphones. Increases coin drop chance!",
                    category = "pets",
                    priceCoins = 420,
                    tagText = "Speedy Worker",
                    drawableResName = "pet_hamster",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+20% Coin Bonus",
                    requiredLevel = 7
                ),
                ShopProduct(
                    id = "shop_pet_aurelia",
                    name = "Aurelia (Starlight Dragon)",
                    subtitle = "Mythical Dragon",
                    description = "Iridescent amethyst baby dragon with glowing stardust horns. Doubles all study multipliers!",
                    category = "pets",
                    priceCoins = 800,
                    tagText = "Legendary",
                    drawableResName = "pet_aurelia",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "2x Multiplier",
                    requiredLevel = 8
                ),
                ShopProduct(
                    id = "shop_pet_panda",
                    name = "Matcha Panda",
                    subtitle = "Zen Master",
                    description = "Mythical baby giant panda holding a bamboo sprig with matcha swirl markings.",
                    category = "pets",
                    priceCoins = 750,
                    tagText = "Mythic",
                    drawableResName = "pet_panda",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Deep Zen Flow",
                    requiredLevel = 10
                ),
                // Category: Accessories & Headwear (multiple items across progressive levels)
                ShopProduct(
                    id = "ribbon_bow",
                    name = "Strawberry Silk Ribbon",
                    subtitle = "Cute Headwear",
                    description = "Dainty pink silk bow that sits adorably on your companion's head.",
                    category = "accessories",
                    priceCoins = 80,
                    tagText = "Starter Cute",
                    drawableResName = "shop_ribbon_bow",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+5 Joy on Pet",
                    requiredLevel = 1
                ),
                ShopProduct(
                    id = "flower_pin",
                    name = "Daisy Flower Clip",
                    subtitle = "Natural Pin",
                    description = "Delicate spring daisy hairclip that brightens up every focus session.",
                    category = "accessories",
                    priceCoins = 65,
                    tagText = "Fresh Flora",
                    drawableResName = "shop_ribbon_bow",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+5% XP Flow",
                    requiredLevel = 1
                ),
                ShopProduct(
                    id = "headphones",
                    name = "Pastel Lo-Fi Headphones",
                    subtitle = "Cosmetic Headwear",
                    description = "Handcrafted audio headgear tuned for productive lo-fi sessions. Unlocks celebration emote!",
                    category = "accessories",
                    priceCoins = 120,
                    tagText = "Audio Lofi",
                    drawableResName = "shop_headphones",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+10% Study Focus",
                    requiredLevel = 2
                ),
                ShopProduct(
                    id = "beanie",
                    name = "Warm Knitted Beanie",
                    subtitle = "Cozy Headwear",
                    description = "Chunky knit burnt orange beanie with a soft woolen pompom on top.",
                    category = "accessories",
                    priceCoins = 160,
                    tagText = "Winter Cozy",
                    drawableResName = "shop_beanie",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Cozy Warmth",
                    requiredLevel = 2
                ),
                ShopProduct(
                    id = "flower_crown",
                    name = "Daisy Flower Wreath",
                    subtitle = "Spring Headwear",
                    description = "Hand-woven crown of fresh daisies and baby's breath that blooms with positive energy.",
                    category = "accessories",
                    priceCoins = 180,
                    tagText = "Spring Flora",
                    drawableResName = "shop_flower_crown",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+10% Calmness",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "cozy_scarf",
                    name = "Caramel Wool Scarf",
                    subtitle = "Warm Neckwear",
                    description = "Extra-soft knitted scarf wrapped snugly around your pet for frosty study days.",
                    category = "accessories",
                    priceCoins = 200,
                    tagText = "Warm Snug",
                    drawableResName = "shop_beanie",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+15% Warmth Boost",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "crown",
                    name = "Golden Royal Crown",
                    subtitle = "Royal Headwear",
                    description = "Polished 24k gold mini tiara studded with tiny sparkling ruby and sapphire gems.",
                    category = "accessories",
                    priceCoins = 280,
                    tagText = "Royal",
                    drawableResName = "shop_crown",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+10% Coin Bonus",
                    requiredLevel = 4
                ),
                ShopProduct(
                    id = "round_glasses",
                    name = "Round Scholar Specs",
                    subtitle = "Academic Eyewear",
                    description = "Gentle rose gold wireframe spectacles that give your pet a wise scholarly charm.",
                    category = "accessories",
                    priceCoins = 240,
                    tagText = "Scholar",
                    drawableResName = "shop_crown",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+15% Focus Memory",
                    requiredLevel = 4
                ),
                ShopProduct(
                    id = "starlight_halo",
                    name = "Celestial Starlight Halo",
                    subtitle = "Mythic Headwear",
                    description = "Floating golden halo encircled by faint cosmic stardust particles.",
                    category = "accessories",
                    priceCoins = 350,
                    tagText = "Celestial",
                    drawableResName = "quest_beret",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+25% Night Flow",
                    requiredLevel = 5
                ),
                ShopProduct(
                    id = "starlight_beret",
                    name = "Starlight Dreamer Beret",
                    subtitle = "Midnight Beret",
                    description = "Deep navy artist beret dotted with embroidered silver constellations.",
                    category = "accessories",
                    priceCoins = 400,
                    tagText = "Grand Master",
                    drawableResName = "quest_beret",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+30% Flow Multiplier",
                    requiredLevel = 6
                ),

                // Category: Furniture & Desk Decor
                ShopProduct(
                    id = "succulent",
                    name = "Succulent Desk Pot",
                    subtitle = "Desk Plant",
                    description = "Chubby mint green succulent plant growing in a smiling ceramic pot with rosy cheeks.",
                    category = "furniture",
                    priceCoins = 60,
                    tagText = "+5 Focus/hr",
                    drawableResName = "shop_succulent",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+5 Focus/hr",
                    requiredLevel = 1
                ),
                ShopProduct(
                    id = "fairylights",
                    name = "Fairy Light Garland",
                    subtitle = "Wall Accent",
                    description = "Miniature hanging warm glowing fairy string lights shaped like small yellow stars.",
                    category = "furniture",
                    priceCoins = 140,
                    tagText = "Bokeh Glow",
                    drawableResName = "shop_fairylights",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Warm Bokeh Glow",
                    requiredLevel = 1
                ),
                ShopProduct(
                    id = "desk_clock",
                    name = "Vintage Wooden Alarm Clock",
                    subtitle = "Time Tracker",
                    description = "Retro analog desk clock with brass bells that gently ticks away distractions.",
                    category = "furniture",
                    priceCoins = 160,
                    tagText = "Clock Accent",
                    drawableResName = "shop_succulent",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+5m Focus Stamina",
                    requiredLevel = 2
                ),
                ShopProduct(
                    id = "bookshelf",
                    name = "Lo-Fi Hanging Bookshelf",
                    subtitle = "Study Nook",
                    description = "Floating pine wood shelf stacked with pastel books and tiny trailing vines.",
                    category = "furniture",
                    priceCoins = 220,
                    tagText = "Book Nook",
                    drawableResName = "shop_succulent",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+15% Study XP",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "mug_warmer",
                    name = "Cat Ear Mug Warmer",
                    subtitle = "Desk Warmer",
                    description = "Electric ceramic pad that keeps your green tea warm indefinitely during deep work.",
                    category = "furniture",
                    priceCoins = 190,
                    tagText = "Cozy Brew",
                    drawableResName = "shop_beanbag",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+10% Energy Rest",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "bonsai",
                    name = "Zen Bonsai Stand",
                    subtitle = "Meditative Flora",
                    description = "Sculpted Japanese juniper bonsai nestled in a rustic slate dish.",
                    category = "furniture",
                    priceCoins = 280,
                    tagText = "Zen Flow",
                    drawableResName = "shop_succulent",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+20% Peace Aura",
                    requiredLevel = 4
                ),
                ShopProduct(
                    id = "star_projector",
                    name = "Nebula Star Projector",
                    subtitle = "Cosmic Ambiance",
                    description = "Spherical ambient projector that casts revolving starlight across your ceiling.",
                    category = "furniture",
                    priceCoins = 360,
                    tagText = "Cosmic Sky",
                    drawableResName = "shop_fairylights",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+25% Evening Focus",
                    requiredLevel = 5
                ),

                // Category: Themes & Flooring
                ShopProduct(
                    id = "warm_tatami",
                    name = "Cozy Straw Tatami",
                    subtitle = "Traditional Floor",
                    description = "Woven rush grass tatami mats with sweet herbal scent and soft dark green borders.",
                    category = "themes",
                    priceCoins = 150,
                    tagText = "Herbal Floor",
                    drawableResName = "shop_flooring",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+10% Calm Floor",
                    requiredLevel = 1
                ),
                ShopProduct(
                    id = "flooring",
                    name = "Sakura Birch Floor Tile",
                    subtitle = "Room Remodel",
                    description = "Light natural birch wood parquet planks with gentle pink cherry blossom petals.",
                    category = "themes",
                    priceCoins = 220,
                    tagText = "Theme Floor",
                    drawableResName = "shop_flooring",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Sakura Petal Floor",
                    requiredLevel = 2
                ),
                ShopProduct(
                    id = "forest_cabin",
                    name = "Rustic Birch Cabin Wood",
                    subtitle = "Timber Flooring",
                    description = "Sturdy reclaimed timber floorboards that evoke a deep woodland retreat.",
                    category = "themes",
                    priceCoins = 260,
                    tagText = "Woodland",
                    drawableResName = "shop_flooring",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Woodland Soundscape",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "celestial_rug",
                    name = "Celestial Starlight Carpet",
                    subtitle = "Cosmic Carpet",
                    description = "Deep indigo plush velvet rug adorned with silver astronomical charts.",
                    category = "themes",
                    priceCoins = 340,
                    tagText = "Night Sky",
                    drawableResName = "shop_flooring",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+20% Star Bonus",
                    requiredLevel = 4
                ),
                ShopProduct(
                    id = "zen_bamboo",
                    name = "Zen Bamboo Sanctuary",
                    subtitle = "Japanese Garden",
                    description = "Smooth black river stones and pale bamboo parquet with gentle water ripples.",
                    category = "themes",
                    priceCoins = 400,
                    tagText = "Deep Zen",
                    drawableResName = "shop_flooring",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Master Zen Multiplier",
                    requiredLevel = 5
                ),

                // Category: Pet Snacks
                ShopProduct(
                    id = "biscuits",
                    name = "Golden Fish Biscuits (x5)",
                    subtitle = "Starter Snack",
                    description = "Warm golden-brown baked fish-shaped cookies. Mochi's favorite focus treat.",
                    category = "snacks",
                    priceCoins = 25,
                    tagText = "+25 Hunger",
                    drawableResName = "shop_biscuits",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+25 Hunger",
                    requiredLevel = 1
                ),
                ShopProduct(
                    id = "croissant",
                    name = "Fresh Strawberry Croissant",
                    subtitle = "Bakery Treat",
                    description = "Flaky golden bakery croissant topped with fresh strawberry and powdered sugar.",
                    category = "snacks",
                    priceCoins = 35,
                    tagText = "+35 Joy",
                    drawableResName = "shop_croissant",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+35 Joy",
                    requiredLevel = 1
                ),
                ShopProduct(
                    id = "macaron",
                    name = "Matcha Macaron",
                    subtitle = "Delicate Sweet",
                    description = "Authentic green matcha macaron with sweet white chocolate ganache filling.",
                    category = "snacks",
                    priceCoins = 40,
                    tagText = "+30 Energy",
                    drawableResName = "shop_macaron",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+30 Energy",
                    requiredLevel = 2
                ),
                ShopProduct(
                    id = "boba_tea",
                    name = "Brown Sugar Boba Tea",
                    subtitle = "Refreshing Drink",
                    description = "Iced milk tea with chewy brown sugar tapioca boba pearls and sweet cream.",
                    category = "snacks",
                    priceCoins = 50,
                    tagText = "Max Joy & Hunger",
                    drawableResName = "shop_boba_tea",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Full Energy Boost",
                    requiredLevel = 2
                ),
                ShopProduct(
                    id = "dango",
                    name = "Tricolor Mochi Dango",
                    subtitle = "Traditional Sweet",
                    description = "Skewered trio of pink, white, and green sweet rice dumplings coated in glaze.",
                    category = "snacks",
                    priceCoins = 55,
                    tagText = "+40 Joy & Rest",
                    drawableResName = "shop_macaron",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+40 Joy & Energy",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "star_candy",
                    name = "Konpeito Star Candies",
                    subtitle = "Sugar Crystals",
                    description = "Tiny crunchy pastel star candies that sparkle when your companion crunches them.",
                    category = "snacks",
                    priceCoins = 65,
                    tagText = "+50 Joy Burst",
                    drawableResName = "shop_croissant",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "+50 Joy Burst",
                    requiredLevel = 3
                ),
                ShopProduct(
                    id = "fruit_parfait",
                    name = "Starlight Fruit Parfait",
                    subtitle = "Luxury Dessert",
                    description = "Tall glass layered with whipped cream, blueberries, strawberries, and matcha sponge.",
                    category = "snacks",
                    priceCoins = 80,
                    tagText = "Max Hunger & Joy",
                    drawableResName = "shop_boba_tea",
                    isDailyDeal = false,
                    isOwned = false,
                    isEquipped = false,
                    effectBonus = "Full Recovery & +50 XP",
                    requiredLevel = 4
                )
            )
            dao.insertShopProducts(shopProducts)

            // Room Decor
            val roomDecor = listOf(
                RoomDecor("wall_lights", "fairylights", isVisible = false),
                RoomDecor("desk_succulent", "succulent", isVisible = false),
                RoomDecor("beanbag", "beanbag", isVisible = false)
            )
            dao.insertAllRoomDecor(roomDecor)
        }
    }
}
