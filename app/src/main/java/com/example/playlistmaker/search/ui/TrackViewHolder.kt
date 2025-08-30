package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.DisplayUtils
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup, private val onTrackClick: (Track) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
    ) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.artwork)
    private val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())

    private var currentTrack: Track? = null

    init {
        itemView.setOnClickListener {
            currentTrack?.let(onTrackClick)
        }
    }

    fun bind(model: Track) {
        currentTrack = model
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = sdf.format(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(DisplayUtils.dpToPx(itemView.context, 2)))
            .into(artworkUrl100)
    }
}