package com.happychicks.profile

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import com.happychicks.R
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

class CharacterSelectActivity : BaseTvActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_select)
        setupOverscan(findViewById(R.id.root))

        val cardA = findViewById<LinearLayout>(R.id.card_a)
        val cardB = findViewById<LinearLayout>(R.id.card_b)
        cardA.setOnClickListener { select(0, getString(R.string.character_a)) }
        cardB.setOnClickListener { select(1, getString(R.string.character_b)) }
        FocusUtils.requestInitialFocus(if (repo.getSelectedCharacter() == 0) cardA else cardB)
    }

    private fun select(id: Int, name: String) {
        repo.setSelectedCharacter(id)
        tts.speak(name)
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
        finish()
    }
}
