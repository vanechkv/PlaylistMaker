package com.example.playlistmaker.playlist.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlaylistBinding
import com.google.android.material.tabs.TabLayoutMediator

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val emptyMessageFeatured = getString(R.string.your_media_library_is_empty)
        val emptyMessagePlaylists = getString(R.string.You_havent_created_any_playlists_yet)

        binding.viewPager.adapter = PlaylistViewPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle,
            emptyMessageFeatured = emptyMessageFeatured,
            emptyMessagePlaylists = emptyMessagePlaylists
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.featured_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        binding.backButton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}