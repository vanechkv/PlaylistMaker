package com.example.playlistmaker.playlists.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.playlists.ui.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Playlist
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.playlists.domain.models.PlaylistsState
import com.example.playlistmaker.search.ui.compose.Message
import com.example.playlistmaker.search.ui.compose.ProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlaylistsScreen(
    emptyMessage: String,
    viewModel: PlaylistsViewModel = koinViewModel(parameters = { parametersOf(emptyMessage) }),
    onAddPlaylistClick: () -> Unit,
    onItemClick: (PlaylistId: Long) -> Unit,
    isDarkTheme: Boolean
) {
    val state by viewModel.observeState.collectAsState(initial = PlaylistsState.Loading)
    var isClickAllowed by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAddPlaylistClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                text = stringResource(id = R.string.new_playlist),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (val state = state) {
            is PlaylistsState.Loading -> {
                ProgressBar()
            }

            is PlaylistsState.Empty -> {
                val icon =
                    if (isDarkTheme) R.drawable.vector_search_not_found_dark else R.drawable.vector_search_not_found
                Message(
                    message = R.string.You_havent_created_any_playlists_yet,
                    icon = icon,
                    showRetry = false
                )
            }

            is PlaylistsState.Content -> {
                val playlists = state.playlists
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistItem(playlist) {
                            if (isClickAllowed) {
                                isClickAllowed = false
                                onItemClick(playlist.playlistId)
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

@Composable
fun PlaylistItem(playlist: Playlist, onItemClick: (Playlist) -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onItemClick(playlist) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playlist.imagePath)
                    .crossfade(true)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .transformations(RoundedCornersTransformation(4f))
                    .build(),
                contentDescription = playlist.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextPlaylistItem(playlist.title)
        TextPlaylistItem(
            LocalResources.current.getQuantityString(
                R.plurals.numberOfTracksAvailable,
                playlist.trackCount,
                playlist.trackCount
            )
        )
    }
}

@Composable
fun TextPlaylistItem(text: String) {
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(
    showSystemUi = false, showBackground = true,
    device = "id:pixel_8"
)
@Composable
fun PlaylistItemPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        val playlist = Playlist(
            playlistId = 1,
            title = "My Favorite Songs",
            description = "A collection of my favorite songs",
            imagePath = null,
            trackIds = emptyList(),
            trackCount = 0
        )
        PlaylistItem(playlist = playlist, onItemClick = {})
    }
}