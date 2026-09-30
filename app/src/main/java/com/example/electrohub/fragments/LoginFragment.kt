package com.example.electrohub.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.electrohub.R
import com.example.electrohub.databinding.FragmentLoginBinding
import com.example.electrohub.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var _binding: FragmentLoginBinding

    private val _viewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentLoginBinding.inflate(
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

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner
            ) {
                requireActivity().finish()
            }
    }


    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    _viewModel.isLoading.collect { isLoading ->

                        _binding.progressBar.visibility =
                            if (isLoading) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }

                        _binding.btnLogin.isEnabled =
                            !isLoading

                        _binding.txtRegister.isEnabled =
                            !isLoading

                        _binding.txtVForgotPassword.isEnabled =
                            !isLoading
                    }
                }


                launch {

                    _viewModel.loginResult.collect { success ->

                        when (success) {

                            true -> {

                                Toast.makeText(
                                    requireContext(),
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                _viewModel.getUsername()
                            }


                            false -> {

                                val message =
                                    _viewModel.loginMessage.value
                                        ?: "Invalid email or password."

                                Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }


                            null -> {
                            }
                        }
                    }
                }


                launch {

                    _viewModel.username.collect { username ->

                        if (
                            !_viewModel.isLoginSuccessful.value
                        ) {
                            return@collect
                        }

                        if (username.isEmpty()) {
                            return@collect
                        }

                        _viewModel.saveUsername(
                            requireContext(),
                            username
                        )

                        _viewModel.saveUserId(
                            requireContext(),
                            _viewModel.userId.value
                        )

                        if (
                            findNavController()
                                .currentDestination?.id
                            == R.id.LoginFragment
                        ) {

                            findNavController().navigate(
                                R.id.action_loginFragment_to_homeFragment
                            )
                        }
                    }
                }


                launch {

                    _viewModel.resetPasswordResult.collect { message ->

                        if (message != null) {

                            Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                            ).show()

                            _viewModel.clearResetPasswordResult()
                        }
                    }
                }
            }
        }
    }


    private fun setViews() {

        _binding.etxtPassword.setOnFocusChangeListener {

                _,
                hasFocus ->

            if (hasFocus) {

                _binding.laptopView
                    .setPasswordVisible(false)

            } else {

                _binding.laptopView
                    .setPasswordVisible(true)
            }
        }


        _binding.etxtPassword
            .addTextChangedListener {text ->

                val hasText = !text.isNullOrEmpty()

                if (
                    _binding.etxtPassword.hasFocus()
                ) {
                    _binding.laptopView.setPasswordVisible(!hasText)
                }
            }

        _binding.btnLogin.setOnClickListener {
            _viewModel.login(getEmail().trim(), getPassword())
        }


        _binding.txtRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


        _binding.txtVForgotPassword.setOnClickListener {
            val email = getEmail().trim()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your email first", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            _viewModel.resetPassword(email)
        }


        _binding.root.setOnTouchListener {
                _,
                _ ->

            _binding.laptopView.setPasswordVisible(true)

            _binding.etxtPassword.clearFocus()

            _binding.etxtEmail.clearFocus()

            false
        }
    }

    private fun getEmail(): String = _binding.etxtEmail.text.toString()
    private fun getPassword(): String = _binding.etxtPassword.text.toString()

    override fun onDestroyView() {
        super.onDestroyView()
    }
}