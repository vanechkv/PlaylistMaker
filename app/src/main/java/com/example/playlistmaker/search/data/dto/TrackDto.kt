package com.example.playlistmaker.search.data.dto

import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: Int,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) {

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

    fun getReleaseYear(): String? {
        return releaseDate?.let {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val sdf = SimpleDateFormat("yyyy", Locale.getDefault())
                val date = parser.parse(it)
                sdf.format(date!!)
            } catch (e: Exception) {
                null
            }
        }
    }
}