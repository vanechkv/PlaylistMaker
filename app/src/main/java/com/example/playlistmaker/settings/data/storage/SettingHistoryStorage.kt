package com.example.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingHistoryStorage(private val shredPref: SharedPreferences) {

    companion object {
        private const val DARK_MODE_KEY = "key_for_dark_mode"
    }

    fun isDarkTheme(): Boolean {
        return shredPref.getBoolean(DARK_MODE_KEY, false)
    }

    fun setDarkTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else AppCompatDelegate.MODE_NIGHT_NO
        )
        shredPref.edit()
            .putBoolean(DARK_MODE_KEY, enabled)
            .apply()
    }
}