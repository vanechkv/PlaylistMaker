package com.example.playlistmaker.featured.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.featured.domain.models.FeaturedState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeaturedViewModel(
    private val emptyMessage: String,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val stateLiveData = MutableStateFlow<FeaturedState>(FeaturedState.Loading)
    val observeState: StateFlow<FeaturedState> = stateLiveData

    init {
        renderState(FeaturedState.Loading)
        viewModelScope.launch (Dispatchers.IO) {
            tracksInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    fun onTrackClick(track: Track) {
        tracksInteractor.saveOnlyTrack(track)
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FeaturedState.Empty(emptyMessage))
        } else {
            renderState(FeaturedState.Content(tracks))
        }
    }

    private fun renderState(state: FeaturedState) {
        stateLiveData.value = state
    }
}