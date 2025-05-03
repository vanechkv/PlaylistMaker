package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.data.storage.SettingHistoryStorage
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val darkMode: SettingHistoryStorage) : SettingsRepository {
    override fun isDarkTheme(): Boolean {
        return darkMode.isDarkTheme()
    }

    override fun setDarkTheme(enabled: Boolean) {
        darkMode.setDarkTheme(enabled)
    }
}