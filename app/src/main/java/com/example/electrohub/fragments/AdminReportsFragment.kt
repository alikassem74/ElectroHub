package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electrohub.R
import com.example.electrohub.adapters.AdminReportsAdapter
import com.example.electrohub.databinding.FragmentAdminReportsBinding
import com.example.electrohub.models.Reports
import com.example.electrohub.viewmodels.AdminReportsViewModel
import kotlinx.coroutines.launch

class AdminReportsFragment : Fragment() {

    private lateinit var _binding: FragmentAdminReportsBinding

    private lateinit var _adapter: AdminReportsAdapter

    private val _viewModel: AdminReportsViewModel by viewModels()


    // =========================================================
    // CREATE VIEW
    // =========================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAdminReportsBinding.inflate(
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

        setupRecyclerView()

        setupViews()

        observeReports()

        _viewModel.loadReports()
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private fun setupRecyclerView() {

        _adapter =
            AdminReportsAdapter(
                emptyList()
            ) { report ->

                openReportDetails(
                    report
                )
            }


        _binding.rvReports.apply {

            adapter = _adapter

            layoutManager =
                LinearLayoutManager(
                    requireContext()
                )
        }
    }


    // =========================================================
    // OPEN REPORT DETAILS
    // =========================================================
    private fun openReportDetails(
        report: Reports
    ) {

        val bundle =
            Bundle().apply {

                putString(
                    "reportId",
                    report.reportId
                )
            }

        findNavController().navigate(
            R.id.action_AdminReportsFragment_to_AdminReportsDetailFragment,
            bundle
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
    }


    // =========================================================
    // OBSERVE REPORTS
    // =========================================================

    private fun observeReports() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    _viewModel.reports.collect { reports ->

                        _adapter.updateReports(
                            reports
                        )


                        _binding.txtVEmpty.visibility =
                            if (reports.isEmpty()) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                    }
                }


                launch {

                    _viewModel.isLoading.collect { loading ->

                        _binding.progressBar.visibility =
                            if (loading) {
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

        _binding.rvReports.adapter = null

        super.onDestroyView()
    }
}