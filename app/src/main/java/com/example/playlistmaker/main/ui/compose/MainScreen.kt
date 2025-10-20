//package com.example.playlistmaker.main.ui.compose
//
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.example.playlistmaker.R
//import com.example.playlistmaker.main.domain.models.Routes
//import com.example.playlistmaker.settings.ui.SettingsViewModel
//
//@Composable
//fun MainScreen(
//    navController: NavHostController,
//    settingsViewModel: SettingsViewModel,
//    isDarkTheme: Boolean
//) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    val titleTopBar = when (currentRoute) {
//        Routes.Playlist.name -> R.string.playlist
//        Routes.Search.name -> R.string.search
//        Routes.Settings.name -> R.string.settings
//        else -> null
//    }
//    val showBottomBar = when (currentRoute) {
//        Routes.Playlist.name -> true
//        Routes.Search.name -> true
//        Routes.Settings.name -> true
//        else -> false
//    }
//
//    Scaffold(
//        containerColor = MaterialTheme.colorScheme.primary,
//        topBar = {
//            TopNavigationBar(
//                title = titleTopBar,
//                navigationBack = { navController.popBackStack() },
//                isNavigationBackVisible =
//                    when (currentRoute) {
//                        Routes.Playlist.name -> false
//                        Routes.Search.name -> false
//                        Routes.Settings.name -> false
//                        else -> true
//                    }
//            )
//        },
//        bottomBar = {
//            if (showBottomBar) BottomNavigationBar(navController = navController)
//        },
//    ) { innerPadding ->
//        NavGraph(
//            navController = navController,
//            innerPadding = innerPadding,
//            settingsViewModel = settingsViewModel,
//            isDarkTheme = isDarkTheme
//        )
//    }
//}
