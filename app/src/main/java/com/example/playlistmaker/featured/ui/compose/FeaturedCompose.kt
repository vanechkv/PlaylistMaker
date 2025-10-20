package com.example.playlistmaker.featured.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.playlistmaker.R
import com.example.playlistmaker.featured.domain.models.FeaturedState
import com.example.playlistmaker.featured.ui.FeaturedViewModel
import com.example.playlistmaker.search.ui.compose.Message
import com.example.playlistmaker.search.ui.compose.ProgressBar
import com.example.playlistmaker.search.ui.compose.TrackItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FeaturedScreen(
    emptyMessage: String,
    viewModel: FeaturedViewModel = koinViewModel(parameters = { parametersOf(emptyMessage) }),
    onItemClick: () -> Unit,
    isDarkTheme: Boolean
) {
    var isClickAllowed by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val state by viewModel.observeState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = state) {
            is FeaturedState.Loading -> {
                ProgressBar()
            }

            is FeaturedState.Empty -> {
                val icon =
                    if (isDarkTheme) R.drawable.vector_search_not_found_dark else R.drawable.vector_search_not_found
                Message(
                    message = R.string.your_media_library_is_empty,
                    icon = icon,
                    showRetry = false
                )
            }

            is FeaturedState.Content -> {
                val tracks = state.tracks
                LazyColumn {
                    items(tracks) { track ->
                        TrackItem(track = track) {
                            if (isClickAllowed) {
                                isClickAllowed = false
                                onItemClick()
                                viewModel.onTrackClick(track)
                                scope.launch {
                                    delay(1000L)
                                    isClickAllowed = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}