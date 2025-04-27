package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.Constans.PLAYLIST_PREFERENCES
import com.example.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.data.storage.SettingHistoryStorage
import com.example.playlistmaker.data.storage.TracksHistoryStorage
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private fun getTracksRepository(): TracksRepository {
        val networkClient = RetrofitNetworkClient()
        val shredPref = applicationContext.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        val tracksHistory = TracksHistoryStorage(shredPref)

        return TracksRepositoryImpl(networkClient, tracksHistory)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        val shredPref = applicationContext.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
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
}