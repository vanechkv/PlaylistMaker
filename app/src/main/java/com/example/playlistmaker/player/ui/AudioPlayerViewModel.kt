package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val tracksInteractor: TracksInteractor,
    private val audioPlayerInteractor: AudioPlayerInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val isFavorite = MutableLiveData<Boolean>()
    fun observeIsFavorite(): LiveData<Boolean> = isFavorite

    init {
        initMediaPlayer()

        viewModelScope.launch(Dispatchers.IO) {
            tracksInteractor.getFavoriteTracksId().collect { tracks ->
                val track = getTrack().trackId
                isFavorite.postValue(tracks.contains(track))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
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

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when(playerState.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> { }
        }
    }

    private fun initMediaPlayer() {
        audioPlayerInteractor.preparePlayer(getTrack().previewUrl,
            {
                playerState.postValue(PlayerState.Prepared())
            },
            {
                playerState.postValue(PlayerState.Prepared())
            })
    }

    fun getTrack() = tracksInteractor.getTrack()

    private fun startPlayer() {
        audioPlayerInteractor.startPlayer()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        audioPlayerInteractor.releasePlayer()
        playerState.postValue(PlayerState.Default())
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (audioPlayerInteractor.getPlayerState() == 2) {
                delay(300L)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }

            playerState.postValue(PlayerState.Prepared())
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(audioPlayerInteractor.getCurrentPosition()) ?: "00:00"
    }
}