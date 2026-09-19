package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electrohub.adapters.WarningsAdapter
import com.example.electrohub.base.DataStoreManager
import com.example.electrohub.databinding.FragmentWarningsBinding
import com.example.electrohub.viewmodels.WarningsViewModel
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

class WarningsFragment : Fragment() {

    private lateinit var _binding: FragmentWarningsBinding

    private lateinit var _warningsAdapter: WarningsAdapter

    private val _viewModel: WarningsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentWarningsBinding.inflate(
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

        setView()
        setupRecyclerView()
        observeWarnings()
        loadWarnings()
    }

    private fun setView() {
        _binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private fun setupRecyclerView() {

        _warningsAdapter =
            WarningsAdapter(
                emptyList()
            )

        _binding.rvWarnings.apply {

            adapter =
                _warningsAdapter

            layoutManager =
                LinearLayoutManager(
                    requireContext()
                )
        }
    }


    // =========================================================
    // LOAD WARNINGS
    // =========================================================

    private fun loadWarnings() {

        viewLifecycleOwner.lifecycleScope.launch {

            val userId =
                DataStoreManager.getUserId(
                    requireContext(),
                    androidx.datastore.preferences.core
                        .stringPreferencesKey(
                            DataStoreManager.PREF_KEY_ID
                        )
                )

            if (userId.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "User not found",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            _viewModel.loadWarnings(
                userId
            )
        }
    }


    // =========================================================
    // OBSERVE
    // =========================================================

    private fun observeWarnings() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                // =================================================
                // WARNINGS
                // =================================================

                launch {

                    _viewModel.warnings.collect { warnings ->

                        _warningsAdapter.updateWarnings(
                            warnings
                        )

                        _binding.txtVNoWarnings.visibility =
                            if (warnings.isEmpty()) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                    }
                }
                // =================================================
                // ERROR
                // =================================================

                launch {

                    _viewModel.error.collect { error ->

                        error?.let {

                            Toast.makeText(
                                requireContext(),
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {

        super.onDestroyView()
    }
}