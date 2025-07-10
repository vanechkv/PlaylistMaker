package com.example.playlistmaker.playlists.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.search.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val context: Context
) : ViewModel() {

    private val isCreated = MutableLiveData<Boolean>()
    fun observeIsCreated(): LiveData<Boolean> = isCreated

    fun createPlaylist(title: String, description: String?, imagePath: Uri?) {
        viewModelScope.launch {
            val savedImagePath = imagePath?.let {
                saveImageToPrivateStorage(it)
            }

            val playlist = Playlist(
                playlistId = 0,
                title = title,
                description = description,
                imagePath = savedImagePath,
                trackIds = emptyList(),
                trackCount = 0
            )

            playlistInteractor.addPlaylist(playlist)
            isCreated.postValue(true)
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        val inputStream = context.contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.absolutePath
    }
}