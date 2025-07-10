package com.example.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.search.data.db.dao.PlaylistDao
import com.example.playlistmaker.search.data.db.dao.TrackDao
import com.example.playlistmaker.search.data.db.entity.PlaylistEntity
import com.example.playlistmaker.search.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.data.db.entity.TrackEntity

@Database(
    version = 1,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao
}