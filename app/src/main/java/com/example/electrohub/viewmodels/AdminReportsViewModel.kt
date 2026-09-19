package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Reports
import com.example.electrohub.repositories.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminReportsViewModel : ViewModel() {

    private val repository =
        ReportRepository()


    // =========================================================
    // REPORTS
    // =========================================================

    private val _reports =
        MutableStateFlow<List<Reports>>(
            emptyList()
        )

    val reports =
        _reports.asStateFlow()


    // =========================================================
    // LOADING
    // =========================================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading =
        _isLoading.asStateFlow()


    // =========================================================
    // ERROR
    // =========================================================

    private val _error =
        MutableStateFlow<String?>(null)

    val error =
        _error.asStateFlow()


    // =========================================================
    // LOAD REPORTS
    // =========================================================

    fun loadReports() {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                repository
                    .getReports()
                    .collect { reports ->

                        _reports.value =
                            reports

                        _isLoading.value =
                            false
                    }

            } catch (e: Exception) {

                _error.value =
                    e.message
                        ?: "Failed to load reports"

                _isLoading.value =
                    false
            }
        }
    }


    // =========================================================
    // DISMISS REPORT
    // =========================================================

    fun dismissReport(
        reportId: String
    ) {

        updateStatus(
            reportId,
            "dismissed"
        )
    }


    // =========================================================
    // RESOLVE REPORT
    // =========================================================

    fun resolveReport(
        reportId: String
    ) {

        updateStatus(
            reportId,
            "resolved"
        )
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    private fun updateStatus(
        reportId: String,
        status: String
    ) {

        viewModelScope.launch {

            repository.updateReportStatus(
                reportId,
                status
            )
        }
    }
}