package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.ui.main.MainActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val shareFrameLayout = findViewById<FrameLayout>(R.id.share_frame_layout)
        shareFrameLayout.setOnClickListener {
            share()
        }

        val supportFrameLayout = findViewById<FrameLayout>(R.id.support_frame_layout)
        supportFrameLayout.setOnClickListener {
            contactSupport()
        }

        val userAgreementFrameLayout = findViewById<FrameLayout>(R.id.user_agreement_frame_layout)
        userAgreementFrameLayout.setOnClickListener {
            openUserAgreement()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)

        themeSwitcher.isChecked = settingsInteractor.isDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.setDarkTheme(checked)
        }
    }

    private fun share() {
        val shareText = getString(R.string.url_ya_praktikum_android_developer)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            setType("text/plain")
        }
        startActivity(shareIntent)
    }

    private fun contactSupport() {
        val supportText = getString(R.string.support_text)
        val supportSubject = getString(R.string.support_subject)
        val supportEmail = getString(R.string.support_email)
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, supportSubject)
            putExtra(Intent.EXTRA_TEXT, supportText)
        }
        startActivity(supportIntent)
    }

    private fun openUserAgreement() {
        val urlUserAgreement = getString(R.string.url_user_agreement)
        val userAgreementIntent = Intent(Intent.ACTION_VIEW)
        userAgreementIntent.apply {
            data = Uri.parse(urlUserAgreement)
        }
        startActivity(userAgreementIntent)
    }
}