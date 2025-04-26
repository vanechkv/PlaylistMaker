package com.example.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

class TracksSearchResponse(@SerializedName("results") val tracks: List<TrackDto>) : Response()