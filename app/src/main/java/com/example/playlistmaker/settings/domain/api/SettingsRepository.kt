package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {

    fun isDarkTheme(): Boolean

    fun setDarkTheme(enabled: Boolean)
}