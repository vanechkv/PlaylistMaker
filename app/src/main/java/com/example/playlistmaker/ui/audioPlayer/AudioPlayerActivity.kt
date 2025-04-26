package com.example.playlistmaker.ui.audioPlayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constans.DELAY_UPDATE_TRACK_TIME
import com.example.playlistmaker.Constans.STATE_DEFAULT
import com.example.playlistmaker.Constans.STATE_PAUSED
import com.example.playlistmaker.Constans.STATE_PLAYING
import com.example.playlistmaker.Constans.STATE_PREPARED
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var artworkUrl100: ImageView
    private lateinit var sdf: SimpleDateFormat
    private lateinit var urlTrack: String
    private lateinit var playButton: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { createUpdateTimer() }
    private lateinit var timer: TextView

    private val trackInteractor by lazy { Creator.provideTracksInteractor(this) }
    private val audioPlayerInteractor by lazy { Creator.provideAudioPlayerInteractor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audioplayer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        duration = findViewById(R.id.track_time)
        album = findViewById(R.id.album_name)
        year = findViewById(R.id.track_year)
        genre = findViewById(R.id.track_genre)
        country = findViewById(R.id.track_country)
        artworkUrl100 = findViewById(R.id.cover)
        playButton = findViewById(R.id.button_play)
        timer = findViewById(R.id.listening_time)

        sdf = SimpleDateFormat("mm:ss", Locale.getDefault())

        val track = trackInteractor.getTrack()

        urlTrack = track.previewUrl

        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = sdf.format(track.trackTimeMillis)
        album.text = track.collectionName ?: "-"
        year.text = track.releaseDate ?: "-"
        genre.text = track.primaryGenreName ?: "-"
        country.text = track.country ?: "-"
        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(artworkUrl100)

        val backButton = findViewById<ImageView>(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

        val addFavoriteButton = findViewById<ImageView>(R.id.button_add_to_favorite)
        addFavoriteButton.setOnClickListener {
            addFavoriteButton.setImageResource(R.drawable.button_added_to_favorite)
        }

    }

    override fun onPause() {
        super.onPause()
        audioPlayerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractor.releasePlayer()
    }

    private fun preparePlayer() {
        audioPlayerInteractor.preparePlayer(urlTrack,
            {
                playButton.isEnabled = true
            },
            {
                playButton.setImageResource(R.drawable.vector_play)
            })

    }

    private fun startPlayer() {
        audioPlayerInteractor.startPlayer()
        playButton.setImageResource(R.drawable.vector_pause)
        startTimer()
    }

    private fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        playButton.setImageResource(R.drawable.vector_play)
        stopTimer()
    }

    private fun playbackControl() {
        when (audioPlayerInteractor.getPlayerState()) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startTimer() {
        handler.post(runnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable)
    }

    private fun createUpdateTimer() {
        when (audioPlayerInteractor.getPlayerState()) {
            STATE_PLAYING -> {
                timer.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(audioPlayerInteractor.getCurrentPosition())
                handler.postDelayed(runnable, DELAY_UPDATE_TRACK_TIME)
            }

            STATE_PREPARED -> {
                timer.setText(R.string.time_00_00)
            }
        }
    }
}