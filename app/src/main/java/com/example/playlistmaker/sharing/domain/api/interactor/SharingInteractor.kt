package com.example.playlistmaker.sharing.domain.api.interactor

import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface SharingInteractor {

    fun shareApp()

    fun openTerms()

    fun openSupport()

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>)
}