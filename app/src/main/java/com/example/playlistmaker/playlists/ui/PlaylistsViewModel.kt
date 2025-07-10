package com.example.playlistmaker.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.models.PlaylistsState
import com.example.playlistmaker.search.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val emptyMessage: String,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState() : LiveData<PlaylistsState> = stateLiveData

    init {
        renderState(PlaylistsState.Loading)
        viewModelScope.launch (Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty(emptyMessage))
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }
}