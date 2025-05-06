package com.example.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.constants.Constants.DARK_MODE_KEY

class SettingHistoryStorage(private val shredPref: SharedPreferences) {

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