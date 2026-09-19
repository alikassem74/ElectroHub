package com.example.electrohub.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.repositories.AccountBlockedException
import com.example.electrohub.repositories.EmailNotVerifiedException
import com.example.electrohub.repositories.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository =
        LoginRepository()


    private val _loginResult =
        MutableStateFlow<Boolean?>(null)

    val loginResult =
        _loginResult.asStateFlow()


    private val _loginMessage =
        MutableStateFlow<String?>(null)

    val loginMessage =
        _loginMessage.asStateFlow()


    private val _username =
        MutableStateFlow("")

    val username =
        _username.asStateFlow()


    private val _userId =
        MutableStateFlow("")

    val userId =
        _userId.asStateFlow()


    private val _isLoginSuccessful =
        MutableStateFlow(false)

    val isLoginSuccessful =
        _isLoginSuccessful.asStateFlow()


    private val _isLoading =
        MutableStateFlow(false)

    val isLoading =
        _isLoading.asStateFlow()


    private val _resetPasswordResult =
        MutableStateFlow<String?>(null)

    val resetPasswordResult =
        _resetPasswordResult.asStateFlow()


    fun login(
        email: String,
        password: String
    ) {

        if (_isLoading.value) {
            return
        }

        viewModelScope.launch {

            _isLoading.value = true

            _loginResult.value = null
            _loginMessage.value = null
            _isLoginSuccessful.value = false
            _username.value = ""
            _userId.value = ""


            val result =
                repository.login(
                    email,
                    password
                )


            if (result.isSuccess) {

                _loginResult.value = true
                _isLoginSuccessful.value = true

                _userId.value =
                    repository.getCurrentUserId()

            } else {

                _loginResult.value = false

                _loginMessage.value =
                    when (result.exceptionOrNull()) {

                        is EmailNotVerifiedException ->
                            "Please verify your email before logging in."

                        is AccountBlockedException ->
                            "Your account has been blocked."

                        else ->
                            "Invalid email or password."
                    }
            }


            _isLoading.value = false
        }
    }


    fun getUsername() {

        viewModelScope.launch {

            val username =
                repository.getUsername()

            _username.value =
                username
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


    fun resetPassword(
        email: String
    ) {

        if (_isLoading.value) {
            return
        }

        viewModelScope.launch {

            val result =
                repository.resetPassword(
                    email
                )


            if (result.isSuccess) {

                _resetPasswordResult.value =
                    "Reset email sent"

            } else {

                _resetPasswordResult.value =
                    result
                        .exceptionOrNull()
                        ?.message
                        ?: "Error sending reset email"
            }
        }
    }


    fun clearResetPasswordResult() {

        _resetPasswordResult.value = null
    }
}