package com.example.playlistmaker.main.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.main.domain.models.Screen
import com.example.playlistmaker.main.ui.ui.theme.KleinBlue
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val items = listOf(Screen.Search, Screen.Playlists, Screen.Settings)
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 0.dp
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = stringResource(id = screen.title)
                    )
                },
                label = {
                    Text(
                        stringResource(id = screen.title),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentDestination == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = KleinBlue,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = KleinBlue,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun BottomNavigationBarPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        val navController = rememberNavController()
        BottomNavigationBar(navController = navController)
    }
}