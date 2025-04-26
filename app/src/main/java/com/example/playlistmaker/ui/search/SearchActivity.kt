package com.example.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Constans.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.Constans.SEARCH_TEXT
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity
import com.example.playlistmaker.ui.main.MainActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private var searchText: String? = null
    private lateinit var searchLayout: FrameLayout
    private lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()
    private var historyTracksList = ArrayList<Track>()

    private var adapter = TrackAdapter(tracks) { track -> onTrackClick(track, historyTracksList) }
    private val adapterHistory =
        TrackAdapter(historyTracksList) { track -> onTrackClick(track, historyTracksList) }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private val trackInteractor by lazy { Creator.provideTracksInteractor(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        searchLayout = findViewById(R.id.search_container)

        progressBar = findViewById(R.id.progress_bar)

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
                searchDebounce()
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

        historyTracksList.addAll(
            trackInteractor.getHistory()
        )

        val historyRecycler =
            historyView.findViewById<RecyclerView>(R.id.history_recycler_view)
        historyRecycler.adapter = adapterHistory

        val clearHistoryButton = historyView.findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            historyTracksList.clear()
            trackInteractor.clearHistory()
            searchLayout.removeAllViews()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && historyTracksList.isNotEmpty()) {

                historyTracksList.clear()
                historyTracksList.addAll(trackInteractor.getHistory())

                adapterHistory.notifyDataSetChanged()
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

        trackInteractor.saveHistory(historyTracksList)
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
                    searchLayout.removeAllViews()
                    search()
                }
            } else {
                errorButton.visibility = View.GONE
            }

            searchLayout.addView(errorView)
        }
    }

    private fun search() {
        if (inputEditText.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE

            trackInteractor.searchTrack(
                inputEditText.text.toString().trim(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(
                        foundTracks: List<Track>?
                    ) {
                        handler.post {
                            progressBar.visibility = View.GONE

                            if (foundTracks != null) {
                                if (foundTracks.isNotEmpty()) {
                                    tracks.clear()
                                    tracks.addAll(foundTracks)
                                    adapter.notifyDataSetChanged()
                                } else {
                                    showError(
                                        getString(R.string.search_not_found),
                                        null,
                                        R.drawable.vector_search_not_found,
                                        false
                                    )
                                }
                            } else {
                                showError(
                                    getString(R.string.internet_error),
                                    getString(R.string.internet_error_explanation),
                                    R.drawable.vector_internet,
                                    true
                                )
                            }
                        }
                    }
                })
        }
    }

    private fun onTrackClick(track: Track, historyTracksList: ArrayList<Track>) {
        trackInteractor.saveTrackToHistory(track, historyTracksList)

        adapterHistory.notifyDataSetChanged()

        val trackIntent = Intent(this, AudioPlayerActivity::class.java)
        startActivity(trackIntent)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}