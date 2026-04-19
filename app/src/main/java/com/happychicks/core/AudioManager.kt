package com.happychicks.core

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.happychicks.data.GameRepository

/**
 * Lightweight sound effect manager using SoundPool.
 * Music volume and SFX volume are independently controlled via GameRepository.
 * Uses silent placeholder by default – add wav/ogg files to res/raw later.
 */
class AudioManager(context: Context, private val repo: GameRepository) {

    private val appContext = context.applicationContext
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()

    private val soundIds = mutableMapOf<String, Int>()

    fun registerSfx(tag: String, rawResId: Int) {
        try {
            val id = soundPool.load(appContext, rawResId, 1)
            soundIds[tag] = id
        } catch (_: Exception) {}
    }

    fun playSfx(tag: String) {
        val id = soundIds[tag] ?: return
        val vol = repo.getSfxVolume() / 100f
        soundPool.play(id, vol, vol, 1, 0, 1f)
    }

    fun release() {
        try { soundPool.release() } catch (_: Exception) {}
        soundIds.clear()
    }
}
