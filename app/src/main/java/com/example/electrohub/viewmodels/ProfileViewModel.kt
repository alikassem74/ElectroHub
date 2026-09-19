package com.example.electrohub.viewmodels

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.base.DataStoreManager
import com.example.electrohub.models.Users
import com.example.electrohub.repositories.ProfileRepository
import com.example.electrohub.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()
    private val userRepository = UsersRepository()
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    // USER
    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?> = _user.asStateFlow()

    // LOADING
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // LOAD USER
    fun loadUser() {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                userRepository.saveCurrentFcmToken()

                val user =
                    repository.getUser()

                _user.value =
                    user

                checkAdmin()

            } finally {

                _isLoading.value = false
            }
        }
    }

    // LOGOUT
    fun logout(
        context: Context,
        onComplete: () -> Unit
    ) {

        viewModelScope.launch {

            val success =
                repository.logout()

            if (success) {

                DataStoreManager.setLoggedIn(
                    context,
                    false,
                    booleanPreferencesKey(
                        DataStoreManager.PREF_KEY_LOGGED_IN
                    )
                )

                onComplete()
            }
        }
    }

    // UPLOAD PROFILE IMAGE
    fun uploadProfileImage(
        uri: Uri
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val imageUrl =
                    repository.uploadProfileImage(
                        uri
                    )

                if (imageUrl != null) {

                    val success =
                        repository.updateProfileImage(
                            imageUrl
                        )

                    if (success) {

                        val updatedUser =
                            repository.getUser()

                        _user.value =
                            updatedUser
                    }
                }

            } finally {

                _isLoading.value = false
            }
        }
    }

    // CHECK ADMIN
    fun checkAdmin() {

        val currentUserId =
            userRepository.getCurrentUserId()

        _isAdmin.value =
            currentUserId.isNotEmpty() &&
                    currentUserId == "WnLBVcVojVfuzmDDKoNYpq8R1qn2"
    }
}