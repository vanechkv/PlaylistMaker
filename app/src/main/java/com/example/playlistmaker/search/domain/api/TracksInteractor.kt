package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>

    fun saveTrackToHistory(track: Track, historyTracksList: ArrayList<Track>)

    fun saveOnlyTrack(track: Track)

    fun saveHistory(tracks: List<Track>)

    fun getTrack(): Track

    fun getHistory(): List<Track>

    fun clearHistory()

    suspend fun addTrackToFavorites(track: Track)

    suspend fun deleteTrackFromFavorites(track: Track)

    fun getFavoriteTracks(): Flow<List<Track>>

    fun getFavoriteTracksId(): Flow<List<Int>>

    fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<Track>>
}