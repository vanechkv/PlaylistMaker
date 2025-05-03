package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.storage.TracksHistoryStorage
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient? = null,
    private val tracksHistory: TracksHistoryStorage
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track>? {
        if (networkClient != null) {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            return if (response.resultCode == 200) {
                (response as TracksSearchResponse).tracks.map {
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
            } else {
                null
            }
        } else {
            return null
        }
    }

    override fun saveTrackToHistory(track: Track, historyTracksList: ArrayList<Track>) {
        tracksHistory.saveTrack(track, historyTracksList)
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
}