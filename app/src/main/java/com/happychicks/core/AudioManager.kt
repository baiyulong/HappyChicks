package com.happychicks.core

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.happychicks.R
import com.happychicks.data.GameRepository

class AudioManager(context: Context, private val repo: GameRepository) {

    private val appContext = context.applicationContext
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()

    private val soundIds = mutableMapOf<String, Int>()
    private var bgPlayer: MediaPlayer? = null

    companion object {
        const val SFX_LAY_EGG = "lay_egg"
        const val SFX_HATCH = "hatch"
        const val SFX_CORRECT = "correct"
        const val SFX_WRONG = "wrong"
        const val SFX_COIN = "coin"
        const val SFX_ACHIEVEMENT = "achievement"
        const val SFX_FOCUS = "focus"
        const val SFX_FEED = "feed"
        const val SFX_CLEAN = "clean"
        const val SFX_PUZZLE_SLIDE = "puzzle_slide"
        const val SFX_PUZZLE_WIN = "puzzle_win"
        const val SFX_DISCOVER = "discover"
    }

    fun init() {
        loadSfx(SFX_LAY_EGG, R.raw.sfx_lay_egg)
        loadSfx(SFX_HATCH, R.raw.sfx_hatch)
        loadSfx(SFX_CORRECT, R.raw.sfx_correct)
        loadSfx(SFX_WRONG, R.raw.sfx_wrong)
        loadSfx(SFX_COIN, R.raw.sfx_coin)
        loadSfx(SFX_ACHIEVEMENT, R.raw.sfx_achievement)
        loadSfx(SFX_FOCUS, R.raw.sfx_button_focus)
        loadSfx(SFX_FEED, R.raw.sfx_feed)
        loadSfx(SFX_CLEAN, R.raw.sfx_clean)
        loadSfx(SFX_PUZZLE_SLIDE, R.raw.sfx_puzzle_slide)
        loadSfx(SFX_PUZZLE_WIN, R.raw.sfx_puzzle_win)
        loadSfx(SFX_DISCOVER, R.raw.sfx_explore_discover)
    }

    private fun loadSfx(tag: String, rawResId: Int) {
        try {
            soundIds[tag] = soundPool.load(appContext, rawResId, 1)
        } catch (e: Exception) {
            Log.w("AudioManager", "Failed to load sfx $tag: ${e.message}")
        }
    }

    fun playSfx(tag: String) {
        val id = soundIds[tag] ?: return
        val vol = repo.getSfxVolume() / 100f
        soundPool.play(id, vol, vol, 1, 0, 1f)
    }

    fun startBgMusic() {
        if (bgPlayer != null) return
        try {
            val vol = repo.getMusicVolume() / 100f
            bgPlayer = MediaPlayer.create(appContext, R.raw.music_bg)?.apply {
                isLooping = true
                setVolume(vol, vol)
                start()
            }
        } catch (e: Exception) {
            Log.w("AudioManager", "BG music failed: ${e.message}")
        }
    }

    fun stopBgMusic() {
        try { bgPlayer?.stop(); bgPlayer?.release() } catch (_: Exception) {}
        bgPlayer = null
    }

    fun updateMusicVolume() {
        val vol = repo.getMusicVolume() / 100f
        try { bgPlayer?.setVolume(vol, vol) } catch (_: Exception) {}
    }

    fun release() {
        stopBgMusic()
        try { soundPool.release() } catch (_: Exception) {}
        soundIds.clear()
    }
}
