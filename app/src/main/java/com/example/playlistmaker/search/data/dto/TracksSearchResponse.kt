package com.example.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

class TracksSearchResponse(@SerializedName("results") val tracks: List<TrackDto>) : Response()