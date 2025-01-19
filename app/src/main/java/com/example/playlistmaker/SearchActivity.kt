package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private var searchText: String? = null
    private val itunesBaseUrl = "https://itunes.apple.com"
    private lateinit var searchLayout: FrameLayout

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        searchLayout = findViewById(R.id.search_container)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val clearButton = findViewById<ImageView>(R.id.search_close_btn)
        inputEditText = findViewById(R.id.search_edit_text)

        clearButton.setOnClickListener {
            inputEditText.setText("")

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            inputEditText.clearFocus()

            searchLayout.removeAllViews()

            tracks.clear()
            adapter.notifyDataSetChanged()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s?.toString()
                tracks.clear()
                adapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }

        inputEditText.addTextChangedListener(textWatcher)

        val recyclerView = findViewById<RecyclerView>(R.id.search_recycler_view)
        recyclerView.adapter = adapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchLayout.removeAllViews()
                search()
                true
            }
            false
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT)
        inputEditText.setText(searchText)
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
            tracks.clear()
            adapter.notifyDataSetChanged()

            searchLayout.removeAllViews()

            val errorView =
                LayoutInflater.from(this).inflate(R.layout.error_view, searchLayout, false)
            val errorText = errorView.findViewById<TextView>(R.id.error_text)
            val errorExplanation = errorView.findViewById<TextView>(R.id.error_explanation)
            val errorIcon = errorView.findViewById<ImageView>(R.id.error_icon)
            val errorButton = errorView.findViewById<Button>(R.id.error_button)

            errorText.text = text

            if (!explanation.isNullOrEmpty()) {
                errorExplanation.text = explanation
                errorExplanation.visibility = View.VISIBLE
            } else {
                errorExplanation.visibility = View.GONE
            }

            errorIcon.setImageResource(iconError)


            if (isButtonVisible) {
                errorButton.visibility = View.VISIBLE
                errorButton.setOnClickListener {
                    search()
                }
            } else {
                errorButton.visibility = View.GONE
            }

            searchLayout.addView(errorView)
        }
    }

    private fun search() {
        itunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.tracks?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.tracks!!)
                                adapter.notifyDataSetChanged()
                            } else {
                                showError(
                                    getString(R.string.search_not_found),
                                    null,
                                    R.drawable.vector_search_not_found,
                                    false
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showError(
                        getString(R.string.internet_error),
                        getString(R.string.internet_error_explanation),
                        R.drawable.vector_internet,
                        true
                    )
                }
            })
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}