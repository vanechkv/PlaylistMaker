package com.example.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class TracksHistoryStorage(
    private val shredPref: SharedPreferences,
    private val gson: Gson
) {
    companion object {
        private const val HISTORY_TRACKS_LIST_KEY = "key_history_tracks_list"
        private const val NEW_TRACK_IN_HISTORY_KEY = "key_new_history_track_in_history"
    }

    fun saveTrack(track: Track, historyTracksList: ArrayList<Track>) {
        historyTracksList.removeIf { it.trackId == track.trackId }
        historyTracksList.add(0, track)
        if (historyTracksList.size > 10) {
            historyTracksList.removeAt(historyTracksList.lastIndex)
        }

        shredPref.edit()
            .putString(NEW_TRACK_IN_HISTORY_KEY, createJsonFromTrack(track))
            .apply()
        saveTracks(historyTracksList)
    }

    fun getTrack(): Track {
        val json = shredPref.getString(NEW_TRACK_IN_HISTORY_KEY, null)
        return createTrackFromJson(json)
    }

    fun getTracks(): List<Track> {
        val json = shredPref.getString(HISTORY_TRACKS_LIST_KEY, null)
        return createTracksListFromJson(json)
    }

    fun clear() {
        shredPref.edit()
            .remove(HISTORY_TRACKS_LIST_KEY)
            .apply()
    }

    fun saveTracks(tracks: List<Track>) {
        val json = createJsonFromTracksList(ArrayList(tracks))
        shredPref.edit()
            .putString(HISTORY_TRACKS_LIST_KEY, json)
            .apply()
    }

    private fun createJsonFromTracksList(tracks: ArrayList<Track>): String {
        return gson.toJson(tracks)
    }

    private fun createJsonFromTrack(track: Track): String {
        return gson.toJson(track)
    }


    private fun createTrackFromJson(json: String?): Track {
        return gson.fromJson(json, Track::class.java)
    }

    private fun createTracksListFromJson(json: String?): ArrayList<Track> {
        return if (json.isNullOrEmpty()) {
            ArrayList()
        } else {
            val tracks = gson.fromJson(json, Array<Track>::class.java)
            ArrayList(tracks.toList())
        }
    }
}