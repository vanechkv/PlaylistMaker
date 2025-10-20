//package com.example.playlistmaker.player.ui.compose
//
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.ServiceConnection
//import android.os.IBinder
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.Text
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.constraintlayout.compose.ConstraintLayout
//import androidx.constraintlayout.compose.Dimension
//import coil.compose.AsyncImage
//import coil.request.ImageRequest
//import coil.transform.RoundedCornersTransformation
//import com.example.playlistmaker.R
//import com.example.playlistmaker.main.ui.ui.theme.AgateGray
//import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
//import com.example.playlistmaker.player.services.MusicService
//import com.example.playlistmaker.player.ui.AudioPlayerViewModel
//import com.example.playlistmaker.player.ui.PlaybackButtonView
//import com.example.playlistmaker.search.domain.models.Track
//import com.example.playlistmaker.utils.DisplayUtils
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import org.koin.androidx.compose.koinViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AudioPlayerScreen(
//    viewModel: AudioPlayerViewModel = koinViewModel(),
//    isDarkTheme: Boolean,
//) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    val track by remember { mutableStateOf(viewModel.getTrack()) }
//
//    val playerState by viewModel.observePlayerState.collectAsState()
//    val isFavorite by viewModel.observeIsFavorite.collectAsState()
//    val playlists by viewModel.observeState.collectAsState()
//    val addStatus by viewModel.observeAddStatus.collectAsState()
//
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true
//    )
//
//    var isClickAllowed by remember { mutableStateOf(true) }
//    fun clickDebounce(block: () -> Unit) {
//        if (!isClickAllowed) return
//        isClickAllowed = false
//        block()
//        scope.launch {
//            delay(1000L)
//            isClickAllowed = true
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(horizontal = 16.dp)
//    ) {
//        Cover(track)
//
//        Spacer(modifier = Modifier.size(46.dp))
//
//        TrackInfo(track)
//
//        Spacer(modifier = Modifier.size(38.dp))
//
//        PlaybackControl(
//            onPlayButtonClick = { viewModel.onPlayButtonClicked() },
//            onAddToPlaylistButtonClick = {  },
//            onAddToFavoritesButtonClick = { viewModel.onButtonFavoriteClick() },
//            isDarkTheme,
//            isFavorite
//        )
//
//        Spacer(modifier = Modifier.size(39.dp))
//
//        TrackExplanation(track)
//    }
//}
//
//@Composable
//fun Cover(track: Track) {
//    AsyncImage(
//        model = ImageRequest.Builder(LocalContext.current)
//            .data(track.artworkUrl100)
//            .crossfade(true)
//            .placeholder(R.drawable.placeholder)
//            .error(R.drawable.placeholder)
//            .transformations(RoundedCornersTransformation(4f))
//            .build(),
//        contentDescription = track.trackName,
//        contentScale = ContentScale.Crop,
//        modifier = Modifier
//            .padding(horizontal = 8.dp)
//            .fillMaxWidth()
//            .aspectRatio(1f)
//            .clip(RoundedCornerShape(8.dp))
//    )
//}
//
//@Composable
//fun TrackInfo(track: Track) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp),
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = track.trackName ?: "",
//            style = MaterialTheme.typography.titleLarge,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            color = MaterialTheme.colorScheme.onSecondary,
//            modifier = Modifier.fillMaxWidth()
//        )
//        Text(
//            text = track.artistName ?: "",
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            style = MaterialTheme.typography.titleSmall,
//            color = MaterialTheme.colorScheme.onSecondary,
//            modifier = Modifier
//                .padding(top = 12.dp)
//                .fillMaxWidth()
//        )
//    }
//}
//
//@Composable
//fun PlaybackControl(
//    onPlayButtonClick: () -> Unit,
//    onAddToPlaylistButtonClick: () -> Unit,
//    onAddToFavoritesButtonClick: () -> Unit,
//    isDarkTheme: Boolean,
//    isFavorite: Boolean
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(
//                onAddToPlaylistButtonClick,
//                modifier = Modifier.size(51.dp)
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.button_add_to_playlist),
//                    contentDescription = null,
//                    tint = Color.Unspecified,
//                )
//            }
//            IconButton(
//                onAddToFavoritesButtonClick,
//                modifier = Modifier.size(51.dp)
//            ) {
//                val icon =
//                    if (isFavorite) R.drawable.button_added_to_favorite else R.drawable.button_add_to_favorites
//                Icon(
//                    painter = painterResource(icon),
//                    contentDescription = null,
//                    tint = Color.Unspecified,
//                )
//            }
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 4.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "00:00",
//                style = MaterialTheme.typography.titleSmall,
//                color = MaterialTheme.colorScheme.onSecondary
//            )
//        }
//    }
//}
//
//@Composable
//fun TrackExplanation(track: Track) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//    ) {
//        TrackItemExplanations(
//            title = R.string.duration,
//            value = DisplayUtils.formatTrackTime(track.trackTimeMillis)
//        )
//        TrackItemExplanations(title = R.string.album, value = track.collectionName ?: "-")
//        TrackItemExplanations(title = R.string.year, value = track.releaseDate ?: "-")
//        TrackItemExplanations(title = R.string.genre, value = track.primaryGenreName ?: "-")
//        TrackItemExplanations(title = R.string.country, value = track.country ?: "-")
//    }
//}
//
//@Composable
//fun TrackItemExplanations(title: Int, value: String) {
//    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
//
//        val (titleRef, valueRef) = createRefs()
//
//        Text(
//            text = stringResource(title),
//            style = MaterialTheme.typography.labelMedium,
//            color = AgateGray,
//            modifier = Modifier.constrainAs(titleRef) {
//                start.linkTo(parent.start)
//            }
//        )
//
//        Text(
//            text = value,
//            style = MaterialTheme.typography.labelMedium,
//            color = MaterialTheme.colorScheme.onSecondary,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            textAlign = TextAlign.End,
//            modifier = Modifier.constrainAs(valueRef) {
//                start.linkTo(titleRef.end, margin = 16.dp)
//                bottom.linkTo(parent.bottom, margin = 17.dp)
//                end.linkTo(parent.end)
//                width = Dimension.fillToConstraints
//            }
//        )
//    }
//}
//
//private val track = Track(
//    trackId = 0,
//    trackName = "Sample Track",
//    artistName = "Sample Artist",
//    trackTimeMillis = 180000L,
//    artworkUrl100 = "https://example.com/artwork.jpg",
//    collectionName = "Sample Album",
//    releaseDate = "2023-01-01",
//    primaryGenreName = "Sample Genre",
//    country = "Sample Country",
//    previewUrl = "https://example.com/preview.mp3",
//    isFavorite = false
//)
//
//@Preview(showSystemUi = false, showBackground = true)
//@Composable
//fun CoverPreview() {
//    PlaylistMakerTheme(dynamicColor = false) {
//        Cover(track = track)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TrackInfoPreview() {
//    PlaylistMakerTheme(dynamicColor = false) {
//        TrackInfo(track)
//    }
//}
//
//@Preview(showSystemUi = false, showBackground = true)
//@Composable
//fun PlaybackControlPreview() {
//    PlaylistMakerTheme(dynamicColor = false) {
//        PlaybackControl(
//            onPlayButtonClick = {},
//            onAddToPlaylistButtonClick = {},
//            onAddToFavoritesButtonClick = {},
//            false,
//            true
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TrackExplanationPreview() {
//    PlaylistMakerTheme(dynamicColor = false) {
//        TrackExplanation(track)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TrackItemExplanationsPreview() {
//    PlaylistMakerTheme(dynamicColor = false) {
//        TrackItemExplanations(title = R.string.duration, value = "3:35")
//    }
//}
//
//@Preview(showSystemUi = false, showBackground = true)
//@Composable
//fun AudioPlayerScreenPreview() {
//    PlaylistMakerTheme(dynamicColor = false) {
//        AudioPlayerScreen(
//            isDarkTheme = false
//        )
//    }
//}