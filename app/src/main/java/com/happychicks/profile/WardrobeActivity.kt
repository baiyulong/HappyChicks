package com.happychicks.profile

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.happychicks.R
import com.happychicks.core.AudioManager
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

class WardrobeActivity : BaseTvActivity() {

    private data class Skin(val id: String, val colorHex: String, val cost: Int)

    private val skins = listOf(
        Skin("red", "#E53935", 30),
        Skin("blue", "#1E88E5", 30),
        Skin("green", "#43A047", 30),
        Skin("purple", "#8E24AA", 50),
        Skin("pink", "#EC407A", 50),
        Skin("orange", "#FB8C00", 50),
        Skin("yellow", "#FDD835", 20),
        Skin("white", "#FFFFFF", 20),
    )

    private lateinit var coinsText: TextView
    private lateinit var grid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wardrobe)
        setupOverscan(findViewById(R.id.root))
        coinsText = findViewById(R.id.coins_text)
        grid = findViewById(R.id.grid)
        refresh()
    }

    private fun refresh() {
        coinsText.text = getString(R.string.coins, repo.getCoins())
        grid.removeAllViews()
        val firstFocus = arrayOfNulls<View>(1)
        skins.forEachIndexed { idx, skin ->
            val unlocked = repo.isSkinUnlocked(skin.id)
            val cell = FrameLayout(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 200; height = 240
                    setMargins(12, 12, 12, 12)
                }
                isFocusable = true; isClickable = true
                setBackgroundResource(R.drawable.bg_menu_card)
                setOnClickListener { onTap(skin) }
            }
            val iv = ImageView(this).apply {
                layoutParams = ViewGroup.LayoutParams(180, 180)
                setImageResource(R.drawable.ic_chick)
                colorFilter = PorterDuffColorFilter(
                    android.graphics.Color.parseColor(skin.colorHex), PorterDuff.Mode.SRC_IN
                )
            }
            val label = TextView(this).apply {
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    gravity = android.view.Gravity.BOTTOM
                }
                gravity = android.view.Gravity.CENTER
                text = if (unlocked) getString(R.string.unlocked)
                else getString(R.string.unlock_cost, skin.cost)
                textSize = 16f
                setTextColor(resources.getColor(R.color.text_primary, null))
            }
            cell.addView(iv); cell.addView(label)
            grid.addView(cell)
            if (idx == 0) firstFocus[0] = cell
        }
        FocusUtils.requestInitialFocus(firstFocus[0])
    }

    private fun onTap(skin: Skin) {
        if (repo.isSkinUnlocked(skin.id)) {
            tts.speak(getString(R.string.unlocked)); return
        }
        if (repo.spendCoins(skin.cost)) {
            repo.unlockSkin(skin.id)
            audio.playSfx(AudioManager.SFX_COIN)
            Toast.makeText(this, R.string.unlock_success, Toast.LENGTH_SHORT).show()
            tts.speak(getString(R.string.unlock_success))
        } else {
            audio.playSfx(AudioManager.SFX_WRONG)
            Toast.makeText(this, R.string.not_enough_coins, Toast.LENGTH_SHORT).show()
            tts.speak(getString(R.string.not_enough_coins))
        }
        refresh()
    }
}
