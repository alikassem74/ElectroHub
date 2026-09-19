package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Reports
import com.example.electrohub.repositories.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val _repository = ReportRepository()

    // =========================================================
    // REPORT RESULT
    // =========================================================

    private val _reportResult =
        MutableStateFlow<Boolean?>(null)

    val reportResult =
        _reportResult.asStateFlow()


    // =========================================================
    // MESSAGE
    // =========================================================

    private val _message =
        MutableStateFlow<String?>(null)

    val message =
        _message.asStateFlow()


    // =========================================================
    // SUBMIT REPORT
    // =========================================================

    fun submitReport(
        report: Reports
    ) {

        viewModelScope.launch {

            val result =
                _repository.submitReport(report)

            if (result.isSuccess) {

                _reportResult.value = true

                _message.value =
                    "Report submitted successfully"

            } else {

                _reportResult.value = false

                _message.value =
                    result.exceptionOrNull()?.message
                        ?: "Failed to submit report"
            }
        }
    }
}