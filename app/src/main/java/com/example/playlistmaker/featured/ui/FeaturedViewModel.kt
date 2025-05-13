package com.example.playlistmaker.featured.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.featured.domain.models.FeaturedState

class FeaturedViewModel(private val emptyMessage: String) : ViewModel() {

    private val stateLiveData = MutableLiveData<FeaturedState>()
    fun observeState() : LiveData<FeaturedState> = stateLiveData

    init {
        stateLiveData.postValue(FeaturedState.Empty(emptyMessage))
    }
}