package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun saveTrackToHistory(track: Track, historyTracksList: ArrayList<Track>)

    fun saveHistory(tracks: List<Track>)

    fun getTrack(): Track

    fun getHistory(): List<Track>

    fun clearHistory()
}