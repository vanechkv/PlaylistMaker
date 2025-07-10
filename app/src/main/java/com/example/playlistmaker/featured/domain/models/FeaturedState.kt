package com.example.playlistmaker.featured.domain.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface FeaturedState {

    object Loading : FeaturedState

    data class Content(
        val tracks: List<Track>
    ): FeaturedState

    data class Empty(
        val message: String
    ) : FeaturedState
}