package com.happychicks.game_farm

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.happychicks.R
import com.happychicks.core.AudioManager
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

/**
 * Farm exploration: 8 hidden slots arranged in a grid; each activation reveals an item (egg/chick/etc)
 * and persists a discovery id. Already-discovered items start visible.
 */
class FarmExploreActivity : BaseTvActivity() {

    private val itemIds = (1..8).map { "item_$it" }
    private lateinit var progress: TextView
    private lateinit var grid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_explore)
        setupOverscan(findViewById(R.id.root))
        progress = findViewById(R.id.progress)
        grid = findViewById(R.id.grid)

        buildGrid()
        updateProgress()
    }

    private fun buildGrid() {
        val firstFocus = arrayOfNulls<View>(1)
        itemIds.forEachIndexed { idx, id ->
            val discovered = repo.getDiscoveredItems().contains(id)
            val cell = ImageView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 180; height = 180
                    setMargins(12, 12, 12, 12)
                }
                setBackgroundResource(R.drawable.bg_menu_card)
                isFocusable = true; isClickable = true
                if (discovered) setImageResource(if (idx % 2 == 0) R.drawable.ic_egg else R.drawable.ic_chick)
                else setImageDrawable(null)
                setOnClickListener { reveal(idx, id, this) }
                contentDescription = "slot_$idx"
            }
            grid.addView(cell)
            if (idx == 0) firstFocus[0] = cell
        }
        FocusUtils.requestInitialFocus(firstFocus[0])
    }

    private fun reveal(idx: Int, id: String, view: ImageView) {
        if (repo.getDiscoveredItems().contains(id)) return
        repo.markDiscovered(id)
        view.setImageResource(if (idx % 2 == 0) R.drawable.ic_egg else R.drawable.ic_chick)
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200)
            .withEndAction { view.animate().scaleX(1f).scaleY(1f).setDuration(200).start() }
            .start()
        audio.playSfx(AudioManager.SFX_DISCOVER)
        repo.addCoins(3)
        tts.speak(getString(R.string.correct))
        updateProgress()
    }

    private fun updateProgress() {
        progress.text = getString(R.string.discovered_count, repo.getDiscoveredItems().size, itemIds.size)
    }
}
