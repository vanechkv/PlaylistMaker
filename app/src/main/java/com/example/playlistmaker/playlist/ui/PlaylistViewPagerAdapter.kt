package com.example.playlistmaker.playlist.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.featured.ui.FeaturedFragment
import com.example.playlistmaker.playlists.ui.PlaylistsFragment

class PlaylistViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val emptyMessageFeatured: String,
    private val emptyMessagePlaylists: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FeaturedFragment.newInstance(emptyMessageFeatured)
            else -> PlaylistsFragment.newInstance(emptyMessagePlaylists)
        }
    }

}