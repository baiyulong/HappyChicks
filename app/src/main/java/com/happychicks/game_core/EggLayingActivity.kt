package com.happychicks.game_core

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.happychicks.R
import com.happychicks.core.AudioManager
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

/**
 * Free Play: Press OK to lay an egg. After a 10s countdown, the egg hatches into a chick.
 * Unlimited. Eggs/chicks totals are persisted for achievements.
 */
class EggLayingActivity : BaseTvActivity() {

    private var eggs = 0
    private var chicks = 0
    private var hatching = false
    private var timer: CountDownTimer? = null

    private lateinit var statsView: TextView
    private lateinit var statusText: TextView
    private lateinit var stage: ImageView
    private lateinit var layBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_egg_laying)
        setupOverscan(findViewById(R.id.root))

        statsView = findViewById(R.id.stats)
        statusText = findViewById(R.id.status_text)
        stage = findViewById(R.id.stage)
        layBtn = findViewById(R.id.btn_lay)

        layBtn.setOnClickListener { layEgg() }
        FocusUtils.requestInitialFocus(layBtn)
        updateStats()
    }

    private fun layEgg() {
        if (hatching) return
        eggs += 1
        repo.addEggs(1)
        stage.setImageResource(R.drawable.ic_egg)
        hatching = true
        layBtn.isEnabled = false
        audio.playSfx(AudioManager.SFX_LAY_EGG)
        tts.speak(getString(R.string.action_lay_egg))
        startHatch()
        achievements.evaluate()
        updateStats()
    }

    private fun startHatch() {
        timer?.cancel()
        timer = object : CountDownTimer(HATCH_MS, 1000) {
            override fun onTick(remainingMs: Long) {
                val sec = ((remainingMs + 999) / 1000).toInt()
                statusText.text = getString(R.string.hatching_in, sec)
            }
            override fun onFinish() {
                chicks += 1
                repo.addChicks(1)
                eggs = maxOf(0, eggs - 1)
                statusText.text = getString(R.string.chick_hatched)
                stage.setImageResource(R.drawable.ic_chick)
                audio.playSfx(AudioManager.SFX_HATCH)
                tts.speak(getString(R.string.chick_hatched))
                // small coin reward
                repo.addCoins(COINS_PER_HATCH)
                hatching = false
                layBtn.isEnabled = true
                achievements.evaluate()
                updateStats()
            }
        }.start()
    }

    private fun updateStats() {
        statsView.text = getString(R.string.egg_count, eggs) + "   " +
            getString(R.string.chick_count, chicks)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val HATCH_MS = 10_000L
        private const val COINS_PER_HATCH = 5
    }
}
