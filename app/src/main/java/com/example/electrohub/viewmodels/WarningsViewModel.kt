package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Warning
import com.example.electrohub.repositories.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WarningsViewModel : ViewModel() {

    private val repository =
        ReportRepository()


    // =========================================================
    // WARNINGS
    // =========================================================

    private val _warnings =
        MutableStateFlow<List<Warning>>(
            emptyList()
        )

    val warnings =
        _warnings.asStateFlow()


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
    // LOAD WARNINGS
    // =========================================================

    fun loadWarnings(
        userId: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                repository
                    .getWarnings(userId)
                    .collect { warnings ->

                        _warnings.value =
                            warnings

                        _isLoading.value =
                            false
                    }

            } catch (e: Exception) {

                _isLoading.value = false

                _error.value =
                    e.message
                        ?: "Failed to load warnings"
            }
        }
    }
}