package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData
import java.text.SimpleDateFormat
import java.util.Locale

class SharingInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator,
    private var sdf: SimpleDateFormat
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        val builder = StringBuilder()
        builder.appendLine(playlist.title)
        builder.appendLine(playlist.description)
        builder.appendLine(context.resources.getQuantityString(R.plurals.numberOfTracksAvailable, playlist.trackCount, playlist.trackCount))
        tracks.forEachIndexed { index, track ->
            sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
            val duration = sdf.format(track.trackTimeMillis)
            builder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${duration})")
        }
        externalNavigator.shareText(builder.toString())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.url_ya_praktikum_android_developer)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.support_email),
            context.getString(R.string.support_subject),
            context.getString(R.string.support_text)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.url_user_agreement)
    }
}