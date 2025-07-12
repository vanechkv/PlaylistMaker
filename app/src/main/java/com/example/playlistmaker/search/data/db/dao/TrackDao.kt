package com.example.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.search.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deletePlaylistTrack(trackId: Int)

    @Query("SELECT * FROM track_table ORDER BY addedTime DESC")
    fun getAllTrack(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    suspend fun getAllTrackId(): List<Int>

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds)")
    fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<PlaylistTrackEntity>>
}