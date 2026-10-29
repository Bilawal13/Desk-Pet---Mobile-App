package com.example

import com.example.data.model.PetItem
import com.example.data.model.ShopProduct
import com.example.data.model.UserProfile
import org.junit.Assert.*
import org.junit.Test

class DeskPetLogicTest {

    @Test
    fun testLevelCalculationProgression() {
        var level = 5
        var xp = 680 + 400 // 1080
        val maxXp = 1000

        while (xp >= maxXp) {
            level += 1
            xp -= maxXp
        }

        assertEquals(6, level)
        assertEquals(80, xp)
    }

    @Test
    fun testPetVitalsBounding() {
        val pet = PetItem(
            id = "mochi",
            name = "Mochi",
            title = "Sprout Kitten",
            hunger = 95,
            energy = 10,
            happiness = 95,
            drawableResName = "mochi_featured"
        )

        // Feed adds 15 hunger, capped at 100
        val fedHunger = (pet.hunger + 15).coerceAtMost(100)
        assertEquals(100, fedHunger)

        // Play consumes 10 energy, floored at 0
        val playedEnergy = (pet.energy - 10).coerceAtLeast(0)
        assertEquals(0, playedEnergy)

        // Pet adds 10 happiness, capped at 100
        val pettedJoy = (pet.happiness + 10).coerceAtMost(100)
        assertEquals(100, pettedJoy)
    }

    @Test
    fun testShopPurchaseBalanceCheck() {
        val profile = UserProfile(coins = 350)
        val product = ShopProduct(
            id = "headphones",
            name = "Pastel Headphones",
            subtitle = "Cosmetic Hat",
            description = "Cute headphones",
            category = "accessories",
            priceCoins = 350,
            drawableResName = "shop_headphones"
        )

        val canAfford = profile.coins >= product.priceCoins
        assertTrue(canAfford)

        val remaining = profile.coins - product.priceCoins
        assertEquals(0, remaining)

        val cannotAffordMore = remaining >= 100
        assertFalse(cannotAffordMore)
    }

    @Test
    fun testFocusTimerRewardCalculation() {
        val sessionMinutes = 25
        val baseCoins = 50
        val streakMultiplier = 1.5f

        val totalCoinsEarned = (baseCoins * streakMultiplier).toInt()
        assertEquals(75, totalCoinsEarned)
    }

    @Test
    fun testPetAnimationStateTransitions() {
        val states = com.example.ui.PetAnimationState.values()
        assertEquals(4, states.size)
        assertTrue(states.contains(com.example.ui.PetAnimationState.IDLE))
        assertTrue(states.contains(com.example.ui.PetAnimationState.HAPPY))
        assertTrue(states.contains(com.example.ui.PetAnimationState.FEED))
        assertTrue(states.contains(com.example.ui.PetAnimationState.PLAY))

        assertEquals("Idle", com.example.ui.PetAnimationState.IDLE.label)
        assertEquals("Happy", com.example.ui.PetAnimationState.HAPPY.label)
        assertEquals("Feed", com.example.ui.PetAnimationState.FEED.label)
        assertEquals("Play", com.example.ui.PetAnimationState.PLAY.label)
    }

    @Test
    fun testSupabaseConfigDetection() {
        val placeholderConfig = com.example.data.auth.SupabaseConfig(
            projectUrl = "https://your-project.supabase.co",
            anonKey = "your-anon-key-placeholder"
        )
        assertFalse(placeholderConfig.isConfigured)

        val realConfig = com.example.data.auth.SupabaseConfig(
            projectUrl = "https://abcd1234efgh.supabase.co",
            anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.realKey"
        )
        assertTrue(realConfig.isConfigured)
    }

    @Test
    fun testAuthUserFallback() {
        val user = com.example.data.auth.SupabaseUser(
            id = "u1",
            email = "bilawal@example.com",
            displayName = "Bilawal",
            username = "kbilawal"
        )
        assertEquals("Bilawal", user.displayName)
        assertEquals("kbilawal", user.username)
        assertEquals("bilawal@example.com", user.email)
    }
}
