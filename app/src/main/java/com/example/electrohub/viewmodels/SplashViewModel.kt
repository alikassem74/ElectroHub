package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.repositories.SplashRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _repository =
        SplashRepository()


    private val _userStatus =
        MutableStateFlow<Int?>(null)

    val userStatus: StateFlow<Int?> =
        _userStatus.asStateFlow()


    private var isChecked = false


    fun checkUser() {

        if (isChecked) {
            return
        }

        isChecked = true

        viewModelScope.launch {

            delay(2000)

            val result =
                _repository.checkUserLogin()

            _userStatus.value =
                result
        }
    }
}