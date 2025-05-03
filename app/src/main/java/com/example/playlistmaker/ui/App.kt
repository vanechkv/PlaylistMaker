package com.example.playlistmaker.ui

import android.app.Application
import com.example.playlistmaker.Creator

class App : Application() {

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor() }

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        settingsInteractor.setDarkTheme(settingsInteractor.isDarkTheme())
    }
}