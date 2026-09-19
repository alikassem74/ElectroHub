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
import com.example.electrohub.databinding.FragmentSplashBinding
import com.example.electrohub.viewmodels.SplashViewModel
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var _binding: FragmentSplashBinding

    private val _viewModel: SplashViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSplashBinding.inflate(
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

        checkLogIn()
    }


    private fun checkLogIn() {

        _viewModel.checkUser()

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                _viewModel.userStatus.collect { status ->

                    if (
                        findNavController()
                            .currentDestination?.id
                        != R.id.SplashFragment
                    ) {
                        return@collect
                    }

                    when (status) {

                        0 -> {

                            findNavController().navigate(
                                R.id.action_splashFragment_to_loginFragment
                            )
                        }

                        1 -> {

                            findNavController().navigate(
                                R.id.action_splashFragment_to_homeFragment
                            )
                        }

                        2 -> {

                            findNavController().navigate(
                                R.id.action_SplashFragment_to_EmailVerificationFragment
                            )
                        }

                        3 -> {

                            Toast.makeText(
                                requireContext(),
                                "Your account has been blocked by the administrator.",
                                Toast.LENGTH_LONG
                            ).show()

                            requireActivity().finish()
                        }

                        4 -> {

                            Toast.makeText(
                                requireContext(),
                                "Unable to check your account. Please try again.",
                                Toast.LENGTH_LONG
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