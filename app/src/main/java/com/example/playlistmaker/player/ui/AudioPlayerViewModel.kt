package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val tracksInteractor: TracksInteractor,
    private val audioPlayerInteractor: AudioPlayerInteractor
) : ViewModel() {

    private val isPlayingLiveData = MutableLiveData<Boolean>()
    fun observeIsPlaying(): LiveData<Boolean> = isPlayingLiveData

    private val currentPositionLiveData = MutableLiveData<String>()
    fun observeCurrentPosition(): LiveData<String> = currentPositionLiveData


    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { createUpdateTimer() }

    private fun createUpdateTimer() {
        when (audioPlayerInteractor.getPlayerState()) {
            STATE_PLAYING -> {
                currentPositionLiveData.postValue(
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(audioPlayerInteractor.getCurrentPosition())
                )
                handler.postDelayed(runnable, DELAY_UPDATE_TRACK_TIME)
            }

            STATE_PREPARED -> {
                currentPositionLiveData.postValue("00:00")
            }
        }
    }

    fun getTrack() = tracksInteractor.getTrack()

    fun preparePlayer() {
        audioPlayerInteractor.preparePlayer(getTrack().previewUrl,
            {
                isPlayingLiveData.postValue(false)
            },
            {
                isPlayingLiveData.postValue(false)
                currentPositionLiveData.postValue("00:00")
            })

    }

    fun startPlayer() {
        audioPlayerInteractor.startPlayer()
        isPlayingLiveData.postValue(true)
        startTimer()
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        isPlayingLiveData.postValue(false)
        stopTimer()
    }

    fun playbackControl() {
        when (audioPlayerInteractor.getPlayerState()) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    fun releasePlayer() {
        handler.removeCallbacks(runnable)
        audioPlayerInteractor.releasePlayer()
    }

    private fun startTimer() {
        handler.post(runnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable)
    }

    companion object {
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_UPDATE_TRACK_TIME = 300L
    }
}