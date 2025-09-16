package com.example.playlistmaker.utils

import android.content.Context
import java.util.concurrent.TimeUnit

object DisplayUtils {
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
    fun formatTrackTime(trackTimeMillis: Long?): String {
        if (trackTimeMillis == null) return "00:00"
        val minutes = TimeUnit.MILLISECONDS.toMinutes(trackTimeMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(trackTimeMillis) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}