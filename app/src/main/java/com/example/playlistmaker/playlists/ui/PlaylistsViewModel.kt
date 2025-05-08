package com.example.playlistmaker.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlists.domain.models.PlaylistsState

class PlaylistsViewModel(private val emptyMessage: String) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState() : LiveData<PlaylistsState> = stateLiveData

    init {
        stateLiveData.postValue(PlaylistsState.Empty(emptyMessage))
    }
}