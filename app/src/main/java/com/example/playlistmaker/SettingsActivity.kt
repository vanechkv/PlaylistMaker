package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        app = applicationContext as App

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val shareFrameLayout = findViewById<FrameLayout>(R.id.share_frame_layout)

        shareFrameLayout.setOnClickListener {
            val shareText = getString(R.string.url_ya_praktikum_android_developer)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.apply {
                putExtra(Intent.EXTRA_TEXT, shareText)
                setType("text/plain")
            }
            startActivity(shareIntent)
        }

        val supportFrameLayout = findViewById<FrameLayout>(R.id.support_frame_layout)

        supportFrameLayout.setOnClickListener {
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

        val userAgreementFrameLayout = findViewById<FrameLayout>(R.id.user_agreement_frame_layout)

        userAgreementFrameLayout.setOnClickListener {
            val urlUserAgreement = getString(R.string.url_user_agreement)
            val userAgreementIntent = Intent(Intent.ACTION_VIEW)
            userAgreementIntent.apply {
                data = Uri.parse(urlUserAgreement)
            }
            startActivity(userAgreementIntent)
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

        themeSwitcher.isChecked = app.darkTheme

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            app.switchTheme(checked)
        }
    }
}