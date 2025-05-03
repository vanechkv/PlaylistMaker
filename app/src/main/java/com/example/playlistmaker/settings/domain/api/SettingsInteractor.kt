package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {

    fun isDarkTheme(): Boolean

    fun setDarkTheme(enabled: Boolean)
}