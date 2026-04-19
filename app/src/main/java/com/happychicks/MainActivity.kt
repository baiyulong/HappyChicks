package com.happychicks

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils
import com.happychicks.game_core.ChallengeActivity
import com.happychicks.game_core.EggLayingActivity
import com.happychicks.game_coloring.ColoringActivity
import com.happychicks.game_counting.CountingActivity
import com.happychicks.game_farm.FarmCareActivity
import com.happychicks.game_farm.FarmExploreActivity
import com.happychicks.game_puzzle.PuzzleActivity
import com.happychicks.game_shape.ShapeActivity
import com.happychicks.profile.CharacterSelectActivity
import com.happychicks.profile.WardrobeActivity
import com.happychicks.settings.SettingsActivity

class MainActivity : BaseTvActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupOverscan(findViewById(R.id.root))

        bind(R.id.card_free) { start(EggLayingActivity::class.java) }
        bind(R.id.card_challenge) { start(ChallengeActivity::class.java) }

        bind(R.id.card_coloring) { start(ColoringActivity::class.java) }
        bind(R.id.card_puzzle) { start(PuzzleActivity::class.java) }
        bind(R.id.card_shape) { start(ShapeActivity::class.java) }
        bind(R.id.card_counting) { start(CountingActivity::class.java) }

        bind(R.id.card_farm_care) { start(FarmCareActivity::class.java) }
        bind(R.id.card_farm_explore) { start(FarmExploreActivity::class.java) }
        bind(R.id.card_wardrobe) { start(WardrobeActivity::class.java) }
        bind(R.id.card_character) { start(CharacterSelectActivity::class.java) }

        bind(R.id.card_settings) { start(SettingsActivity::class.java) }

        FocusUtils.requestInitialFocus(findViewById(R.id.card_free))
    }

    private fun bind(id: Int, action: () -> Unit) {
        findViewById<TextView>(id)?.setOnClickListener { action() }
    }

    private fun start(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }
}
