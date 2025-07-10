package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

class PlaylistsBottomSheetViewHolder(parent: ViewGroup, private val onPlaylistClick: (Playlist) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.bottom_playlist_view, parent, false)
    ) {
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val tracksCount: TextView = itemView.findViewById(R.id.tracks_count)
    private val artwork: ImageView = itemView.findViewById(R.id.artwork)

    private var currentPlaylist: Playlist? = null

    init {
        itemView.setOnClickListener {
            currentPlaylist?.let(onPlaylistClick)
        }
    }

    fun bind(model: Playlist) {
        currentPlaylist = model
        name.text = model.title
        tracksCount.text = getTracksCountText(model.trackCount)
        Glide.with(itemView)
            .load(model.imagePath)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(dpToPx(2)))
            .into(artwork)
    }

    private fun dpToPx(dp: Int): Int {
        val density = itemView.context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun getTracksCountText(count: Int): String {
        val word = when {
            count % 10 == 1 && count % 100 != 11 -> "трек"
            count % 10 in 2..4 && (count % 100 !in 12..14) -> "трека"
            else -> "треков"
        }
        return "$count $word"
    }
}