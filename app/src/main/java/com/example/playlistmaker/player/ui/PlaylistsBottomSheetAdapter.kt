package com.example.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

class PlaylistsBottomSheetAdapter(
    private val playlists: ArrayList<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsBottomSheetViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsBottomSheetViewHolder {
        return PlaylistsBottomSheetViewHolder(parent, onPlaylistClick)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsBottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }
}