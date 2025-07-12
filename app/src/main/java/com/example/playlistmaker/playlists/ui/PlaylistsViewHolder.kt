package com.example.playlistmaker.playlists.ui

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
import com.example.playlistmaker.utils.DisplayUtils

class PlaylistsViewHolder(
    parent: ViewGroup,
    private val onPlaylistClick: (Playlist) -> Unit
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.main_platlist_view, parent, false)
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
        tracksCount.text = itemView.resources.getQuantityString(
            R.plurals.numberOfTracksAvailable,
            model.trackCount,
            model.trackCount
        )
        Glide.with(itemView)
            .load(model.imagePath)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(DisplayUtils.dpToPx(itemView.context, 8)))
            .into(artwork)
    }
}