package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.electrohub.R
import com.example.electrohub.databinding.FragmentRegisterBinding
import com.example.electrohub.viewmodels.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var _binding: FragmentRegisterBinding

    private val _viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(
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
        setupLocationDropdowns()
        setObservers()
    }

    private fun setViews() {

        _binding.btnRegister.setOnClickListener {

            var valid = true
            val name = getName()
            val email = getEmail()
            val password = getPassword()
            val confirmPassword = getConfirmPassword()
            val country = getCountry()
            val region = getRegion()

            if (name.isEmpty()) {

                _binding.etxtName.error =
                    "Name is required"

                valid = false
            }

            if (email.isEmpty()) {

                _binding.etxtEmail.error =
                    "Email is required"

                valid = false
            }

            if (password.isEmpty()) {

                _binding.etxtPassword.error =
                    "Password is required"

                valid = false
            }

            if (confirmPassword != password) {

                _binding.etxtConfirmPassword.error =
                    "This doesn't match the password"

                valid = false
            }

            if (
                password.length < 8 ||
                !hasUppercase(password) ||
                !hasLowercase(password) ||
                !hasNumber(password) ||
                !hasSpecialCharacter(password)
            ) {

                _binding.etxtPassword.error =
                    "Invalid Password"

                valid = false
            }

            if (country.isEmpty()) {

                _binding.acTxtCountry.error =
                    "Country is required"

                valid = false
            }

            if (region.isEmpty()) {

                _binding.acTxtRegion.error =
                    "Region is required"

                valid = false
            }

            if (!valid) {
                return@setOnClickListener
            }

            _viewModel.register(
                name,
                email,
                password,
                country,
                region
            )
        }

        _binding.txtLogin.setOnClickListener {

            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment
            )
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

                        _binding.btnRegister.isEnabled =
                            !isLoading

                        _binding.txtLogin.isEnabled =
                            !isLoading
                    }
                }

                launch {

                    _viewModel.registerResult.collect { userId ->

                        if (userId == null) {
                            return@collect
                        }

                        Toast.makeText(
                            requireContext(),
                            "Account Created",
                            Toast.LENGTH_SHORT
                        ).show()

                        _viewModel.saveUsername(
                            requireContext(),
                            getName()
                        )

                        _viewModel.saveUserId(
                            requireContext(),
                            userId
                        )

                        findNavController().navigate(
                            R.id.action_RegisterFragment_to_EmailVerificationFragment
                        )
                    }
                }
            }
        }
    }

    private fun setupLocationDropdowns() {

        val countries =
            listOf(
                "Lebanon"
            )

        val regions =
            listOf(
                "Beirut",
                "Mount Lebanon",
                "North Lebanon",
                "Akkar",
                "Baalbek-Hermel",
                "Bekaa",
                "South Lebanon",
                "Nabatieh"
            )

        val countryAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                countries
            )

        val regionAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                regions
            )

        _binding.acTxtCountry.setAdapter(
            countryAdapter
        )

        _binding.acTxtRegion.setAdapter(
            regionAdapter
        )

        _binding.acTxtCountry.setText(
            "Lebanon",
            false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun getName(): String =
        _binding.etxtName.text
            .toString()
            .trim()

    private fun getPassword(): String =
        _binding.etxtPassword.text
            .toString()

    private fun getEmail(): String =
        _binding.etxtEmail.text
            .toString()
            .trim()

    private fun getConfirmPassword(): String =
        _binding.etxtConfirmPassword.text
            .toString()

    private fun getCountry(): String =
        _binding.acTxtCountry.text
            .toString()
            .trim()

    private fun getRegion(): String =
        _binding.acTxtRegion.text
            .toString()
            .trim()

    private fun hasUppercase(
        text: String
    ): Boolean {

        return text.any {
            it.isUpperCase()
        }
    }

    private fun hasLowercase(
        text: String
    ): Boolean {

        return text.any {
            it.isLowerCase()
        }
    }

    private fun hasNumber(
        text: String
    ): Boolean {

        return text.any {
            it.isDigit()
        }
    }

    private fun hasSpecialCharacter(
        text: String
    ): Boolean {

        return text.any {
            !it.isLetterOrDigit()
        }
    }
}

