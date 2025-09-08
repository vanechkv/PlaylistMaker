package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.services.AudioPlayerControl
import com.example.playlistmaker.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.models.PlaylistsState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val tracksInteractor: TracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    private val addStatus = MutableLiveData<Pair<String, Boolean>>()
    fun observeAddStatus(): LiveData<Pair<String, Boolean>> = addStatus

    private val isFavorite = MutableLiveData<Boolean>()
    fun observeIsFavorite(): LiveData<Boolean> = isFavorite

    private var audioPlayerControl: AudioPlayerControl? = null

    init {

        viewModelScope.launch {
            tracksInteractor.getFavoriteTracksId().collect { tracks ->
                val track = getTrack().trackId
                isFavorite.postValue(tracks.contains(track))
            }
        }

        renderState(PlaylistsState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl
        viewModelScope.launch {
            audioPlayerControl.getPlayerState().collect {
                playerState.postValue(it)
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty(""))
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }

    fun onButtonFavoriteClick() {
        viewModelScope.launch {
            val track = getTrack()
            if (isFavorite.value == true) {
                tracksInteractor.deleteTrackFromFavorites(track)
                track.isFavorite = false
            } else {
                tracksInteractor.addTrackToFavorites(track)
                track.isFavorite = true
            }
            isFavorite.postValue(track.isFavorite)
        }
    }

    fun onPlaylistClicked(track: Track, playlist: Playlist) {
        if (track.trackId in playlist.trackIds) {
            addStatus.postValue(Pair(playlist.title, false))
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track, playlist)
                addStatus.postValue(Pair(playlist.title, true))
            }
        }
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> audioPlayerControl?.pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> audioPlayerControl?.startPlayer()
            else -> Unit
        }
    }

    fun showNotification() {
        if (playerState.value is PlayerState.Playing) {
            audioPlayerControl?.showNotification()
        }
    }

    fun hideNotification() {
        audioPlayerControl?.hideNotification()
    }

    fun getTrack() = tracksInteractor.getTrack()
}