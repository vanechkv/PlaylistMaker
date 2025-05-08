package com.example.playlistmaker.search.domain.models

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: Int
    ) : TracksState

    data class Empty(
        val message: Int
    ) : TracksState

    data class SearchHistory(
        val history: List<Track>
    ) : TracksState
}