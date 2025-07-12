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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor
) : PlaylistRepository {


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

    override fun getPlaylistById(playlistId: Long): Flow<Playlist> {
        return appDatabase
            .playlistDao()
            .getPlaylistById(playlistId)
            .distinctUntilChanged()
            .map { playlistDbConvertor.map(it) }
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, track: Track) {
        val playlist = getPlaylistById(playlistId).first()
        val updateTrackIds = playlist.trackIds.filter { it != track.trackId }
        val updateTrackCount = playlist.trackCount - 1
        val updatePlaylist = playlistDbConvertor.map(
            playlist.copy(
                trackIds = updateTrackIds,
                trackCount = updateTrackCount
            )
        )
        appDatabase.playlistDao().updatePlaylist(updatePlaylist)
        val playlists = getPlaylists().first()
        val isTrackInPlaylist = playlists.any { it.trackIds.contains(track.trackId) }
        if (!isTrackInPlaylist) appDatabase.trackDao().deletePlaylistTrack(track.trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlistDbConvertor.map(playlist))

        val playlists = getPlaylists().first()
        playlist.trackIds.forEach { trackId ->
            val isTrackInPlaylist = playlists.any { it.trackIds.contains(trackId) }
            if (!isTrackInPlaylist) appDatabase.trackDao().deletePlaylistTrack(trackId)
        }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }
}