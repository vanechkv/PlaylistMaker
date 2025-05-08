package com.example.playlistmaker.playlists.domain.models

import com.example.playlistmaker.featured.domain.models.FeaturedState

interface PlaylistsState {

    data class Empty(
        val message: String
    ) : PlaylistsState
}