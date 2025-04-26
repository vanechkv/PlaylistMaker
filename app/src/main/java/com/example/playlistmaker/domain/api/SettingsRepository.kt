package com.example.playlistmaker.domain.api

interface SettingsRepository {

    fun isDarkTheme(): Boolean

    fun setDarkTheme(enabled: Boolean)
}