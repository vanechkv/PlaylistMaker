package com.example.playlistmaker.player.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants.DELAY_UPDATE_TRACK_TIME
import com.example.playlistmaker.constants.Constants.STATE_PAUSED
import com.example.playlistmaker.constants.Constants.STATE_PLAYING
import com.example.playlistmaker.constants.Constants.STATE_PREPARED
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor,
    private val audioPlayerInteractor: AudioPlayerInteractor
) : AndroidViewModel(application) {

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
                currentPositionLiveData.postValue(getApplication<Application>().getString(R.string.time_00_00))
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
                currentPositionLiveData.postValue(getApplication<Application>().getString(R.string.time_00_00))
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
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(
                    application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application,
                    Creator.provideTracksInteractor(),
                    Creator.provideAudioPlayerInteractor()
                )
            }
        }
    }
}