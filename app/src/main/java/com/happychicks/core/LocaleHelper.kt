package com.happychicks.core

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun applySavedLocale(context: Context, lang: String) {
        val locale = when (lang) {
            "en" -> Locale.ENGLISH
            else -> Locale.SIMPLIFIED_CHINESE
        }
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        res.updateConfiguration(config, res.displayMetrics)
    }

    fun currentLocale(context: Context): Locale {
        return context.resources.configuration.locales[0]
    }
}
