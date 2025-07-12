package com.example.playlistmaker.playlistInfo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlistInfo.domain.models.PlaylistState
import com.example.playlistmaker.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.interactor.SharingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val trackInteractor: TracksInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    private val isDeleted = MutableLiveData<Boolean>()
    fun observeIsDeleted(): LiveData<Boolean> = isDeleted

    init {
        renderState(PlaylistState.Loading)
    }

    fun loadData(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylistById(playlistId)
                .collect { playlist ->
                    trackInteractor.getTracksInPlaylist(playlist.trackIds.reversed())
                        .collect { tracks ->
                            withContext(Dispatchers.Main) {
                                processResult(playlist, tracks)
                            }
                        }
                }
        }
    }

    fun onTrackClick(track: Track) {
        trackInteractor.saveOnlyTrack(track)
    }

    fun deleteTrack(playlistId: Long, track: Track) {
        viewModelScope.launch {
            playlistInteractor
                .deleteTrackFromPlaylist(playlistId, track)
            loadData(playlistId)
        }
    }

    private fun processResult(playlist: Playlist, tracks: List<Track>) {
        renderState(PlaylistState.Content(playlist, tracks))
    }

    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        sharingInteractor.sharePlaylist(playlist, tracks)
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
            isDeleted.postValue(true)
        }
    }
}