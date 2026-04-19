package com.happychicks

import com.happychicks.data.GameRepository
import org.junit.Assert.*
import org.junit.Test

/**
 * Pure unit tests (no Android runtime required). More integration tests live in androidTest/.
 * These validate repository logic invariants using an in-memory fake.
 */
class GameRepositoryLogicTest {

    @Test fun coinBalanceNeverNegativeOnSpend() {
        val fake = InMemoryRepo()
        fake.addCoins(10)
        assertTrue(fake.spend(5))
        assertFalse(fake.spend(999))
        assertEquals(5, fake.coins)
    }

    @Test fun bestScoreUpdatesOnlyWhenHigher() {
        val fake = InMemoryRepo()
        assertTrue(fake.tryBest(30)); assertEquals(30, fake.best)
        assertFalse(fake.tryBest(10)); assertEquals(30, fake.best)
        assertTrue(fake.tryBest(50)); assertEquals(50, fake.best)
    }

    @Test fun hungerClampedTo0to100() {
        val fake = InMemoryRepo()
        fake.hunger = 200; assertEquals(100, fake.hunger)
        fake.hunger = -10; assertEquals(0, fake.hunger)
    }

    private class InMemoryRepo {
        var coins = 0; var best = 0; private var h = 100
        var hunger: Int
            get() = h
            set(v) { h = v.coerceIn(0, 100) }
        fun addCoins(n: Int) { coins = (coins + n).coerceAtLeast(0) }
        fun spend(n: Int): Boolean {
            if (coins < n) return false
            coins -= n; return true
        }
        fun tryBest(s: Int): Boolean = if (s > best) { best = s; true } else false
    }
}
