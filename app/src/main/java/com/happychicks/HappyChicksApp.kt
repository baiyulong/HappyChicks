package com.happychicks

import android.app.Application
import com.happychicks.core.AudioManager
import com.happychicks.core.LocaleHelper
import com.happychicks.core.TtsManager
import com.happychicks.data.GameRepository

class HappyChicksApp : Application() {

    val repository: GameRepository by lazy { GameRepository(this) }
    val tts: TtsManager by lazy { TtsManager(this) }
    val audio: AudioManager by lazy { AudioManager(this, repository) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        LocaleHelper.applySavedLocale(this, repository.getLanguage())
        audio.init()
    }

    companion object {
        lateinit var instance: HappyChicksApp
            private set
    }
}
