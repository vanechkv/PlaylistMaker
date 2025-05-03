package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.storage.TracksHistoryStorage
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksHistory: TracksHistoryStorage
) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("")
            }

            200 -> {
                Resource.Success((response as TracksSearchResponse).tracks.map {
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
                })
            }

            else -> {
                Resource.Error("")
            }
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