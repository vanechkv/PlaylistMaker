package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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

        sdf = SimpleDateFormat("mm:ss", Locale.getDefault())

        val shredPref = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)

        val jsonTrack = shredPref.getString(NEW_TRACK_IN_HISTORY_KEY, null)
        val track = createTrackFromJson(jsonTrack)

        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = sdf.format(track.trackTimeMillis)
        album.text = track.collectionName ?: "-"
        year.text = track.getReleaseYear() ?: "-"
        genre.text = track.primaryGenreName ?: "-"
        country.text = track.country ?: "-"
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(artworkUrl100)

        val backButton = findViewById<ImageView>(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }

        val playButton = findViewById<ImageView>(R.id.button_play)
        playButton.setOnClickListener {
            playButton.setImageResource(R.drawable.vector_pause)
        }

        val addFavoriteButton = findViewById<ImageView>(R.id.button_add_to_favorite)
        addFavoriteButton.setOnClickListener {
            addFavoriteButton.setImageResource(R.drawable.button_added_to_favorite)
        }
    }

    private fun createTrackFromJson(json: String?): Track {
        return Gson().fromJson(json, Track::class.java)
    }
}