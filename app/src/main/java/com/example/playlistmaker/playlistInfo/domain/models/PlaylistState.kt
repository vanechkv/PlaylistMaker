package com.example.playlistmaker.playlistInfo.domain.models

import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistState {

    object Loading : PlaylistState

    data class Content(
        val playlists: Playlist,
        val tracks: List<Track>
    ): PlaylistState

    data class Empty(
        val message: String
    ) : PlaylistState
}