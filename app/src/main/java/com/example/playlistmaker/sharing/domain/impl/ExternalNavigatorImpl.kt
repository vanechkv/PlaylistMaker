package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            putExtra(Intent.EXTRA_TEXT, link)
            setType("text/plain")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val userAgreementIntent = Intent(Intent.ACTION_VIEW)
        userAgreementIntent.apply {
            data = Uri.parse(link)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(userAgreementIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.targetEmail))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subjectEmail)
            putExtra(Intent.EXTRA_TEXT, emailData.message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(supportIntent)
    }

    override fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            putExtra(Intent.EXTRA_TEXT, text)
            setType("text/plain")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }
}