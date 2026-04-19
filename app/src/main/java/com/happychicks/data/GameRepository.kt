package com.happychicks.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Central repository for all local game state.
 * Backed by SharedPreferences. Keys follow the spec in HappyChicks.md §10.
 */
class GameRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ===== Challenge =====
    fun getBestScore(): Int = prefs.getInt(KEY_BEST_SCORE, 0)
    fun updateBestScore(score: Int): Boolean {
        val old = getBestScore()
        if (score > old) { prefs.edit().putInt(KEY_BEST_SCORE, score).apply(); return true }
        return false
    }

    // ===== Coins =====
    fun getCoins(): Int = prefs.getInt(KEY_COINS, 0)
    fun addCoins(delta: Int) {
        prefs.edit().putInt(KEY_COINS, (getCoins() + delta).coerceAtLeast(0)).apply()
    }
    fun spendCoins(cost: Int): Boolean {
        val cur = getCoins()
        if (cur < cost) return false
        prefs.edit().putInt(KEY_COINS, cur - cost).apply()
        return true
    }

    // ===== Character =====
    fun getSelectedCharacter(): Int = prefs.getInt(KEY_CHAR, 0)
    fun setSelectedCharacter(id: Int) { prefs.edit().putInt(KEY_CHAR, id).apply() }

    // ===== Skins =====
    fun getUnlockedSkins(): Set<String> =
        prefs.getStringSet(KEY_SKINS, emptySet()) ?: emptySet()

    fun unlockSkin(skinId: String) {
        val set = getUnlockedSkins().toMutableSet()
        set.add(skinId)
        prefs.edit().putStringSet(KEY_SKINS, set).apply()
    }

    fun isSkinUnlocked(skinId: String) = getUnlockedSkins().contains(skinId)

    // ===== Achievements =====
    fun isAchievementUnlocked(id: String): Boolean =
        prefs.getBoolean("achievement_$id", false)

    fun unlockAchievement(id: String): Boolean {
        if (isAchievementUnlocked(id)) return false
        prefs.edit().putBoolean("achievement_$id", true).apply()
        return true
    }

    // ===== Totals (drives achievements) =====
    fun getTotalEggs(): Int = prefs.getInt(KEY_TOTAL_EGGS, 0)
    fun addEggs(n: Int) {
        prefs.edit().putInt(KEY_TOTAL_EGGS, getTotalEggs() + n).apply()
    }

    fun getTotalChicks(): Int = prefs.getInt(KEY_TOTAL_CHICKS, 0)
    fun addChicks(n: Int) {
        prefs.edit().putInt(KEY_TOTAL_CHICKS, getTotalChicks() + n).apply()
    }

    // ===== Farm state =====
    fun getHunger(): Int = prefs.getInt(KEY_HUNGER, 100)
    fun setHunger(v: Int) { prefs.edit().putInt(KEY_HUNGER, v.coerceIn(0, 100)).apply() }

    fun getCleanliness(): Int = prefs.getInt(KEY_CLEAN, 100)
    fun setCleanliness(v: Int) { prefs.edit().putInt(KEY_CLEAN, v.coerceIn(0, 100)).apply() }

    fun getLastCareTimestamp(): Long = prefs.getLong(KEY_LAST_CARE, System.currentTimeMillis())
    fun setLastCareTimestamp(t: Long) { prefs.edit().putLong(KEY_LAST_CARE, t).apply() }

    // ===== Exploration =====
    fun getDiscoveredItems(): Set<String> =
        prefs.getStringSet(KEY_DISCOVERED, emptySet()) ?: emptySet()

    fun markDiscovered(id: String) {
        val set = getDiscoveredItems().toMutableSet(); set.add(id)
        prefs.edit().putStringSet(KEY_DISCOVERED, set).apply()
    }

    // ===== Settings =====
    fun getMusicVolume(): Int = prefs.getInt(KEY_MUSIC_VOL, 70)
    fun setMusicVolume(v: Int) { prefs.edit().putInt(KEY_MUSIC_VOL, v.coerceIn(0, 100)).apply() }

    fun getSfxVolume(): Int = prefs.getInt(KEY_SFX_VOL, 80)
    fun setSfxVolume(v: Int) { prefs.edit().putInt(KEY_SFX_VOL, v.coerceIn(0, 100)).apply() }

    fun getLanguage(): String = prefs.getString(KEY_LANG, "zh") ?: "zh"
    fun setLanguage(lang: String) { prefs.edit().putString(KEY_LANG, lang).apply() }

    // ===== Reset =====
    fun resetAll() { prefs.edit().clear().apply() }

    companion object {
        private const val PREFS = "happychicks_prefs"
        const val KEY_BEST_SCORE = "best_score"
        const val KEY_COINS = "total_coins"
        const val KEY_CHAR = "selected_character"
        const val KEY_SKINS = "unlocked_skins"
        const val KEY_TOTAL_EGGS = "total_eggs"
        const val KEY_TOTAL_CHICKS = "total_chicks"
        const val KEY_HUNGER = "chick_hunger"
        const val KEY_CLEAN = "chick_cleanliness"
        const val KEY_LAST_CARE = "last_care_ts"
        const val KEY_DISCOVERED = "discovered_items"
        const val KEY_MUSIC_VOL = "music_volume"
        const val KEY_SFX_VOL = "sfx_volume"
        const val KEY_LANG = "language"

        // Achievement ids (keep stable)
        const val ACH_FIRST_HATCH = "1"
        const val ACH_TEN_CHICKS = "2"
        const val ACH_FIFTY_EGGS = "3"
    }
}
