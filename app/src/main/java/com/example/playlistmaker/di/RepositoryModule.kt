package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.playlists.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.search.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.search.data.converters.TrackDbConvertor
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.impl.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }

    factory {
        TrackDbConvertor()
    }

    factory {
        PlaylistDbConvertor(get())
    }
}