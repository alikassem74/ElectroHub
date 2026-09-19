package com.example.electrohub.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.base.LanguageManager
import com.example.electrohub.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _repository =
        SettingsRepository()


    // =========================================================
    // LANGUAGE
    // =========================================================

    private val _language =
        MutableStateFlow<String?>(null)

    val language: StateFlow<String?> =
        _language.asStateFlow()


    fun saveLanguage(
        context: Context,
        language: String
    ) {

        viewModelScope.launch {

            val oldLanguage =
                _repository.getLanguage(context)


            if (oldLanguage == language) {
                return@launch
            }


            _repository.saveLanguage(
                context,
                language
            )


            LanguageManager.applyLanguage(
                context,
                language
            )


            (context as? Activity)?.recreate()
        }
    }


    fun getLanguage(
        context: Context
    ) {

        viewModelScope.launch {

            val result =
                _repository.getLanguage(context)

            _language.value =
                result
        }
    }


    // =========================================================
    // DARK MODE
    // =========================================================

    private val _darkMode =
        MutableStateFlow<Boolean?>(null)

    val darkMode: StateFlow<Boolean?> =
        _darkMode.asStateFlow()


    fun saveDarkMode(
        context: Context,
        enabled: Boolean
    ) {

        viewModelScope.launch {

            _repository.saveDarkMode(
                context,
                enabled
            )

            _repository.applyDarkMode(
                enabled
            )

            _darkMode.value =
                enabled
        }
    }


    fun getDarkMode(
        context: Context
    ) {

        viewModelScope.launch {

            val result =
                _repository.getDarkMode(context)

            _darkMode.value =
                result
        }
    }
}