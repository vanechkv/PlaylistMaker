package com.example.playlistmaker.playlist.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.featured.ui.compose.FeaturedScreen
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.playlists.ui.compose.PlaylistsScreen
import com.example.playlistmaker.settings.ui.compose.Header
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    onFeaturedTrackClick: () -> Unit,
    onAddPlaylistClick: () -> Unit,
    onPlaylistItemClick: (Long) -> Unit,
    isDarkTheme: Boolean
) {
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(R.string.playlist)

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(pagerState, scope, selectedTabIndex = pagerState.currentPage)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> FeaturedScreen(
                    "",
                    onItemClick = onFeaturedTrackClick,
                    isDarkTheme = isDarkTheme
                )

                1 -> PlaylistsScreen(
                    "",
                    onAddPlaylistClick = onAddPlaylistClick,
                    onItemClick = onPlaylistItemClick,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun TabRow(pagerState: PagerState, scope: CoroutineScope, selectedTabIndex: Int) {
    val tabs = listOf(
        stringResource(id = R.string.featured_tracks),
        stringResource(id = R.string.playlists)
    )
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.primary,
        divider = {},
        contentColor = MaterialTheme.colorScheme.onPrimary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun TabRowPreview() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val selectedTabIndex = 0
    PlaylistMakerTheme(dynamicColor = false) {
        TabRow(
            pagerState = pagerState, scope = scope, selectedTabIndex = selectedTabIndex
        )
    }
}