package com.example.playlistmaker.search.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long,
    val title: String,
    val description: String?,
    val imagePath: String?,
    val trackIds: String,
    val trackCount: Int
)
