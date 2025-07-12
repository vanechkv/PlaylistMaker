package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.search.data.converters.TrackDbConvertor
import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.data.db.entity.PlaylistEntity
import com.example.playlistmaker.search.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.data.db.entity.TrackEntity
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.storage.TracksHistoryStorage
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksHistory: TracksHistoryStorage,
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(""))
            }

            200 -> {
                val tracks = (response as TracksSearchResponse).tracks.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.getCoverArtwork(),
                        it.collectionName,
                        it.getReleaseYear(),
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
                emit(Resource.Success(tracks))
            }

            else -> {
                emit(Resource.Error(""))
            }
        }
    }

    override fun saveTrackToHistory(track: Track, historyTracksList: ArrayList<Track>) {
        tracksHistory.saveTrack(track, historyTracksList)
    }

    override fun saveOnlyTrack(track: Track) {
        tracksHistory.saveOnlyTrack(track)
    }

    override fun saveHistory(tracks: List<Track>) {
        tracksHistory.saveTracks(tracks)
    }

    override fun getTrack(): Track {
        return tracksHistory.getTrack()
    }

    override fun getHistory(): List<Track> {
        return tracksHistory.getTracks()
    }

    override fun clearHistory() {
        tracksHistory.clear()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase
            .trackDao()
            .getAllTrack()
            .distinctUntilChanged()
            .map { convertFromTrackEntity(it) }
    }

    override fun getFavoriteTracksId(): Flow<List<Int>> = flow {
        emit(appDatabase.trackDao().getAllTrackId())
    }

    override fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<Track>> {
        return appDatabase
            .trackDao()
            .getTracksInPlaylist(trackIds)
            .distinctUntilChanged()
            .map { entity ->
                val tracks = convertFromPlaylistTrackEntity(entity)
                tracks.sortedBy { track -> trackIds.indexOf(track.trackId) }

            }
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertFromPlaylistTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}