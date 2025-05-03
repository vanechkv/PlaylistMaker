package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.constants.Constants.SEARCH_TEXT
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.databinding.ErrorViewBinding
import com.example.playlistmaker.databinding.HistoryViewBinding
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.search.domain.models.TracksState

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
    }

    private var adapter = TrackAdapter(arrayListOf()) {
        viewModel.onTrackClick(it)
        openPlayer()
    }
    private val adapterHistory =
        TrackAdapter(arrayListOf()) {
            viewModel.onTrackClick(it)
            openPlayer()
        }

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        viewModel.observeHistory().observe(this) { history ->
            adapterHistory.updateTracks(history)
        }

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        val historyViewBinding =
            HistoryViewBinding.inflate(layoutInflater, binding.searchContainer, false)
        val historyView = historyViewBinding.root

        binding.backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        binding.searchCloseBtn.setOnClickListener {
            binding.searchEditText.text = null

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchEditText.setText(savedInstanceState.getString(SEARCH_TEXT))
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
                showError(state.message, null, R.drawable.vector_search_not_found, false)
            }

            is TracksState.Error -> {
                showError(
                    state.errorMessage,
                    getString(R.string.internet_error_explanation),
                    R.drawable.vector_internet,
                    true
                )
            }
        }
    }

    private fun openPlayer() {
        startActivity(Intent(this, AudioPlayerActivity::class.java))
    }
}