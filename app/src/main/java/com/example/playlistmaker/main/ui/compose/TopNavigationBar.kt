package com.example.playlistmaker.main.ui.compose

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    title: Int?,
    navigationBack: () -> Unit,
    isNavigationBackVisible: Boolean = false
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            if (title != null)
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (isNavigationBackVisible) {
                IconButton(onClick = navigationBack) {
                    Icon(
                        painter = painterResource(R.drawable.vector_back),
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun TopNavigationBarPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        TopNavigationBar(
            title = R.string.settings,
            navigationBack = {},
            isNavigationBackVisible = false
        )
    }
}
