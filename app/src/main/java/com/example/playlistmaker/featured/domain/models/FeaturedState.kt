package com.example.playlistmaker.featured.domain.models

sealed interface FeaturedState {

    data class Empty(
        val message: String
    ) : FeaturedState
}