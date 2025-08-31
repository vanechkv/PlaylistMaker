package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.playlists.domain.models.PlaylistsState
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.utils.DisplayUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = AudioPlayerFragment()
    }

    private val viewModel: AudioPlayerViewModel by viewModel()
    private lateinit var binding: FragmentAudioPlayerBinding
    private lateinit var sdf: SimpleDateFormat

    private var isClickAllowed = true

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private val adapter = PlaylistsBottomSheetAdapter(arrayListOf()) {
        if (clickDebounce()) viewModel.onPlaylistClicked(viewModel.getTrack(), it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sdf = SimpleDateFormat("mm:ss", Locale.getDefault())

        val track = viewModel.getTrack()

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = sdf.format(track.trackTimeMillis)
        binding.albumName.text = track.collectionName ?: "-"
        binding.trackYear.text = track.releaseDate ?: "-"
        binding.trackGenre.text = track.primaryGenreName ?: "-"
        binding.trackCountry.text = track.country ?: "-"
        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(DisplayUtils.dpToPx(requireContext(), 8)))
            .into(binding.cover)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.buttonPlay.isEnabled = it.isPlayButtonEnabled
            when (it) {
                is PlayerState.Playing -> {
                    binding.buttonPlay.setPlaying(true)
                }
                is PlayerState.Paused -> {
                    binding.buttonPlay.setPlaying(false)
                }
                is PlayerState.Prepared -> {
                    binding.buttonPlay.setPlaying(false)
                }
                is PlayerState.Default -> {
                    binding.buttonPlay.setPlaying(false)
                }
            }
            binding.listeningTime.text = it.progress
        }

        viewModel.observeIsFavorite().observe(viewLifecycleOwner) { isFavorite ->
            val icon =
                if (isFavorite) R.drawable.button_added_to_favorite else R.drawable.button_add_to_favorites
            binding.buttonAddToFavorite.setImageResource(icon)
        }

        binding.buttonAddToFavorite.setOnClickListener {
            viewModel.onButtonFavoriteClick()
        }

        val bottomSheetContainer = binding.bottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val overlay = binding.overlay

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.isVisible = false
                    }

                    else -> {
                        overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

        })

        binding.buttonAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_addPlaylistFragment)
        }

        binding.playlistRecycler.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeAddStatus().observe(viewLifecycleOwner) {
            if (it.second) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showToast(getString(R.string.added_to_playlist, it.first))
            } else {
                showToast(
                    getString(
                        R.string.the_track_has_already_been_added_to_the_playlist,
                        it.first
                    )
                )
            }
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.playlistRecycler.isVisible = true
        adapter.updatePlaylists(playlists)
    }

    private fun showToast(message: String) {
        val layoutInflater = LayoutInflater.from(requireContext())
        val layout = layoutInflater.inflate(R.layout.toast_view, null)

        val textView = layout.findViewById<TextView>(R.id.toast_message)
        textView.text = message

        Toast(requireContext()).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> Unit
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Loading -> Unit
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}