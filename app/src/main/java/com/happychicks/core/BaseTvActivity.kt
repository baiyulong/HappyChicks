package com.happychicks.core

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.happychicks.HappyChicksApp
import com.happychicks.data.AchievementManager
import com.happychicks.data.GameRepository

/**
 * Base TV activity: fullscreen, overscan-safe, with repository & TTS injected.
 * Subclasses override onBackPressed via BACK key handling.
 */
abstract class BaseTvActivity : AppCompatActivity() {

    protected val app: HappyChicksApp get() = application as HappyChicksApp
    protected val repo: GameRepository get() = app.repository
    protected val tts: TtsManager get() = app.tts
    protected val audio: AudioManager get() = app.audio
    protected val achievements: AchievementManager by lazy { AchievementManager(this, repo, tts) }

    override fun onStart() {
        super.onStart()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )
    }

    override fun onResume() {
        super.onResume()
        audio.startBgMusic()
    }

    override fun onPause() {
        audio.stopBgMusic()
        super.onPause()
    }

    /** Apply overscan padding; call after setContentView. */
    protected fun setupOverscan(root: View) {
        OverscanHelper.applyToRoot(this, root)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (KeyDispatcher.isBack(event)) {
            finish(); return true
        }
        return super.dispatchKeyEvent(event)
    }
}
