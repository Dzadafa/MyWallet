package com.dzadafa.mywallet

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREFS_NAME = "ThemePrefs"
    private const val KEY_THEME = "theme_mode"

    const val LIGHT_MODE = AppCompatDelegate.MODE_NIGHT_NO
    const val DARK_MODE = AppCompatDelegate.MODE_NIGHT_YES
    const val SYSTEM_DEFAULT = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    fun applyTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val selectedTheme = prefs.getInt(KEY_THEME, SYSTEM_DEFAULT)
        AppCompatDelegate.setDefaultNightMode(selectedTheme)
    }

    fun saveTheme(context: Context, themeMode: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME, themeMode).apply()
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    fun getCurrentTheme(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME, SYSTEM_DEFAULT)
    }
}
