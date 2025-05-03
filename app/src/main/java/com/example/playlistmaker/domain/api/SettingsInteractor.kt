package com.example.playlistmaker.domain.api

interface SettingsInteractor {

    fun isDarkTheme(): Boolean

    fun setDarkTheme(enabled: Boolean)
}