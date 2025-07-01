package com.example.playlistmaker.player.domain.model

import com.example.playlistmaker.R

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val buttonImg: Int, val progress: String) {

    class Default : PlayerState(false, R.drawable.vector_play, "00:00")

    class Prepared : PlayerState(true, R.drawable.vector_play, "00:00")

    class Playing(progress: String) : PlayerState(true, R.drawable.vector_pause, progress)

    class Paused(progress: String) : PlayerState(true, R.drawable.vector_play, progress)
}