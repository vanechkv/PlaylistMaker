package com.example.playlistmaker.playlists.data.repository

import com.example.playlistmaker.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.search.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.search.data.converters.TrackDbConvertor
import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.data.db.entity.PlaylistEntity
import com.example.playlistmaker.search.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor
): PlaylistRepository {


    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        if (track.trackId !in playlist.trackIds) {
            val newIds = playlist.trackIds + track.trackId
            val updatePlaylist = playlist.copy(
                trackIds = newIds,
                trackCount = newIds.size
            )
            appDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(updatePlaylist))
            appDatabase.playlistDao().insertTrack(trackDbConvertor.mapToPlaylist(track))
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase
            .playlistDao()
            .getPlaylists()
            .distinctUntilChanged()
            .map { convertFromPlaylistEntity(it) }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }
}