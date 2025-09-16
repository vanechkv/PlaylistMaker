package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.ui.theme.GraphiteBlack
import com.example.playlistmaker.main.ui.ui.theme.KleinBlue
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.compose.Header
import com.example.playlistmaker.utils.DisplayUtils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onItemClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val state by viewModel.observeState.collectAsState(TracksState.Initial)
    val history by viewModel.observeHistory.collectAsState(emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    var hasFocus by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var isClickAllowed by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Header(R.string.search)

        Spacer(modifier = Modifier.height(12.dp))

        SearchContainer(
            searchQuery = searchQuery,
            onSearchQueryChanged = {
                viewModel.setSearchQuery(it)
            },
            onSearchClear = {
                viewModel.clearQuery()
                focusManager.clearFocus()
            },
            onFocusChanged = { hasFocus = it },
            focusManager = focusManager
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (hasFocus && searchQuery.isBlank() && history.isNotEmpty()) {
            HistoryContainer(
                history = history,
                onItemClick = { track ->
                    if (isClickAllowed) {
                        isClickAllowed = false
                        viewModel.onTrackClick(track)
                        onItemClick()
                        scope.launch {
                            delay(1000L)
                            isClickAllowed = true
                        }
                    }
                },
                onClearHistory = {
                    viewModel.clearHistory()
                }
            )
        }

        when (val state = state) {
            is TracksState.Initial -> Unit

            is TracksState.Loading -> {
                ProgressBar()
            }

            is TracksState.Content -> {
                val tracks = state.tracks
                LazyColumn {
                    items(tracks) { track ->
                        TrackItem(track) { track ->
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

            is TracksState.Empty -> {
                val message = state.message
                val icon =
                    if (isDarkTheme) R.drawable.vector_search_not_found_dark else R.drawable.vector_search_not_found
                Message(
                    message = message,
                    icon = icon,
                    showRetry = false
                )
            }

            is TracksState.Error -> {
                val message = state.errorMessage
                val icon =
                    if (isDarkTheme) R.drawable.vector_internet_dark else R.drawable.vector_internet
                Message(
                    message = message,
                    explantation = R.string.internet_error_explanation,
                    icon = icon,
                    showRetry = true,
                    onRetry = { viewModel.searchDebounce(searchQuery) }
                )
            }

            is TracksState.SearchHistory -> Unit
        }
    }
}

@Composable
fun SearchContainer(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClear: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.vector_search),
                contentDescription = stringResource(id = R.string.search),
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.secondaryContainer
            )
            BasicTextField(
                value = searchQuery,
                onValueChange = {
                    onSearchQueryChanged(it)
                },
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    color = GraphiteBlack
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 9.dp)
                    .onFocusChanged { state -> onFocusChanged(state.isFocused) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.search),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                    innerTextField()
                }
            )
            if (searchQuery.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .size(12.dp)
                        .clickable {
                            onSearchClear()
                        }
                )
            }


        }
    }
}

@Composable
fun TrackItem(track: Track, onItemClick: (Track) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(track) }
            .padding(vertical = 8.dp)
            .padding(end = 20.dp).padding(start = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(track.artworkUrl100)
                .crossfade(true)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transformations(RoundedCornersTransformation(4f))
                .build(),
            contentDescription = track.trackName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp)
            ) {
                Text(
                    text = track.trackName ?: "",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp)
            ) {
                Row {
                    Text(
                        text = track.artistName ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dot),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = DisplayUtils.formatTrackTime(track.trackTimeMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1
                    )
                }
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.vector_forward),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun Message(
    message: Int,
    explantation: Int? = null,
    icon: Int,
    showRetry: Boolean = false,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 110.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = message),
            tint = Color.Unspecified
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = stringResource(id = message),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Center
        )
        if (explantation != null) {
            Text(
                modifier = Modifier
                    .padding(top = 22.27.dp),
                text = stringResource(id = explantation),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center
            )
        }
        if (showRetry) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.padding(top = 24.dp),
                shape = RoundedCornerShape(54.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.update),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun HistoryContainer(
    history: List<Track>,
    onItemClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 42.dp)
    ) {
        Text(
            text = stringResource(id = R.string.you_were_looking_for),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f, false)
        ) {
            items(history) { track ->
                TrackItem(track = track, onItemClick = onItemClick)
            }
        }

        Button(
            onClick = onClearHistory,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                text = stringResource(id = R.string.clear_history),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 148.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = KleinBlue
        )
    }
}

private val track = Track(
    trackId = 1,
    trackName = "Track Name",
    artistName = "Artist Name",
    trackTimeMillis = 180000,
    artworkUrl100 = null,
    collectionName = "Collection Name",
    releaseDate = "2023-01-01",
    primaryGenreName = "Genre",
    country = "Country",
    previewUrl = "url"
)

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun SearchContainerPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        SearchContainer(
            searchQuery = "",
            onSearchQueryChanged = {},
            onSearchClear = {},
            onFocusChanged = {},
            focusManager = LocalFocusManager.current
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun TrackItemPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        TrackItem(track = track, onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun MessagePreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        Message(
            message = R.string.internet_error,
            explantation = R.string.internet_error_explanation,
            icon = R.drawable.vector_internet,
            showRetry = true,
            onRetry = {}
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun HistoryContainerPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        val history = listOf(
            Track(
                trackId = 1,
                trackName = "Track Name 1",
                artistName = "Artist Name 1",
                trackTimeMillis = 180000,
                artworkUrl100 = null,
                collectionName = "Collection Name 1",
                releaseDate = "2023-01-01",
                primaryGenreName = "Genre 1",
                country = "Country 1",
                previewUrl = "url1"
            )
        )
        HistoryContainer(
            history = history,
            onItemClick = {},
            onClearHistory = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = false, showBackground = true)
@Composable
fun SearchScreenPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        val viewModel: SearchViewModel = koinViewModel()
        SearchScreen(
            viewModel = viewModel,
            onItemClick = {},
            isDarkTheme = false
        )
    }
}

