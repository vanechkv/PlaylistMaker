package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.Constans.STATE_DEFAULT
import com.example.playlistmaker.Constans.STATE_PAUSED
import com.example.playlistmaker.Constans.STATE_PLAYING
import com.example.playlistmaker.Constans.STATE_PREPARED
import com.example.playlistmaker.domain.api.AudioPlayerRepository

class AudioPlayerRepositoryImpl : AudioPlayerRepository {

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    override fun preparePlayer(urlTrack: String, prepared: () -> Unit, completion: () -> Unit) {
        mediaPlayer.setDataSource(urlTrack)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            prepared()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            completion()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getPlayerState(): Int {
        return playerState
    }
}