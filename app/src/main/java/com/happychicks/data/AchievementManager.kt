package com.happychicks.data

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.happychicks.HappyChicksApp
import com.happychicks.R
import com.happychicks.core.AudioManager
import com.happychicks.core.TtsManager

/**
 * Centralized achievement evaluation. Call after eggs/chicks counters change.
 * Shows a toast + TTS callout when a new achievement unlocks.
 */
class AchievementManager(
    private val context: Context,
    private val repo: GameRepository,
    private val tts: TtsManager
) {
    private val audio: AudioManager get() = (context.applicationContext as HappyChicksApp).audio

    /** Evaluate all achievements; trigger unlock for any newly satisfied. */
    fun evaluate() {
        val totalEggs = repo.getTotalEggs()
        val totalChicks = repo.getTotalChicks()

        if (totalChicks >= 1) tryUnlock(
            GameRepository.ACH_FIRST_HATCH,
            R.string.achievement_first_hatch_name,
            R.string.achievement_first_hatch_desc
        )
        if (totalChicks >= 10) tryUnlock(
            GameRepository.ACH_TEN_CHICKS,
            R.string.achievement_ten_chicks_name,
            R.string.achievement_ten_chicks_desc
        )
        if (totalEggs >= 50) tryUnlock(
            GameRepository.ACH_FIFTY_EGGS,
            R.string.achievement_fifty_eggs_name,
            R.string.achievement_fifty_eggs_desc
        )
    }

    private fun tryUnlock(id: String, nameRes: Int, descRes: Int) {
        if (repo.unlockAchievement(id)) {
            val title = context.getString(R.string.achievement_unlocked)
            val name = context.getString(nameRes)
            val desc = context.getString(descRes)
            val msg = "$title: $name\n$desc"
            Toast.makeText(context, msg, Toast.LENGTH_LONG).apply {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
            audio.playSfx(AudioManager.SFX_ACHIEVEMENT)
            tts.speak("$title, $name")
            repo.addCoins(COINS_PER_ACHIEVEMENT)
        }
    }

    companion object {
        const val COINS_PER_ACHIEVEMENT = 20
    }
}
