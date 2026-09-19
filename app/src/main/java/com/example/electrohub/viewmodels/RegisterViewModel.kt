package com.example.electrohub.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.repositories.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository()
    private val _registerResult = MutableStateFlow<String?>(null)
    val registerResult = _registerResult.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun register(
        name: String,
        email: String,
        password: String,
        country: String,
        region: String
    ) {

        if (_isLoading.value) {
            return
        }

        viewModelScope.launch {

            _isLoading.value = true
            _registerResult.value = null

            try {

                val result =
                    repository.register(
                        name,
                        email,
                        password,
                        country,
                        region
                    )

                _registerResult.value =
                    result.getOrNull()

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun saveUsername(
        context: Context,
        username: String
    ) {

        viewModelScope.launch {

            repository.saveUsername(
                context,
                username
            )
        }
    }

    fun saveUserId(
        context: Context,
        userId: String
    ) {

        viewModelScope.launch {

            repository.saveUserId(
                context,
                userId
            )
        }
    }
}
