package com.example.playlistmaker.playlists.domain.api

import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>
}