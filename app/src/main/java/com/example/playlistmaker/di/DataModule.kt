package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.storage.TracksHistoryStorage
import com.example.playlistmaker.settings.data.storage.SettingHistoryStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val PLAYLIST_PREFERENCES = "playlist_preferences"


val dataModule = module {
    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    factory {
        Gson()
    }

    single {
        MediaPlayer()
    }

    single {
        TracksHistoryStorage(get(), get())
    }

    single {
        SettingHistoryStorage(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }
}