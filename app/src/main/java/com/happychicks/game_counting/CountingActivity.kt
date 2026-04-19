package com.happychicks.game_counting

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.happychicks.R
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

class CountingActivity : BaseTvActivity() {

    private lateinit var items: LinearLayout
    private lateinit var answers: LinearLayout
    private var correct = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counting)
        setupOverscan(findViewById(R.id.root))
        items = findViewById(R.id.items)
        answers = findViewById(R.id.answers)
        nextQuestion()
    }

    private fun nextQuestion() {
        items.removeAllViews(); answers.removeAllViews()
        correct = (1..5).random()
        tts.speak(getString(R.string.counting_prompt))

        repeat(correct) {
            val iv = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(140, 140).apply { setMargins(8, 0, 8, 0) }
                setImageResource(if ((0..1).random() == 0) R.drawable.ic_egg else R.drawable.ic_chick)
            }
            items.addView(iv)
        }

        val options = mutableSetOf(correct)
        while (options.size < 4) options.add((1..6).random())
        val shuffled = options.shuffled()

        val firstFocus = arrayOfNulls<View>(1)
        shuffled.forEachIndexed { idx, n ->
            val btn = Button(this).apply {
                text = n.toString()
                layoutParams = LinearLayout.LayoutParams(120, 120).apply { setMargins(12, 0, 12, 0) }
                setBackgroundResource(R.drawable.bg_tv_button)
                textSize = 32f
                setTextColor(resources.getColor(R.color.text_primary, null))
                setOnClickListener { onPick(n) }
            }
            answers.addView(btn)
            if (idx == 0) firstFocus[0] = btn
        }
        FocusUtils.requestInitialFocus(firstFocus[0])
    }

    private fun onPick(n: Int) {
        if (n == correct) {
            tts.speak("$correct, " + getString(R.string.correct))
            repo.addCoins(2)
            answers.postDelayed({ nextQuestion() }, 1200)
        } else {
            tts.speak(getString(R.string.try_again))
        }
    }
}
