package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.constants.Constants.PLAYLIST_PREFERENCES
import com.example.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.settings.data.storage.SettingHistoryStorage
import com.example.playlistmaker.search.data.storage.TracksHistoryStorage
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.sharing.domain.api.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private fun getTracksRepository(): TracksRepository {
        val networkClient = RetrofitNetworkClient(applicationContext)
        val shredPref =
            applicationContext.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        val tracksHistory = TracksHistoryStorage(shredPref)

        return TracksRepositoryImpl(networkClient, tracksHistory)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        val shredPref =
            applicationContext.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        val darkMode = SettingHistoryStorage(shredPref)
        return SettingsRepositoryImpl(darkMode)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(applicationContext)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(applicationContext, getExternalNavigator())
    }
}