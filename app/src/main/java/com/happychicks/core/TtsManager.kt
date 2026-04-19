package com.happychicks.core

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/** TTS wrapper with Chinese-first and English fallback. Silent if engine unavailable. */
class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ready = false
    private var pendingSpeech: String? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.w(TAG, "TTS init failed: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val current = LocaleHelper.currentLocale(context)
            val result = tts?.setLanguage(current) ?: TextToSpeech.LANG_NOT_SUPPORTED
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            ready = true
            pendingSpeech?.let { speak(it); pendingSpeech = null }
        } else {
            Log.w(TAG, "TTS init status=$status")
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return
        if (!ready) { pendingSpeech = text; return }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hc-${System.currentTimeMillis()}")
    }

    fun shutdown() {
        try { tts?.stop(); tts?.shutdown() } catch (_: Exception) {}
        tts = null; ready = false
    }

    companion object { private const val TAG = "TtsManager" }
}
