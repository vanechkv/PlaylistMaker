package com.example.playlistmaker.search.domain.models

import java.io.Serializable

data class Playlist(
    val playlistId: Long,
    val title: String,
    val description: String?,
    val imagePath: String?,
    val trackIds: List<Int>,
    val trackCount: Int
) : Serializable
