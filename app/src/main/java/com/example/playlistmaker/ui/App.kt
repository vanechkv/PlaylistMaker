package com.example.playlistmaker.ui

import android.app.Application
import com.example.playlistmaker.Creator

class App : Application() {

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(this) }

    override fun onCreate() {
        super.onCreate()

        settingsInteractor.setDarkTheme(settingsInteractor.isDarkTheme())
    }
}