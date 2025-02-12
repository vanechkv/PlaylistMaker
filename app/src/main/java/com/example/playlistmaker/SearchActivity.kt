package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private var searchText: String? = null
    private val itunesBaseUrl = "https://itunes.apple.com"
    private lateinit var searchLayout: FrameLayout

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private var historyTracksList = ArrayList<Track>()

    private var adapter = TrackAdapter(tracks) {track -> onTrackClick(track)}
    private val adapterHistory = TrackAdapter(historyTracksList) {track -> onTrackClick(track)}

    private lateinit var app: App
    private lateinit var shredPref: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        app = applicationContext as App

        shredPref = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)

        searchLayout = findViewById(R.id.search_container)

        val historyView =
            LayoutInflater.from(this).inflate(R.layout.history_view, searchLayout, false)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val clearButton = findViewById<ImageView>(R.id.search_close_btn)
        inputEditText = findViewById(R.id.search_edit_text)

        clearButton.setOnClickListener {
            inputEditText.text = null

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            inputEditText.clearFocus()

            searchLayout.removeAllViews()

            tracks.clear()
            adapter.notifyDataSetChanged()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s?.toString()

                tracks.clear()
                adapter.notifyDataSetChanged()

                if (inputEditText.hasFocus() && s?.isEmpty() == true && historyTracksList.isNotEmpty()) {
                    adapterHistory.notifyDataSetChanged()
                    searchLayout.addView(historyView)
                } else {
                    searchLayout.removeAllViews()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }

        inputEditText.addTextChangedListener(textWatcher)

        val recyclerView = findViewById<RecyclerView>(R.id.search_recycler_view)
        recyclerView.adapter = adapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            inputEditText.clearFocus()
            if (actionId == EditorInfo.IME_ACTION_DONE && inputEditText.text.isNotEmpty()) {
                searchLayout.removeAllViews()
                search()
            }
            false
        }

        historyTracksList.addAll(createTracksListFromJson(shredPref.getString(
            HISTORY_TRACKS_LIST_KEY, null)))

        val historyRecycler =
            historyView.findViewById<RecyclerView>(R.id.history_recycler_view)
        historyRecycler.adapter = adapterHistory

        val clearHistoryButton = historyView.findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            historyTracksList.clear()
            shredPref.edit()
                .remove(HISTORY_TRACKS_LIST_KEY)
                .apply()
            searchLayout.removeAllViews()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && historyTracksList.isNotEmpty()) {
                listener =
                    SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                        if (key == NEW_TRACK_IN_HISTORY_KEY) {
                            val trackJson = sharedPreferences.getString(NEW_TRACK_IN_HISTORY_KEY, null)
                            val track = createTrackFromJson(trackJson)
                            if (!historyTracksList.contains(track)) {
                                historyTracksList.add(0, track)
                                adapterHistory.notifyItemInserted(0)
                            }
                        }
                    }

                shredPref.registerOnSharedPreferenceChangeListener(listener)

                searchLayout.addView(historyView)
            } else {
                searchLayout.removeAllViews()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStop() {
        super.onStop()

        shredPref.edit()
            .putString(HISTORY_TRACKS_LIST_KEY, createJsonFromTracksList(historyTracksList))
            .apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT)
        inputEditText.setText(searchText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showError(
        text: String,
        explanation: String?,
        iconError: Int,
        isButtonVisible: Boolean = false
    ) {
        if (text.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()

            searchLayout.removeAllViews()

            val errorView =
                LayoutInflater.from(this).inflate(R.layout.error_view, searchLayout, false)
            val errorText = errorView.findViewById<TextView>(R.id.error_text)
            val errorExplanation = errorView.findViewById<TextView>(R.id.error_explanation)
            val errorIcon = errorView.findViewById<ImageView>(R.id.error_icon)
            val errorButton = errorView.findViewById<Button>(R.id.error_button)

            errorText.text = text

            if (!explanation.isNullOrEmpty()) {
                errorExplanation.text = explanation
                errorExplanation.visibility = View.VISIBLE
            } else {
                errorExplanation.visibility = View.GONE
            }

            errorIcon.setImageResource(iconError)

            if (isButtonVisible) {
                errorButton.visibility = View.VISIBLE
                errorButton.setOnClickListener {
                    search()
                }
            } else {
                errorButton.visibility = View.GONE
            }

            searchLayout.addView(errorView)
        }
    }

    private fun search() {
        itunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.tracks?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.tracks!!)
                                adapter.notifyDataSetChanged()
                            } else {
                                showError(
                                    getString(R.string.search_not_found),
                                    null,
                                    R.drawable.vector_search_not_found,
                                    false
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showError(
                        getString(R.string.internet_error),
                        getString(R.string.internet_error_explanation),
                        R.drawable.vector_internet,
                        true
                    )
                }
            })
    }

    private fun createJsonFromTracksList(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }


    private fun createTrackFromJson(json: String?): Track {
        return Gson().fromJson(json, Track::class.java)
    }

    private fun createTracksListFromJson(json: String?): ArrayList<Track> {
        return if (json.isNullOrEmpty()) {
            ArrayList()
        } else {
            val tracks = Gson().fromJson(json, Array<Track>::class.java)
            ArrayList(tracks.toList())
        }
    }

    private fun onTrackClick (track: Track) {
        historyTracksList.removeIf {it.trackId == track.trackId}
        historyTracksList.add(0, track)
        if (historyTracksList.size > 10) {
            historyTracksList.removeAt(historyTracksList.lastIndex)
        }
        adapterHistory.notifyDataSetChanged()
        shredPref.edit()
            .putString(NEW_TRACK_IN_HISTORY_KEY, createJsonFromTrack(track))
            .apply()

        val trackIntent = Intent(this, AudioPlayerActivity::class.java)
        startActivity(trackIntent)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}