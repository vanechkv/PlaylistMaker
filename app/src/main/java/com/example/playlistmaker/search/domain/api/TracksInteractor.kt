package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)

    fun saveTrackToHistory(track: Track, historyTracksList: ArrayList<Track>)

    fun saveHistory(tracks: List<Track>)

    fun getTrack(): Track

    fun getHistory(): List<Track>

    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}