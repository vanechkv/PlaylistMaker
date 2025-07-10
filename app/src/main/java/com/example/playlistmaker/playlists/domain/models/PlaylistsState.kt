package com.example.playlistmaker.playlists.domain.models

import com.example.playlistmaker.search.domain.models.Playlist

interface PlaylistsState {

    object Loading : PlaylistsState

    data class Content(
        val playlists: List<Playlist>
    ): PlaylistsState

    data class Empty(
        val message: String
    ) : PlaylistsState
}