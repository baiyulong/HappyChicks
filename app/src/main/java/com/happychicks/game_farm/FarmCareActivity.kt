package com.happychicks.game_farm

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.happychicks.R
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils
import java.util.concurrent.TimeUnit

/**
 * Farm care: hunger/cleanliness decay over real time (1 point per minute of absence, capped).
 * Feed & clean actions restore stats to 100.
 */
class FarmCareActivity : BaseTvActivity() {

    private lateinit var hungerText: TextView
    private lateinit var cleanText: TextView
    private lateinit var feedBtn: Button
    private lateinit var cleanBtn: Button
    private lateinit var chickImg: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_care)
        setupOverscan(findViewById(R.id.root))

        hungerText = findViewById(R.id.tv_hunger)
        cleanText = findViewById(R.id.tv_clean)
        feedBtn = findViewById(R.id.btn_feed)
        cleanBtn = findViewById(R.id.btn_clean)
        chickImg = findViewById(R.id.chick_img)

        applyDecay()
        refresh()

        feedBtn.setOnClickListener {
            repo.setHunger(100); refresh(); animatePulse()
            tts.speak(getString(R.string.action_feed))
        }
        cleanBtn.setOnClickListener {
            repo.setCleanliness(100); refresh(); animatePulse()
            tts.speak(getString(R.string.action_clean))
        }
        FocusUtils.requestInitialFocus(feedBtn)
    }

    private fun applyDecay() {
        val now = System.currentTimeMillis()
        val last = repo.getLastCareTimestamp()
        val elapsedMin = TimeUnit.MILLISECONDS.toMinutes(now - last).toInt().coerceAtLeast(0)
        if (elapsedMin > 0) {
            repo.setHunger(repo.getHunger() - elapsedMin)
            repo.setCleanliness(repo.getCleanliness() - elapsedMin)
        }
        repo.setLastCareTimestamp(now)
    }

    private fun refresh() {
        hungerText.text = getString(R.string.hunger_level, repo.getHunger())
        cleanText.text = getString(R.string.cleanliness_level, repo.getCleanliness())
    }

    private fun animatePulse() {
        chickImg.startAnimation(AlphaAnimation(0.4f, 1f).apply { duration = 300 })
    }

    override fun onPause() {
        repo.setLastCareTimestamp(System.currentTimeMillis())
        super.onPause()
    }
}
