package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.electrohub.R
import com.example.electrohub.databinding.FragmentProfileBinding
import com.example.electrohub.viewmodels.ProfileViewModel
import com.example.electrohub.views.LaptopView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding

    private val ADMIN_ID = "WnLBVcVojVfuzmDDKoNYpq8R1qn2"

    private val _viewModel: ProfileViewModel by viewModels()


    // =========================================================
    // IMAGE PICKER
    // =========================================================

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {

                _binding.imgVProfile.setImageURI(it)

                _viewModel.uploadProfileImage(it)
            }
        }


    // =========================================================
    // CREATE VIEW
    // =========================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentProfileBinding.inflate(
                inflater,
                container,
                false
            )

        return _binding.root
    }


    // =========================================================
    // VIEW CREATED
    // =========================================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        setViews()
        observeUser()

        _viewModel.loadUser()
        _viewModel.checkAdmin()
    }


    // =========================================================
    // VIEWS
    // =========================================================

    private fun setViews() {

        // -----------------------------------------------------
        // BACK
        // -----------------------------------------------------

        _binding.btnBack.setOnClickListener {

            findNavController()
                .navigateUp()
        }


        // -----------------------------------------------------
        // SAVED PRODUCTS
        // -----------------------------------------------------

        _binding.btnSaved.setOnClickListener {

            findNavController().navigate(
                R.id.action_ProfileFragment_to_SaveForLaterFragment
            )
        }


        // -----------------------------------------------------
        // MY PRODUCTS
        // -----------------------------------------------------

        _binding.btnMyProducts.setOnClickListener {

            findNavController().navigate(
                R.id.action_ProfileFragment_to_MyProductsFragment
            )
        }


        // -----------------------------------------------------
        // MY CHATS
        // -----------------------------------------------------

        _binding.btnMyChats.setOnClickListener {

            findNavController().navigate(
                R.id.action_ProfileFragment_to_MyChatsFragment
            )
        }


        // -----------------------------------------------------
        // MY WARNINGS
        // -----------------------------------------------------

        _binding.btnMyWarnings.setOnClickListener {

            findNavController().navigate(
                R.id.action_ProfileFragment_to_WarningsFragment
            )
        }


        // -----------------------------------------------------
        // PROFILE IMAGE
        // -----------------------------------------------------

        _binding.imgVProfile.setOnClickListener {

            imagePicker.launch("image/*")
        }


        // -----------------------------------------------------
        // SETTINGS
        // -----------------------------------------------------

        _binding.btnSettings.setOnClickListener {

            findNavController().navigate(
                R.id.action_ProfileFragment_to_SettingsFragment
            )
        }


        // -----------------------------------------------------
        // ADMIN REPORTS
        // -----------------------------------------------------

        _binding.btnAdminReports.setOnClickListener {

            findNavController().navigate(
                R.id.action_ProfileFragment_to_AdminReportsFragment
            )
        }


        // -----------------------------------------------------
        // LOGOUT LAPTOP ANIMATION
        // -----------------------------------------------------

        _binding.btnLogout.setOnClickListener {

            // Immediately disable logout so it
            // cannot be pressed multiple times.
            _binding.btnLogout.isEnabled = false

            _binding.laptopLogoutView.setMood(
                LaptopView.Mood.ERROR
            )

            viewLifecycleOwner.lifecycleScope.launch {

                delay(1000)

                _viewModel.logout(
                    requireContext()
                ) {

                    findNavController().navigate(
                        R.id.action_ProfileFragment_to_LoginFragment
                    )
                }
            }
        }
    }


    // =========================================================
    // PROFILE LOADING STATE
    // =========================================================

    private fun setProfileLoading(
        isLoading: Boolean
    ) {

        _binding.progressBar.visibility =
            if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

        _binding.btnBack.isEnabled =
            !isLoading

        _binding.btnSaved.isEnabled =
            !isLoading

        _binding.btnMyProducts.isEnabled =
            !isLoading

        _binding.btnMyChats.isEnabled =
            !isLoading

        _binding.btnMyWarnings.isEnabled =
            !isLoading

        _binding.btnSettings.isEnabled =
            !isLoading

        _binding.btnAdminReports.isEnabled =
            !isLoading

        _binding.btnLogout.isEnabled =
            !isLoading

        _binding.imgVProfile.isEnabled =
            !isLoading
    }


    // =========================================================
    // STATE FLOW OBSERVERS
    // =========================================================

    private fun observeUser() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                // -------------------------------------------------
                // LOADING
                // -------------------------------------------------

                launch {

                    _viewModel.isLoading
                        .collect { isLoading ->

                            setProfileLoading(
                                isLoading
                            )
                        }
                }


                // -------------------------------------------------
                // USER
                // -------------------------------------------------

                launch {

                    _viewModel.user
                        .collect { user ->

                            user?.let {

                                _binding.txtVUsername.text =
                                    it.name ?: ""

                                _binding.txtVEmail.text =
                                    it.email ?: ""


                                if (
                                    !it.profileImage
                                        .isNullOrEmpty()
                                ) {

                                    Glide.with(
                                        requireContext()
                                    )
                                        .load(
                                            it.profileImage
                                        )
                                        .into(
                                            _binding.imgVProfile
                                        )
                                }
                            }
                        }
                }


                // -------------------------------------------------
                // ADMIN
                // -------------------------------------------------

                launch {

                    _viewModel.isAdmin
                        .collect { isAdmin ->

                            _binding.btnAdminReports.visibility =
                                if (isAdmin) {
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }
                        }
                }
            }
        }
    }


    // =========================================================
    // DESTROY VIEW
    // =========================================================

    override fun onDestroyView() {

        super.onDestroyView()
    }
}
