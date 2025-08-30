package com.example.playlistmaker.sharing.domain.api.repository

import com.example.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {

    fun shareLink(link: String)

    fun openLink(link: String)

    fun openEmail(emailData: EmailData)

    fun shareText(text: String)
}