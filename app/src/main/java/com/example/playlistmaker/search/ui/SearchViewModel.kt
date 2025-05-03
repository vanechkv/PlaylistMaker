package com.example.playlistmaker.search.ui

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState

class SearchViewModel(application: Application, private val tracksInteractor: TracksInteractor) :
    AndroidViewModel(application) {

    private var searchText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val historyLiveData = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = historyLiveData

    private val historyTracksList = ArrayList<Track>()

    init {
        historyTracksList.addAll(tracksInteractor.getHistory())
        historyLiveData.postValue(historyTracksList)
    }

    fun onTrackClick(track: Track) {
        tracksInteractor.saveTrackToHistory(track, historyTracksList)
        historyLiveData.postValue(historyTracksList)
    }

    fun clearHistory() {
        historyTracksList.clear()
        tracksInteractor.clearHistory()
        historyLiveData.postValue(historyTracksList)
    }

    fun saveHistory() {
        tracksInteractor.saveHistory(historyTracksList)
    }

    private fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTrack(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(
                        foundTracks: List<Track>?,
                        errorMessage: String?
                    ) {

                        val tracks = mutableListOf<Track>()
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(
                                    TracksState.Error(
                                        errorMessage = getApplication<Application>().getString(R.string.internet_error)
                                    )
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.Empty(
                                        message = getApplication<Application>().getString(R.string.search_not_found)
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    TracksState.Content(
                                        tracks = tracks
                                    )
                                )
                            }
                        }
                    }
                })
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    fun searchDebounce(changedText: String) {
        this.searchText = changedText

        searchRunnable?.let { handler.removeCallbacks(it) }

        if (changedText.isBlank()) {
            renderState(TracksState.Empty(""))
            return
        }

        searchRunnable = Runnable { search(changedText) }

        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application,
                    Creator.provideTracksInteractor()
                )
            }
        }
    }
}