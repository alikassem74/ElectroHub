package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.electrohub.databinding.FragmentSettingsBinding
import com.example.electrohub.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var _binding: FragmentSettingsBinding

    private val _viewModel: SettingsViewModel by viewModels()

    private var isLanguageLoaded = false
    private var isDarkModeLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )

        return _binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupLanguageSpinner()
        setupListeners()
        observeSettings()
        loadSettings()
    }

    // =========================================================
    // LANGUAGE SPINNER
    // =========================================================

    private fun setupLanguageSpinner() {

        val languages = listOf(
            "English",
            "العربية"
        )

        val languageCodes = listOf(
            "en",
            "ar"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )

        _binding.spinnerLanguage.adapter = adapter

        _binding.spinnerLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (!isLanguageLoaded) {
                        return
                    }

                    val selectedLanguage =
                        languageCodes[position]

                    _viewModel.saveLanguage(
                        requireContext(),
                        selectedLanguage
                    )
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>
                ) {
                }
            }
    }

    // =========================================================
    // OBSERVE SETTINGS
    // =========================================================

    private fun observeSettings() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                // LANGUAGE
                launch {

                    _viewModel.language.collect { language ->

                        language ?: return@collect

                        val position =
                            if (language == "ar") {
                                1
                            } else {
                                0
                            }

                        _binding.spinnerLanguage.setSelection(
                            position,
                            false
                        )

                        isLanguageLoaded = true
                    }
                }

                // DARK MODE
                launch {

                    _viewModel.darkMode.collect { enabled ->

                        enabled ?: return@collect

                        // Prevent the listener from firing
                        // while loading the saved value.
                        isDarkModeLoaded = false

                        _binding.switchDarkMode.isChecked =
                            enabled

                        isDarkModeLoaded = true
                    }
                }
            }
        }
    }

    // =========================================================
    // LOAD SETTINGS
    // =========================================================

    private fun loadSettings() {

        _viewModel.getLanguage(
            requireContext()
        )

        _viewModel.getDarkMode(
            requireContext()
        )
    }

    // =========================================================
    // LISTENERS
    // =========================================================

    private fun setupListeners() {

        _binding.btnBack.setOnClickListener {

            findNavController()
                .navigateUp()
        }

        _binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->

            if (!isDarkModeLoaded) {
                return@setOnCheckedChangeListener
            }

            _viewModel.saveDarkMode(
                requireContext(),
                isChecked
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}