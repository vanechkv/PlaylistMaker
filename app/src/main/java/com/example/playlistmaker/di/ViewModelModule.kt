package com.example.playlistmaker.di

import com.example.playlistmaker.featured.ui.FeaturedViewModel
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.playlists.ui.AddPlaylistViewModel
import com.example.playlistmaker.playlists.ui.PlaylistsViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        AudioPlayerViewModel(get(),get(), get())
    }

    viewModel { (message: String) ->
        FeaturedViewModel(message, get())
    }

    viewModel { (message: String) ->
        PlaylistsViewModel(message, get())
    }

    viewModel {
        AddPlaylistViewModel(get(), androidContext())
    }
}