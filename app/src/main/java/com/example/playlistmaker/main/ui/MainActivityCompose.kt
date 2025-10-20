//package com.example.playlistmaker.main.ui
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.core.view.WindowCompat
//import androidx.navigation.compose.rememberNavController
//import com.example.playlistmaker.main.ui.compose.MainScreen
//import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
//import com.example.playlistmaker.settings.ui.SettingsViewModel
//import org.koin.androidx.compose.koinViewModel
//
//class MainActivityCompose : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        enableEdgeToEdge()
//        setContent {
//            val navController = rememberNavController()
//            val settingsViewModel: SettingsViewModel = koinViewModel()
//            val isDarkTheme by settingsViewModel.getDarkTheme.collectAsState(false)
//            PlaylistMakerTheme(darkTheme = isDarkTheme, dynamicColor = false) {
//                MainScreen(navController, settingsViewModel, isDarkTheme)
//            }
//        }
//    }
//}