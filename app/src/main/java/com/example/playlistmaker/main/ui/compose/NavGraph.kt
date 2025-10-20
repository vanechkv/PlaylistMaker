//package com.example.playlistmaker.main.ui.compose
//
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.padding
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.playlistmaker.main.domain.models.Routes
//import com.example.playlistmaker.search.ui.compose.SearchScreen
//import com.example.playlistmaker.settings.ui.SettingsViewModel
//import com.example.playlistmaker.settings.ui.compose.SettingsScreen
//
//@Composable
//fun NavGraph(
//    startDestination: String = Routes.Playlist.name,
//    navController: NavHostController,
//    innerPadding: PaddingValues,
//    settingsViewModel: SettingsViewModel,
//    isDarkTheme: Boolean
//) {
//
//    NavHost(
//        navController = navController,
//        startDestination = startDestination,
//        modifier = Modifier.padding(innerPadding)
//    ) {
//        composable(route = Routes.Playlist.name) {
//
//        }
//        composable(route = Routes.Search.name) {
//            SearchScreen(onItemClick = {navController.navigate(Routes.AudioPlayer.name)}, isDarkTheme = isDarkTheme)
//        }
//        composable(route = Routes.Settings.name) {
//            SettingsScreen(settingsViewModel)
//        }
//        composable(route = Routes.AudioPlayer.name) {
//            AudioPlayerScreen(isDarkTheme = isDarkTheme)
//        }
//    }
//}