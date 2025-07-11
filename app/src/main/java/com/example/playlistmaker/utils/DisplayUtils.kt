package com.example.playlistmaker.utils

import android.content.Context

object DisplayUtils {
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}