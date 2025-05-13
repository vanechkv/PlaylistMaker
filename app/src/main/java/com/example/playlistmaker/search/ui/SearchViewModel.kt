package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {
    private var searchText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val historyLiveData = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = historyLiveData

    private val historyTracksList = ArrayList<Track>()

    init {
        historyTracksList.addAll(tracksInteractor.getHistory())
        historyLiveData.postValue(historyTracksList)
    }

    fun onTrackClick(track: Track) {
        tracksInteractor.saveTrackToHistory(track, historyTracksList)
        historyLiveData.postValue(historyTracksList)
    }

    fun clearHistory() {
        historyTracksList.clear()
        tracksInteractor.clearHistory()
        historyLiveData.postValue(historyTracksList)
    }

    fun saveHistory() {
        tracksInteractor.saveHistory(historyTracksList)
    }

    private fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTrack(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(
                        foundTracks: List<Track>?,
                        errorMessage: String?
                    ) {

                        val tracks = mutableListOf<Track>()
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(
                                    TracksState.Error(
                                        errorMessage = R.string.internet_error
                                    )
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.Empty(
                                        message = R.string.search_not_found
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    TracksState.Content(
                                        tracks = tracks
                                    )
                                )
                            }
                        }
                    }
                })
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    fun searchDebounce(changedText: String) {
        this.searchText = changedText

        searchRunnable?.let { handler.removeCallbacks(it) }

        if (changedText.isBlank()) {
            renderState(TracksState.SearchHistory(historyTracksList))
            return
        }

        searchRunnable = Runnable { search(changedText) }

        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}