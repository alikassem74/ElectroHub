package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.repositories.EmailVerificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel : ViewModel() {

    private val _repository =
        EmailVerificationRepository()

    private val _verificationResult =
        MutableStateFlow<Boolean?>(null)

    val verificationResult: StateFlow<Boolean?> =
        _verificationResult.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _emailSentResult =
        MutableStateFlow<Boolean?>(null)

    val emailSentResult: StateFlow<Boolean?> =
        _emailSentResult.asStateFlow()

    fun sendVerificationEmail() {

        if (_isLoading.value) {
            return
        }

        viewModelScope.launch {

            _isLoading.value = true
            _emailSentResult.value = null

            try {

                val result =
                    _repository.sendVerificationEmail()

                _emailSentResult.value =
                    result.isSuccess

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun checkEmailVerification() {

        if (_isLoading.value) {
            return
        }

        viewModelScope.launch {

            _isLoading.value = true
            _verificationResult.value = null

            try {

                val result =
                    _repository.isEmailVerified()

                _verificationResult.value =
                    if (result.isSuccess) {
                        result.getOrNull() ?: false
                    } else {
                        false
                    }

            } finally {

                _isLoading.value = false
            }
        }
    }
}

