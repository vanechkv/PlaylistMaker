package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel: AudioPlayerViewModel by viewModel()

    private lateinit var sdf: SimpleDateFormat

    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sdf = SimpleDateFormat("mm:ss", Locale.getDefault())

        val track = viewModel.getTrack()

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = sdf.format(track.trackTimeMillis)
        binding.albumName.text = track.collectionName ?: "-"
        binding.trackYear.text = track.releaseDate ?: "-"
        binding.trackGenre.text = track.primaryGenreName ?: "-"
        binding.trackCountry.text = track.country ?: "-"
        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(binding.cover)

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel.observePlayerState().observe(this) {
            binding.buttonPlay.isEnabled = it.isPlayButtonEnabled
            binding.buttonPlay.setImageResource(it.buttonImg)
            binding.listeningTime.text = it.progress
        }

        viewModel.observeIsFavorite().observe(this) { isFavorite ->
            val icon = if (isFavorite) R.drawable.button_added_to_favorite else R.drawable.button_add_to_favorites
            binding.buttonAddToFavorite.setImageResource(icon)
        }

        binding.buttonAddToFavorite.setOnClickListener {
            viewModel.onButtonFavoriteClick()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}