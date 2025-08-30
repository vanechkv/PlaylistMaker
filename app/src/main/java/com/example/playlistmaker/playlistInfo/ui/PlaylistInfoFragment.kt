package com.example.playlistmaker.playlistInfo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.playlistInfo.domain.models.PlaylistState
import com.example.playlistmaker.search.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.DisplayUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {

    private val viewModel: PlaylistInfoViewModel by viewModel()
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private val args: PlaylistInfoFragmentArgs by navArgs()
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

    private val adapter = PlaylistTrackAdapter(
        arrayListOf(), onTrackClick = {
            viewModel.onTrackClick(it)
            if (clickDebounce()) openPlayer()
        }, onTrackLongClick = {
            showDeleteTrackDialog(it)
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadData(args.playlistId)

        binding.tracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecycler.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val bottomSheetContainer = binding.bottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        val bottomSheetMenuContainer = binding.bottomSheetMenu
        val bottomSheetMenuBehavior = BottomSheetBehavior.from(bottomSheetMenuContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        val overlay = binding.overlay

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        overlay.isVisible = false
                    }

                    else -> {
                        overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

        })

        bottomSheetMenuBehavior.addBottomSheetCallback(object :
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

        binding.buttonMore.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.observeIsDeleted().observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent(playlist: Playlist, tracks: List<Track>) {
        setImage(playlist.imagePath, binding.cover, 0)
        binding.playlistName.text = playlist.title
        binding.playlistDescription.text = playlist.description
        var duration = tracks
            .mapNotNull { it.trackTimeMillis }
            .sum()
        sdf = SimpleDateFormat("mm", Locale.getDefault())
        duration = sdf.format(duration).toLong()
        binding.playlistTime.text =
            resources.getQuantityString(R.plurals.numberOfTimeAvailable, duration.toInt(), duration)
        binding.tracksCount.text = resources.getQuantityString(
            R.plurals.numberOfTracksAvailable,
            playlist.trackCount,
            playlist.trackCount
        )
        adapter.updateTracks(tracks)
        binding.playlist.apply {
            setImage(playlist.imagePath, artwork, 2)
            playlistName.text = playlist.title
            tracksCount.text = resources.getQuantityString(
                R.plurals.numberOfTracksAvailable,
                playlist.trackCount,
                playlist.trackCount
            )
        }
        binding.buttonShare.setOnClickListener {
            sharePlaylist(playlist, tracks)
        }

        binding.share.setOnClickListener {
            sharePlaylist(playlist, tracks)
        }

        binding.delete.setOnClickListener {
            showDeletePlaylistDialog(playlist)
        }

        binding.edit.setOnClickListener {
            val action =
                PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToEditPlaylistFragment(
                    playlist
                )
            findNavController().navigate(action)
        }
    }

    private fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        if (tracks.isEmpty()) {
            showToast(getString(R.string.there_is_no_track_list_in_this_playlist_that_can_be_shared))
            return
        }
        viewModel.sharePlaylist(playlist, tracks)
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

    private fun setImage(image: String?, view: ImageView, radius: Int) {
        if (radius == 0) {
            Glide.with(this)
                .load(image)
                .placeholder(R.drawable.placeholder)
                .transform(CenterCrop())
                .into(view)
        } else {
            Glide.with(this)
                .load(image)
                .placeholder(R.drawable.placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(DisplayUtils.dpToPx(requireContext(), radius))
                )
                .into(view)
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> Unit
            is PlaylistState.Content -> showContent(state.playlists, state.tracks)
            is PlaylistState.Loading -> Unit
        }
    }

    private fun openPlayer() {
        findNavController().navigate(R.id.action_playlistInfoFragment_to_audioPlayerFragment)
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle(getString(R.string.want_to_delete_track))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteTrack(args.playlistId, track)
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle(getString(R.string.want_to_delete_playlist, playlist.title))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deletePlaylist(playlist)
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        fun newInstance() = PlaylistInfoFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}