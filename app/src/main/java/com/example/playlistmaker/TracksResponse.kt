package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

class TracksResponse (@SerializedName("results") val tracks: ArrayList<Track>)