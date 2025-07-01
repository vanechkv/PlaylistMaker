package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {
    private var searchText: String? = null

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val historyLiveData = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = historyLiveData

    private val historyTracksList = ArrayList<Track>()

    private var searchJob: Job? = null

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

            viewModelScope.launch {
                tracksInteractor
                    .searchTrack(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
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

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    fun searchDebounce(changedText: String) {
        if(searchText == changedText) {
            return
        }

        if (changedText.isBlank()) {
            renderState(TracksState.SearchHistory(historyTracksList))
            return
        }

        searchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(changedText)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}