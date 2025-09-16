package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {
    private var searchText: String? = null
    private val searchQueryStateFlow = MutableStateFlow("")
    val searchQuery: StateFlow<String> = searchQueryStateFlow

    private val stateLiveData =
        MutableStateFlow<TracksState>(TracksState.Initial)
    val observeState: StateFlow<TracksState> = stateLiveData

    private val historyLiveData = MutableStateFlow<List<Track>>(emptyList())
    val observeHistory: StateFlow<List<Track>> = historyLiveData

    private val historyTracksList = ArrayList<Track>()

    private var searchJob: Job? = null

    init {
        historyTracksList.addAll(tracksInteractor.getHistory())
        historyLiveData.value = historyTracksList.toList()
    }

    fun onTrackClick(track: Track) {
        tracksInteractor.saveTrackToHistory(track, historyTracksList)
        historyLiveData.value = historyTracksList.toList()
    }

    fun clearHistory() {
        historyTracksList.clear()
        tracksInteractor.clearHistory()
        historyLiveData.value = historyTracksList.toList()
        renderState(TracksState.SearchHistory(historyTracksList.toList()))
    }

    fun saveHistory() {
        tracksInteractor.saveHistory(historyTracksList)
    }

    private fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            val query = newSearchText

            viewModelScope.launch(Dispatchers.IO) {
                tracksInteractor
                    .searchTrack(query)
                    .collect { pair ->
                        if (query == searchText) {
                            processResult(pair.first, pair.second)
                        }
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
        stateLiveData.value = state
    }

    fun searchDebounce(changedText: String) {
        if (searchText == changedText) {
            return
        }

        if (changedText.isBlank()) {
            searchText = null
            searchJob?.cancel()
            renderState(TracksState.SearchHistory(historyTracksList.toList()))
            return
        }

        searchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(changedText)
        }
    }

    fun setSearchQuery(newQuery: String) {
        if (searchQueryStateFlow.value == newQuery) return
        searchQueryStateFlow.value = newQuery
        searchDebounce(newQuery)
    }

    fun clearQuery() {
        searchQueryStateFlow.value = ""
        searchText = null
        searchJob?.cancel()
        renderState(TracksState.SearchHistory(historyTracksList.toList()))
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}