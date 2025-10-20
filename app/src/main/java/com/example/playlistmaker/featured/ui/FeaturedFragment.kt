package com.example.playlistmaker.featured.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ErrorViewBinding
import com.example.playlistmaker.databinding.FragmentFeaturedBinding
import com.example.playlistmaker.featured.domain.models.FeaturedState
import com.example.playlistmaker.featured.ui.compose.FeaturedScreen
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.compose.SearchScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FeaturedFragment : Fragment() {

    companion object {
        private const val EMPTY_MESSAGE = "empty_message"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance(emptyMessage: String) = FeaturedFragment().apply {
            arguments = Bundle().apply {
                putString(EMPTY_MESSAGE, emptyMessage)
            }
        }
    }

    private val viewModel: FeaturedViewModel by viewModel {
        parametersOf(requireArguments().getString(EMPTY_MESSAGE))
    }

    private lateinit var binding: FragmentFeaturedBinding

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

//    private var adapter = TrackAdapter(arrayListOf()) {
//        viewModel.onTrackClick(it)
//        if (clickDebounce()) openPlayer()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val isDarkTheme = currentMode == AppCompatDelegate.MODE_NIGHT_YES
        val composeView = ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme(dynamicColor = false) {
                    FeaturedScreen(
                        viewModel = viewModel,
                        onItemClick = {
                            findNavController().navigate(R.id.action_playlistFragment_to_audioPlayerFragment)
                        },
                        isDarkTheme = isDarkTheme,
                        emptyMessage = requireArguments().getString(EMPTY_MESSAGE) ?: ""
                    )
                }
            }
        }
        return composeView
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.featuredRecycler.adapter = adapter
//
//        viewModel.observeState().observe(viewLifecycleOwner) {
//            render(it)
//        }
//    }
//
//    private fun showEmpty(message: String) {
//        val errorViewBinding =
//            ErrorViewBinding.inflate(layoutInflater, binding.contentLayout, false)
//        val errorView = errorViewBinding.root
//
//        errorViewBinding.apply {
//            errorText.text = message
//            errorIcon.setImageResource(R.drawable.vector_search_not_found)
//        }
//        binding.contentLayout.isVisible = true
//        binding.featuredRecycler.isVisible = false
//        binding.contentLayout.addView(errorView)
//    }
//
//    private fun showContent(tracks: List<Track>) {
//        binding.contentLayout.isVisible = false
//        binding.featuredRecycler.isVisible = true
//        adapter.updateTracks(tracks)
//    }
//
//    private fun render(state: FeaturedState) {
//        when (state) {
//            is FeaturedState.Empty -> showEmpty(state.message)
//            is FeaturedState.Content -> showContent(state.tracks)
//            is FeaturedState.Loading -> {}
//        }
//    }
//
//    private fun openPlayer() {
//        findNavController().navigate(R.id.action_playlistFragment_to_audioPlayerFragment)
//    }
}