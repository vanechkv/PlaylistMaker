package com.example.playlistmaker.search.data.converters

import com.example.playlistmaker.search.data.db.entity.PlaylistEntity
import com.example.playlistmaker.search.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(
    private val gson: Gson
) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.title,
            playlist.description,
            playlist.imagePath,
            fromListToJson(playlist.trackIds),
            playlist.trackCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.title,
            playlist.description,
            playlist.imagePath,
            fromJsonToList(playlist.trackIds),
            playlist.trackCount
        )
    }

    private fun fromListToJson(list: List<Int>): String {
        return gson.toJson(list)
    }

    fun fromJsonToList(json: String): List<Int> {
        return gson.fromJson(json, object : TypeToken<List<Int>>() {})
    }
}