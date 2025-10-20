package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.settings.ui.compose.SettingsScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme(dynamicColor = false) {
                    SettingsScreen(viewModel = viewModel)
                }
            }
        }
        return composeView
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.shareFrameLayout.setOnClickListener {
//            viewModel.shareApp()
//        }
//
//        binding.supportFrameLayout.setOnClickListener {
//            viewModel.openSupport()
//        }
//
//        binding.userAgreementFrameLayout.setOnClickListener {
//            viewModel.openTerms()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        viewModel.getDarkTheme().observe(this) { isDarkTheme ->
//            binding.themeSwitcher.isChecked = isDarkTheme
//        }
//
//        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
//            viewModel.setDarkTheme(checked)
//        }
//    }
}