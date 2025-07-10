package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun saveTrackToHistory(track: Track, historyTracksList: ArrayList<Track>) {
        repository.saveTrackToHistory(track, historyTracksList)
    }

    override fun saveOnlyTrack(track: Track) {
        repository.saveOnlyTrack(track)
    }

    override fun saveHistory(tracks: List<Track>) {
        repository.saveHistory(tracks)
    }

    override fun getTrack(): Track {
        return repository.getTrack()
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        repository.deleteTrackFromFavorites(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun getFavoriteTracksId(): Flow<List<Int>> {
        return repository.getFavoriteTracksId()
    }
}