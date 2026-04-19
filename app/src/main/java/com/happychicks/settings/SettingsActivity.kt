package com.happychicks.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import com.happychicks.MainActivity
import com.happychicks.R
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils
import com.happychicks.core.LocaleHelper

class SettingsActivity : BaseTvActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupOverscan(findViewById(R.id.root))

        val sbMusic = findViewById<SeekBar>(R.id.sb_music).apply { progress = repo.getMusicVolume() }
        val sbSfx = findViewById<SeekBar>(R.id.sb_sfx).apply { progress = repo.getSfxVolume() }
        val btnZh = findViewById<Button>(R.id.btn_lang_zh)
        val btnEn = findViewById<Button>(R.id.btn_lang_en)
        val btnReset = findViewById<Button>(R.id.btn_reset)

        sbMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) { repo.setMusicVolume(progress) }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        sbSfx.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) { repo.setSfxVolume(progress) }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        btnZh.setOnClickListener { switchLanguage("zh") }
        btnEn.setOnClickListener { switchLanguage("en") }

        btnReset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.settings_reset)
                .setMessage(R.string.settings_reset_confirm)
                .setPositiveButton(R.string.btn_confirm) { _, _ ->
                    repo.resetAll()
                    Toast.makeText(this, R.string.settings_reset_done, Toast.LENGTH_SHORT).show()
                    val i = Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i); finish()
                }
                .setNegativeButton(R.string.btn_cancel, null)
                .show()
        }

        FocusUtils.requestInitialFocus(sbMusic)
    }

    private fun switchLanguage(lang: String) {
        repo.setLanguage(lang)
        LocaleHelper.applySavedLocale(this, lang)
        // Restart activity stack to apply new locale
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i); finish()
    }
}
