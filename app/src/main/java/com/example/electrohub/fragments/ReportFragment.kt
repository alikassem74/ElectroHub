package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.electrohub.databinding.FragmentReportBinding
import com.example.electrohub.models.Reports
import com.example.electrohub.viewmodels.ReportViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {

    private lateinit var _binding: FragmentReportBinding

    private var productId = ""
    private var sellerId = ""
    private var productName = ""

    private val _viewModel: ReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportBinding.inflate(
            inflater,
            container,
            false
        )

        ViewCompat.setOnApplyWindowInsetsListener(
            _binding.root
        ) { _, insets ->

            val keyboardHeight =
                insets.getInsets(
                    WindowInsetsCompat.Type.ime()
                ).bottom

            _binding.etxtDescription.translationY =
                -keyboardHeight.toFloat()

            insets
        }

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

        productId =
            arguments?.getString("productId").orEmpty()

        sellerId =
            arguments?.getString("sellerId").orEmpty()

        productName =
            arguments?.getString("productName").orEmpty()

        setViews()
        setObservers()
    }

    // =========================================================
    // VIEWS
    // =========================================================

    private fun setViews() {

        _binding.btnCancel.setOnClickListener {

            findNavController().popBackStack()
        }

        _binding.btnSubmitReport.setOnClickListener {

            submitReport()
        }
    }

    // =========================================================
    // SUBMIT REPORT
    // =========================================================

    private fun submitReport() {

        val reason = getSelectedReason()

        if (reason.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please select a reason",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val report = Reports(

            reporterId = "",

            productId = productId,

            productOwnerId = sellerId,

            productName = productName,

            reason = reason,

            description = getDescription(),

            timestamp = Timestamp.now(),

            status = "pending"
        )

        _viewModel.submitReport(report)
    }

    // =========================================================
    // GET SELECTED REASON
    // =========================================================

    private fun getSelectedReason(): String {

        return when {

            _binding.rbWrongCategory.isChecked ->
                "Wrong Category"

            _binding.rbFakeProduct.isChecked ->
                "Fake Product"

            _binding.rbWrongInfo.isChecked ->
                "Misleading Information"

            _binding.rbImages.isChecked ->
                "Inappropriate Images"

            _binding.rbSpam.isChecked ->
                "Spam"

            _binding.rbScam.isChecked ->
                "Scam/Fraud"

            _binding.rbOther.isChecked -> {

                if (getDescription().isEmpty()) {

                    Toast.makeText(
                        requireContext(),
                        "Please explain your report reason",
                        Toast.LENGTH_SHORT
                    ).show()

                    ""
                } else {
                    "Other"
                }
            }

            else -> ""
        }
    }

    // =========================================================
    // OBSERVERS
    // =========================================================

    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    _viewModel.reportResult.collect { success ->

                        if (success == true) {

                            Toast.makeText(
                                requireContext(),
                                "Report submitted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().popBackStack()
                        }
                    }
                }

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
            }
        }
    }

    // =========================================================
    // DESCRIPTION
    // =========================================================

    private fun getDescription(): String {

        return _binding.etxtDescription
            .text
            .toString()
            .trim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}