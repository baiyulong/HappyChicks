package com.happychicks.game_core

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.happychicks.R
import com.happychicks.core.AudioManager
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

/** 60-second time challenge: count eggs laid before timer ends. Persists best score. */
class ChallengeActivity : BaseTvActivity() {

    private var score = 0
    private var running = true
    private var timer: CountDownTimer? = null

    private lateinit var timerText: TextView
    private lateinit var scoreText: TextView
    private lateinit var layBtn: Button
    private lateinit var stage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)
        setupOverscan(findViewById(R.id.root))

        timerText = findViewById(R.id.timer_text)
        scoreText = findViewById(R.id.score_text)
        layBtn = findViewById(R.id.btn_lay)
        stage = findViewById(R.id.stage)

        layBtn.setOnClickListener {
            if (!running) return@setOnClickListener
            score += 1
            repo.addEggs(1)
            scoreText.text = getString(R.string.egg_count, score)
            stage.setImageResource(R.drawable.ic_egg)
            audio.playSfx(AudioManager.SFX_LAY_EGG)
            stage.postDelayed({ if (running) stage.setImageResource(R.drawable.ic_hen) }, 250)
            achievements.evaluate()
        }

        FocusUtils.requestInitialFocus(layBtn)
        startTimer()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(DURATION_MS, 1000) {
            override fun onTick(remainingMs: Long) {
                val sec = ((remainingMs + 999) / 1000).toInt()
                timerText.text = getString(R.string.challenge_time_left, sec)
            }
            override fun onFinish() {
                running = false
                layBtn.isEnabled = false
                showResult()
            }
        }.start()
    }

    private fun showResult() {
        val isBest = repo.updateBestScore(score)
        val best = repo.getBestScore()
        val msg = buildString {
            append(getString(R.string.challenge_result_score, score)); append('\n')
            append(getString(R.string.challenge_best_score, best))
            if (isBest) { append("\n"); append(getString(R.string.challenge_new_best)) }
        }
        tts.speak(getString(R.string.challenge_result_title))
        AlertDialog.Builder(this)
            .setTitle(R.string.challenge_result_title)
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton(R.string.btn_confirm) { _, _ -> finish() }
            .show()
    }

    override fun onDestroy() {
        timer?.cancel(); super.onDestroy()
    }

    companion object { private const val DURATION_MS = 60_000L }
}
