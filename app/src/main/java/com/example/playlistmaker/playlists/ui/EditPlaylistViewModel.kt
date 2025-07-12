package com.example.playlistmaker.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.search.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val isUpdated = MutableLiveData<Boolean>()
    fun observeIsUpdated(): LiveData<Boolean> = isUpdated

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
            isUpdated.postValue(true)
        }
    }
}