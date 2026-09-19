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
import androidx.navigation.fragment.findNavController
import com.example.electrohub.R
import com.example.electrohub.databinding.FragmentEmailVerificationBinding
import com.example.electrohub.viewmodels.EmailVerificationViewModel
import kotlinx.coroutines.launch

class EmailVerificationFragment : Fragment() {

    private lateinit var _binding: FragmentEmailVerificationBinding

    private val _viewModel: EmailVerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEmailVerificationBinding.inflate(
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

        setViews()
        setObservers()

        _viewModel.sendVerificationEmail()
    }

    private fun setViews() {

        _binding.btnCheckVerification.setOnClickListener {

            _viewModel.checkEmailVerification()
        }

        _binding.btnResendEmail.setOnClickListener {

            _viewModel.sendVerificationEmail()
        }
    }

    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    _viewModel.verificationResult.collect { result ->

                        if (result == null) {
                            return@collect
                        }

                        if (result) {

                            Toast.makeText(
                                requireContext(),
                                "Email verified successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().navigate(
                                R.id.action_EmailVerificationFragment_to_HomeFragment
                            )

                        } else {

                            Toast.makeText(
                                requireContext(),
                                "Email is not verified yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                launch {
                    _viewModel.isLoading.collect { isLoading ->

                        _binding.btnCheckVerification.isEnabled =
                            !isLoading

                        _binding.btnResendEmail.isEnabled =
                            !isLoading
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
