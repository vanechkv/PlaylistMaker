package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.storage.SettingHistoryStorage
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val darkMode: SettingHistoryStorage) : SettingsRepository {
    override fun isDarkTheme(): Boolean {
        return darkMode.isDarkTheme()
    }

    override fun setDarkTheme(enabled: Boolean) {
        darkMode.setDarkTheme(enabled)
    }
}