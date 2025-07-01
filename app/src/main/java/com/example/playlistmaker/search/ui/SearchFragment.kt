package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ErrorViewBinding
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.databinding.HistoryViewBinding
import com.example.playlistmaker.search.domain.models.TracksState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance() = SearchFragment()
    }

    private val viewModel: SearchViewModel by viewModel()

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

    private var adapter = TrackAdapter(arrayListOf()) {
        viewModel.onTrackClick(it)
        if (clickDebounce()) openPlayer()
    }
    private val adapterHistory =
        TrackAdapter(arrayListOf()) {
            viewModel.onTrackClick(it)
            if (clickDebounce()) openPlayer()
        }

    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restoredText = savedInstanceState?.getString(SEARCH_TEXT)
        binding.searchEditText.setText(restoredText)

        viewModel.observeHistory().observe(viewLifecycleOwner) { history ->
            adapterHistory.updateTracks(history)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        val historyViewBinding =
            HistoryViewBinding.inflate(layoutInflater, binding.searchContainer, false)
        val historyView = historyViewBinding.root

        binding.searchCloseBtn.setOnClickListener {
            adapter.updateTracks(emptyList())
            binding.searchEditText.text = null

            val inputMethodManager =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

            binding.searchEditText.clearFocus()

            binding.searchContainer.removeAllViews()

        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(s?.toString().orEmpty())
                binding.searchCloseBtn.visibility = clearButtonVisibility(s)

                adapter.notifyDataSetChanged()

                val history = viewModel.observeHistory().value.orEmpty()

                if (binding.searchEditText.hasFocus() && s.isNullOrEmpty() && history.isNotEmpty()) {
                    adapterHistory.notifyDataSetChanged()
                    binding.searchContainer.addView(historyView)
                } else {
                    binding.searchContainer.removeAllViews()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }

        binding.searchEditText.addTextChangedListener(textWatcher)

        binding.searchRecyclerView.adapter = adapter

        historyViewBinding.historyRecyclerView.adapter = adapterHistory

        historyViewBinding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.searchContainer.removeAllViews()
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val history = viewModel.observeHistory().value.orEmpty()
            if (hasFocus && binding.searchEditText.text.isNullOrEmpty() && history.isNotEmpty()) {
                adapterHistory.notifyDataSetChanged()
                binding.searchContainer.addView(historyView)
            } else {
                binding.searchContainer.removeAllViews()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveHistory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, binding.searchEditText.text.toString())
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showError(
        text: String,
        explanation: String?,
        iconError: Int,
        isButtonVisible: Boolean = false
    ) {
        if (text.isNotEmpty()) {
            adapter.notifyDataSetChanged()

            binding.searchContainer.removeAllViews()

            val errorViewBinding =
                ErrorViewBinding.inflate(layoutInflater, binding.searchContainer, false)
            val errorView = errorViewBinding.root

            errorViewBinding.errorText.text = text

            if (!explanation.isNullOrEmpty()) {
                errorViewBinding.errorExplanation.text = explanation
                errorViewBinding.errorExplanation.visibility = View.VISIBLE
            } else {
                errorViewBinding.errorExplanation.visibility = View.GONE
            }

            errorViewBinding.errorIcon.setImageResource(iconError)

            if (isButtonVisible) {
                errorViewBinding.errorButton.visibility = View.VISIBLE
                errorViewBinding.errorButton.setOnClickListener {
                    binding.searchContainer.removeAllViews()
                    viewModel.searchDebounce(binding.searchEditText.text.toString())
                }
            } else {
                errorViewBinding.errorButton.visibility = View.GONE
            }

            binding.searchContainer.addView(errorView)
        }
    }

    private fun render(state: TracksState) {
        binding.progressBar.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchContainer.removeAllViews()

        when (state) {
            is TracksState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            is TracksState.Content -> {
                adapter.updateTracks(state.tracks)
                binding.searchRecyclerView.visibility = View.VISIBLE
            }

            is TracksState.Empty -> {
                showError(getString(state.message), null, R.drawable.vector_search_not_found, false)
            }

            is TracksState.Error -> {
                showError(
                    getString(state.errorMessage),
                    getString(R.string.internet_error_explanation),
                    R.drawable.vector_internet,
                    true
                )
            }

            is TracksState.SearchHistory -> {
                adapterHistory.updateTracks(state.history)
            }
        }
    }

    private fun openPlayer() {
        findNavController().navigate(R.id.action_searchFragment_to_audioPlayerActivity)
    }
}