package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }

    override fun saveTrackToHistory(track: Track, historyTracksList: ArrayList<Track>) {
        repository.saveTrackToHistory(track, historyTracksList)
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
}