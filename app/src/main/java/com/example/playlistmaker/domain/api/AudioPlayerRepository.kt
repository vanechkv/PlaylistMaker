package com.example.playlistmaker.domain.api

interface AudioPlayerRepository {

    fun preparePlayer(urlTrack: String, prepared: () -> Unit, completion: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getCurrentPosition(): Int

    fun getPlayerState(): Int
}