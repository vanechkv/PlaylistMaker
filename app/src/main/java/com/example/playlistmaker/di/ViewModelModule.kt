package com.example.playlistmaker.di

import com.example.playlistmaker.featured.ui.FeaturedViewModel
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.playlists.ui.PlaylistsViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
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
        AudioPlayerViewModel(get(),get())
    }

    viewModel { (message: String) ->
        FeaturedViewModel(message)
    }

    viewModel { (message: String) ->
        PlaylistsViewModel(message)
    }
}