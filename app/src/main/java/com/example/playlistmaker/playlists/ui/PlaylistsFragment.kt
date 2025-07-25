package com.example.playlistmaker.playlists.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ErrorViewBinding
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.playlist.ui.PlaylistFragmentDirections
import com.example.playlistmaker.playlists.domain.models.PlaylistsState
import com.example.playlistmaker.search.domain.models.Playlist
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_MESSAGE = "empty_message"

        fun newInstance(emptyMessage: String) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(EMPTY_MESSAGE, emptyMessage)
            }
        }
    }

    private val viewModel: PlaylistsViewModel by viewModel {
        parametersOf(requireArguments().getString(EMPTY_MESSAGE))
    }

    private lateinit var binding: FragmentPlaylistsBinding

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

    private val adapter = PlaylistsAdapter(arrayListOf()) {
        if (clickDebounce()) openPlaylistInfo(it.playlistId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.playlistsRecycler.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_playlistFragment_to_addPlaylistFragment)
        }
    }

    private fun showEmpty(message: String) {
        val errorViewBinding =
            ErrorViewBinding.inflate(layoutInflater, binding.contentLayout, false)
        val errorView = errorViewBinding.root

        errorViewBinding.apply {
            errorText.text = message
            errorIcon.setImageResource(R.drawable.vector_search_not_found)
        }
        binding.contentLayout.isVisible = true
        binding.playlistsRecycler.isVisible = false
        binding.contentLayout.addView(errorView)
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.contentLayout.isVisible = false
        binding.playlistsRecycler.isVisible = true
        adapter.updatePlaylists(playlists)
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty(state.message)
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Loading -> {}
        }
    }

    private fun openPlaylistInfo(playlistId: Long) {
        val direction = PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistInfoFragment(playlistId)
        findNavController().navigate(direction)
    }
}