package com.example.playlistmaker.sharing.domain.models

data class EmailData(
    val targetEmail: String,
    val subjectEmail: String,
    val message: String
)
