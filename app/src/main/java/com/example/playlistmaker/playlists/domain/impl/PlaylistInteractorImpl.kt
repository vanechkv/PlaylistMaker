package com.example.playlistmaker.playlists.domain.impl

import com.example.playlistmaker.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository): PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }
}