package com.example.playlistmaker.featured.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ErrorViewBinding
import com.example.playlistmaker.databinding.FragmentFeaturedBinding
import com.example.playlistmaker.featured.domain.models.FeaturedState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FeaturedFragment : Fragment() {

    companion object {
        private const val EMPTY_MESSAGE = "empty_message"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeaturedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                is FeaturedState.Empty -> showEmpty(it.message)
            }
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
        binding.contentLayout.addView(errorView)
    }
}