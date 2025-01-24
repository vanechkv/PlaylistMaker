package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_PREFERENCES = "playlist_preferences"
const val DARK_MODE_KEY = "key_for_dark_mode"
const val HISTORY_TRACKS_LIST_KEY = "key_history_tracks_list"
const val NEW_TRACK_IN_HISTORY_KEY = "key_new_history_track_in_history"

class App : Application() {

    var darkTheme = false
    private lateinit var shredPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        shredPref = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        darkTheme = shredPref.getBoolean(DARK_MODE_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else AppCompatDelegate.MODE_NIGHT_NO
        )
        shredPref.edit()
            .putBoolean(DARK_MODE_KEY, darkThemeEnabled)
            .apply()
    }
}