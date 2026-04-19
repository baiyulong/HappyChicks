package com.happychicks.game_shape

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.happychicks.R
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

class ShapeActivity : BaseTvActivity() {

    private data class Shape(val drawableRes: Int, val nameRes: Int)

    private val shapes = listOf(
        Shape(R.drawable.shape_circle, R.string.shape_circle),
        Shape(R.drawable.shape_square, R.string.shape_square),
        Shape(R.drawable.shape_triangle, R.string.shape_triangle)
    )
    private val tints = intArrayOf(
        Color.parseColor("#E53935"), Color.parseColor("#1E88E5"),
        Color.parseColor("#43A047"), Color.parseColor("#FDD835"),
        Color.parseColor("#FB8C00")
    )

    private lateinit var promptView: TextView
    private lateinit var optionsRow: LinearLayout
    private var targetIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shape)
        setupOverscan(findViewById(R.id.root))
        promptView = findViewById(R.id.prompt)
        optionsRow = findViewById(R.id.options)
        nextQuestion()
    }

    private fun nextQuestion() {
        optionsRow.removeAllViews()
        val shuffled = shapes.shuffled()
        targetIndex = (0 until shuffled.size).random()
        val target = shuffled[targetIndex]

        promptView.text = getString(R.string.shape_prompt, getString(target.nameRes))
        tts.speak(getString(R.string.shape_prompt, getString(target.nameRes)))

        val firstFocus = arrayOfNulls<View>(1)
        shuffled.forEachIndexed { idx, shape ->
            val iv = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200).apply { setMargins(24, 0, 24, 0) }
                setImageResource(shape.drawableRes)
                colorFilter = PorterDuffColorFilter(tints[idx % tints.size], PorterDuff.Mode.SRC_IN)
                isFocusable = true; isClickable = true
                background = resources.getDrawable(R.drawable.bg_menu_card, null)
                setOnClickListener { onPick(idx, shape) }
            }
            optionsRow.addView(iv)
            if (idx == 0) firstFocus[0] = iv
        }
        FocusUtils.requestInitialFocus(firstFocus[0])
    }

    private fun onPick(idx: Int, picked: ShapeActivity.Shape) {
        val targetName = promptView.text.toString()
        if (idx == targetIndex) {
            tts.speak(getString(R.string.correct))
            repo.addCoins(2)
            promptView.postDelayed({ nextQuestion() }, 1000)
        } else {
            tts.speak(getString(R.string.try_again))
        }
    }
}
