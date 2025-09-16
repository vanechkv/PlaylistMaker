package com.example.playlistmaker.main.domain.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.playlistmaker.R

sealed class Screen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    object Search : Screen(Routes.Search.name, R.string.search, R.drawable.vector_search)
    object Playlists : Screen(Routes.Playlist.name, R.string.playlist, R.drawable.vector_playlist)
    object Settings : Screen(Routes.Settings.name, R.string.settings, R.drawable.vector_settings)
}