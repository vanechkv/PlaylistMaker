package com.example.playlistmaker.playlists.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.utils.DisplayUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : AddPlaylistFragment() {

    companion object {
        fun newInstance() = EditPlaylistFragment()
    }

    private val viewModel: EditPlaylistViewModel by viewModel()
    private val args: EditPlaylistFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.title.text = getString(R.string.edit)
        binding.buttonCreate.text = getString(R.string.save)

        val playlist = args.playlist
        imagePath = playlist.imagePath?.toUri()
        binding.nameEditText.setText(playlist.title)
        binding.descriptionEditText.setText(playlist.description)
        Glide.with(this)
            .load(imagePath)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(DisplayUtils.dpToPx(requireContext(), 8)))
            .into(binding.coverImage)
        binding.addImage.isVisible = false


        binding.buttonCreate.setOnClickListener {
            viewModel.updatePlaylist(
                args.playlist.copy(
                    title = binding.nameEditText.text.toString(),
                    description = binding.descriptionEditText.text.toString(),
                    imagePath = imagePath.toString()
                )
            )
        }

        viewModel.observeIsUpdated().observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
            }
        }
    }
}

