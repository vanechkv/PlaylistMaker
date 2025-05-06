package com.example.playlistmaker.app

import android.app.Application
import com.example.playlistmaker.creator.Creator

class App : Application() {

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor() }

    override fun onCreate() {
        super.onCreate()

        Creator.init(this)
        settingsInteractor.setDarkTheme(settingsInteractor.isDarkTheme())
    }
}