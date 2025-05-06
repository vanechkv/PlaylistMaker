package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator
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