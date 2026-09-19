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
import com.example.electrohub.databinding.FragmentAdminReportDetailsBinding
import com.example.electrohub.models.Reports
import com.example.electrohub.viewmodels.AdminReportDetailsViewModel
import kotlinx.coroutines.launch

class AdminReportDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentAdminReportDetailsBinding

    private var report: Reports? = null

    private val _viewModel:
            AdminReportDetailsViewModel by viewModels()


    // =========================================================
    // CREATE VIEW
    // =========================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAdminReportDetailsBinding.inflate(
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

        setupViews()

        observeReport()

        loadReport()
    }


    // =========================================================
    // LOAD REPORT
    // =========================================================

    private fun loadReport() {

        val reportId =
            arguments
                ?.getString("reportId")
                .orEmpty()


        if (reportId.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Report not found",
                Toast.LENGTH_SHORT
            ).show()

            findNavController()
                .popBackStack()

            return
        }


        _viewModel.loadReport(
            reportId
        )
    }


    // =========================================================
    // VIEWS
    // =========================================================

    private fun setupViews() {

        _binding.btnBack.setOnClickListener {

            findNavController()
                .popBackStack()
        }


        // =====================================================
        // DISMISS REPORT
        // =====================================================

        _binding.btnDismiss.setOnClickListener {

            report?.let {

                _viewModel.dismissReport(
                    it.reportId
                )
            }
        }


        // =====================================================
        // WARN REPORTER
        // =====================================================

        _binding.btnWarnReporter.setOnClickListener {

            report?.let {

                _viewModel.warnReporter(it.reporterId)
            }
        }


        // =====================================================
        // WARN PRODUCT OWNER
        // =====================================================

        _binding.btnWarnOwner.setOnClickListener {

            report?.let {

                _viewModel.warnSeller(
                    it.productOwnerId
                )
            }
        }


        // =====================================================
        // REMOVE PRODUCT
        // =====================================================

        _binding.btnRemoveProduct.setOnClickListener {

            _viewModel.product.value?.let { product ->

                _viewModel.removeProduct(
                    product
                )
            }
        }


        // =====================================================
        // BLOCK PRODUCT OWNER
        // =====================================================

        _binding.btnBlockUser.setOnClickListener {

            report?.let {

                _viewModel.blockUser(
                    it.productOwnerId
                )
            }
        }
    }


    // =========================================================
    // OBSERVE
    // =========================================================

    private fun observeReport() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                // =================================================
                // REPORT
                // =================================================

                launch {

                    _viewModel.report.collect { report ->

                        report?.let {

                            this@AdminReportDetailsFragment
                                .report = it

                            displayReport(it)


                            // Load actual product
                            _viewModel.getProductById(
                                it.productId
                            )
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


                // =================================================
                // MESSAGE
                // =================================================

                launch {

                    _viewModel.message.collect { message ->

                        message?.let {

                            Toast.makeText(
                                requireContext(),
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }


                // =================================================
                // ACTION COMPLETED
                // =================================================

                launch {

                    _viewModel.actionCompleted
                        .collect { completed ->

                            if (completed) {

                                findNavController()
                                    .popBackStack()
                            }
                        }
                }


                // =================================================
                // PRODUCT
                // =================================================

                launch {

                    _viewModel.product.collect { product ->

                        product?.let {

                            // Product is available
                        }
                    }
                }
            }
        }
    }


    // =========================================================
    // DISPLAY REPORT
    // =========================================================

    private fun displayReport(
        report: Reports
    ) {

        _binding.txtVProductName.text =
            report.productName


        _binding.txtVReason.text =
            report.reason


        _binding.txtVDescription.text =
            if (report.description.isEmpty()) {

                "No description provided"

            } else {

                report.description
            }


        _binding.txtVReporterId.text =
            report.reporterId


        _binding.txtVProductOwnerId.text =
            report.productOwnerId


        _binding.txtVStatus.text =
            report.status.uppercase()
    }


    // =========================================================
    // DESTROY VIEW
    // =========================================================

    override fun onDestroyView() {

        super.onDestroyView()
    }
}