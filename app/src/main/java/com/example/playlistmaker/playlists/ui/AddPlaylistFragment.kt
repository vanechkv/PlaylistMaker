package com.example.playlistmaker.playlists.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AddPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = AddPlaylistFragment()
    }

    private val viewModel: AddPlaylistViewModel by viewModel()
    private lateinit var binding: FragmentAddPlaylistBinding
    private var imagePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            if (isFormEmpty()) {
                showExitConfirmationDialog()
            }else {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isFormEmpty()) {
                showExitConfirmationDialog()
            }else {
                findNavController().navigateUp()
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.placeholder)
                        .transform(CenterCrop(), RoundedCorners(dpToPx(8)))
                        .into(binding.coverImage)
                    imagePath = uri
                    binding.addImage.visibility = View.GONE
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.coverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.nameEditText.addTextChangedListener {
            val isNamePlaylistNotEmpty = it.toString().isNotBlank()
            binding.buttonCreate.isEnabled = isNamePlaylistNotEmpty
        }

        binding.buttonCreate.setOnClickListener {
            viewModel.createPlaylist(
                title = binding.nameEditText.text.toString(),
                description = binding.descriptionEditText.text.toString(),
                imagePath = imagePath
            )
        }

        viewModel.observeIsCreated().observe(viewLifecycleOwner) {
            if (it == true) {
                showToast()
                findNavController().navigateUp()
            }
        }
    }

    private fun showToast() {
        val layoutInflater = LayoutInflater.from(requireContext())
        val layout = layoutInflater.inflate(R.layout.toast_view, null)

        val textView = layout.findViewById<TextView>(R.id.toast_message)
        textView.text = getString(R.string.playlist_created, binding.nameEditText.text)

        Toast(requireContext()).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    private fun isFormEmpty(): Boolean {
        return binding.nameEditText.text?.isNotEmpty() == true ||
                binding.descriptionEditText.text?.isNotEmpty() == true ||
                imagePath != null
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}